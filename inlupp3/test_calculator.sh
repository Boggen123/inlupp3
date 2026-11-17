#!/usr/bin/env bash
#
# End-to-end-test av hela kalkylatorn (huvudloop + parser + AST).
#
# Skickar in alla testrader i kalkylatorn, plockar isär svaren och jämför dem
# med förväntat resultat. Programmet körs via kommandot `script`, som ger det
# en pseudo-terminal. Det behövs eftersom System.console() är null när indata
# kommer från en vanlig pipe.
#
# Kör från projektets rotmapp (bredvid Makefile):
#     bash test_calculator.sh
#
# ANPASSA vid behov:
#   PROMPT   - exakt text som kalkylatorn skriver som prompt (standard "? ")
#   CALC_CMD - kommandot som startar kalkylatorn (miljövariabel)
#   listan med testfall (raderna som börjar med  t "...")  längre ned

PROMPT=""
CMD="${CALC_CMD:-java -cp classes org.ioopm.calculator.Calculator}"
TIMEOUT_SECS=30      # max tid för hela körningen
STARTUP_DELAY=1      # sekunder innan indata skickas (så att eko hinner stängas av)

# ----------------------------------------------------------------------
# Testfall:  t "indata" typ "förväntat"
#   exact : svaret ska vara exakt detta
#   has   : svaret ska innehålla detta (skiftlägesokänsligt)
#   any   : inget krav på svaret, men programmet får inte krascha
# ----------------------------------------------------------------------
INPUTS=(); KINDS=(); EXPECTED=()
t() { INPUTS+=("$1"); KINDS+=("$2"); EXPECTED+=("$3"); }

# --- Exempel ur uppgiften ---
t "2*(5-2)"                  exact "6.0"
t "1/(2+8) = a"              exact "0.1"
t "2 = x = y"                exact "2.0"
t "(x+y=z) - (z-1)*(1=x)"    exact "1.0"
t "ans"                      exact "1.0"
t "ans*10"                   exact "10.0"
t "-ans+2"                   exact "-8.0"
t "- - -x"                   exact "-1.0"

# --- Okända variabler och uttryck som värden ---
t "b"                        exact "b"
t "1 = a"                    exact "1.0"
t "a + b"                    exact "1.0 + b"
t "b + a = c"                exact "b + 1.0"
t "2 = b"                    exact "2.0"
t "c"                        exact "b + 1.0"      # ändras inte av 2 = b
t "2*q = w"                  exact "2.0 * q"

# --- Kommandot vars ---
t "vars"                     has   "x=1.0"

# --- Syntaxfel: inga krascher, programmet lever vidare ---
t "3**5 ++ x"                has   "syntax error"
t "foo(x+y)"                 has   "syntax error"
t "1 = quit"                 has   "syntax error"
t "1 +"                      has   "syntax error"
t "(1 + 2"                   has   "syntax error"
t "1+1"                      exact "2.0"

# --- Kantfall i talen ---
t "1/0"                      exact "Infinity"
t "   2  +   3   "           exact "5.0"

# --- Namngivna konstanter ---
t "Answer + Answer"          exact "84.0"
t "2*pi"                     exact "6.283185307179586"
t "43 = Answer"              has   "cannot redefine named constant"
t "1 = pi"                   has   "cannot redefine"

# --- Misslyckad tilldelning får inte ändra något ---
t "5 = k"                    exact "5.0"
t "(7=m)+(1=pi)"             has   "cannot redefine"
t "m"                        exact "m"            # återställd
t "k"                        exact "5.0"          # orörd

# --- Skiftlägeskänsliga namn ---
t "2 = X"                    exact "2.0"
t "x"                        exact "1.0"

# --- clear och tom rad ---
t "clear"                    any   ""
t "x"                        exact "x"
t "vars"                     any   ""
t ""                         any   ""
t "1+1"                      exact "2.0"          # lever efter tom rad

