# Inlupp 3
I den här uppgiften har en symbolisk kalkylator utvecklats i java som en del av fas 2 i IOOPM. 


## Kompilera och köra

Kompilera programmet:

```bash
make
```

Kör programmet:

```bash
make run
```

## Test


```bash
make test
```

## Funktioner

Kalkylatorn stöder bland annat:

- **Uttryck:** `heltal, variabler, +, -, *, /, namngivna konstanter`
- **Variabler:** `Tilldelning av variabler sker med t.ex 5=x ` `ans är tidigare svar`
- **Kommandon:** `Vars`, `Clear`, `Quit`, 

## Designval

Vi skrev först egna tester. Därefter använde vi AI-genererade tester för att komplettera dem och täcka edge cases.

Vi följde parserns design och använder stor första bokstav på kommandon (t.ex. `Vars`, `Clear`).

Instruktionerna upplevdes som otydliga gällande funktionalitet, så vi valde att lägga `NamedConstants` i en separat fil från `Constants`.

## Överenskommelser med lärare

Vi har inte haft någon överenskommelse med läraren om avvikelser från uppgiftsbeskrivningen.