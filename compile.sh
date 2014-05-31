#!/bin/bash
java -jar /usr/local/lib/antlr-4.2.2-complete.jar Corundum.g4
javac Corundum*.java
javac Walker.java Corundum*.java
