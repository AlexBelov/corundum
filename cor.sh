#!/bin/bash
java -classpath /usr/local/lib/antlr-4.2.2-complete.jar:. Compiler $1 1>.compiled.pir
parrot .compiled.pir
