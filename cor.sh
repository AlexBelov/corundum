#!/bin/bash
java -jar /usr/local/lib/antlr-4.2.2-complete.jar Corundum.g4
javac Corundum*.java
java -classpath /usr/local/lib/antlr-4.2.2-complete.jar:. org.antlr.v4.runtime.misc.TestRig Corundum prog -gui $1
javac Walker.java Corundum*.java
