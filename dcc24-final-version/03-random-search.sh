#!/bin/bash
PREFIX="java -cp ./dist/joint-rna-compression-with-dependencies.jar compression.grammargenerator.RandomGrammarExplorer" 
SUFFIX="10 dowell-benchmark 42424242 small-dataset adaptive"

# 6 NTs, 10 rules
$PREFIX 6 10 $SUFFIX 

$PREFIX 5 10 $SUFFIX 
$PREFIX 3  9 $SUFFIX 
$PREFIX 3 13 $SUFFIX 
$PREFIX 6 13 $SUFFIX 
$PREFIX 8 18 $SUFFIX 
