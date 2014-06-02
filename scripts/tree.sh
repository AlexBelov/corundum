#!/bin/bash
java -classpath /usr/local/lib/antlr-4.2.2-complete.jar:. org.antlr.v4.runtime.misc.TestRig Corundum prog -gui $1
