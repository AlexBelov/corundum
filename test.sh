#!/bin/bash
javac Walker.java Corundum*.java
java -classpath /usr/local/lib/antlr-4.2.2-complete.jar:. Walker $1
