all:
	javac -d classes -sourcepath src src/org/ioopm/calculator/*.java

run:
	java -cp classes org.ioopm.calculator.Calculator

test:
	javac -d classes -sourcepath src src/Test.java
	java -cp classes -enableassertions Test

testparse:
	javac -d classes -sourcepath src src/TestParser.java
	java -cp classes -enableassertions TestParser
clean:
	rm -rf classes
