all:
	javac -d classes -sourcepath src src/org/ioopm/calculator/*.java

run:
	java -cp classes org.ioopm.calculator.Calculator

test:
	javac -d classes -sourcepath src src/Test.java
	java -cp classes -enableassertions Test

clean:
	rm -rf classes