# ----------------------------------------------------------------------
# Hjälpfunktioner
# ----------------------------------------------------------------------
passed=0
failed=0
ok()   { passed=$((passed + 1)); }
fail() { failed=$((failed + 1)); printf 'FAILED %s: %s\n' "$1" "$2"; }
trim() {
    local s=$1
    s="${s#"${s%%[![:space:]]*}"}"
    s="${s%"${s##*[![:space:]]}"}"
    printf '%s' "$s"
}

if ! command -v script >/dev/null 2>&1; then
    echo "Kommandot 'script' saknas (paketet util-linux/bsdutils)."
    exit 1
fi

# Bygg först, om vi inte testar ett eget kommando
if [[ -z "${CALC_CMD:-}" ]]; then
    if ! make -s; then
        echo "make misslyckades, åtgärda kompileringsfelen först."
        exit 1
    fi
fi

# ----------------------------------------------------------------------
# Kör kalkylatorn
# ----------------------------------------------------------------------
export TERM=dumb
output=$(
    { sleep "$STARTUP_DELAY"; printf '%s\n' "${INPUTS[@]}" quit; } \
    | timeout "$TIMEOUT_SECS" script -qec "stty -echo; exec $CMD" /dev/null 2>&1
)
code=$?

if (( code == 124 )); then
    echo "FAILED: programmet svarade inte inom ${TIMEOUT_SECS} s (hänger det?)."
    echo "Utdata hittills:"
    echo "$output"
    exit 1
fi

# Städa: radslut från terminalen och eventuella escape-sekvenser
output=${output//$'\r'/}
output=$(printf '%s' "$output" | sed -E 's/\x1b\[[0-9;?]*[ -\/]*[@-~]//g')

# ----------------------------------------------------------------------
# Dela upp utdata i ett svar per prompt
#   Utdata ser ut som:  <hälsning> "? " <svar 1> "? " <svar 2> ...
# ----------------------------------------------------------------------
records=()
idx=-1
greeting=""
while IFS= read -r line || [[ -n "$line" ]]; do
    if (( idx < 0 )) && [[ $line != "$PROMPT"* ]]; then
        greeting+="$line "
        continue
    fi
    while [[ $line == "$PROMPT"* ]]; do
        idx=$((idx + 1))
        records[idx]=""
        line=${line#"$PROMPT"}
    done
    if [[ -n $line ]]; then
        [[ -n ${records[idx]} ]] && records[idx]+=$'\n'
        records[idx]+=$line
    fi
done <<< "$output"

echo "Välkomsttext: $(trim "$greeting")"
echo

expected_records=$(( ${#INPUTS[@]} + 1 ))     # alla testrader + quit
if (( ${#records[@]} != expected_records )); then
    echo "FAILED: skickade $expected_records rader men fick ${#records[@]} svar."
    echo "Kalkylatorn kan ha en annan prompt (ändra PROMPT överst), kraschat"
    echo "tidigt, eller skrivit extra rader som börjar med prompten."
    echo
    echo "---- Rå utdata ----"
    echo "$output"
    exit 1
fi

# ----------------------------------------------------------------------
# Jämför varje svar med förväntat
# ----------------------------------------------------------------------
for i in "${!INPUTS[@]}"; do
    got=$(trim "${records[i]}")
    label="'${INPUTS[i]}'"
    kind=${KINDS[i]}
    want=${EXPECTED[i]}

    if [[ $got == *"Exception in thread"* || $got == *$'\tat '* ]]; then
        fail "$label" "stacktrace i utdata (oinfångat undantag?): $got"
        continue
    fi

    case $kind in
        exact)
            if [[ $got == "$want" ]]; then ok
            else fail "$label" "förväntade (exakt) '$want' men fick '$got'"; fi
            ;;
        has)
            if [[ ${got,,} == *"${want,,}"* ]]; then ok
            else fail "$label" "förväntade att svaret innehåller '$want' men fick '$got'"; fi
            ;;
        *)
            ok
            ;;
    esac
done

# ----------------------------------------------------------------------
# quit: avslutningstext och slutkod
# ----------------------------------------------------------------------
farewell=$(trim "${records[${#INPUTS[@]}]}")
if (( code == 0 )); then ok; else fail "'quit'" "slutkod $code (förväntade 0)"; fi
if [[ $farewell == *"Exception in thread"* ]]; then
    fail "'quit'" "stacktrace vid avslut: $farewell"
elif [[ -z $farewell ]]; then
    fail "'quit'" "ingen avslutningstext/statistik skrevs ut"
else
    ok
    echo
    echo "Avslutningstext (kontrollera statistiken för hand):"
    echo "$farewell"
fi

echo
echo "Passed: $passed, Failed: $failed"
(( failed == 0 ))
