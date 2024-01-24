#!/bin/bash
PREFIX="java -cp ./dist/joint-rna-compression-with-dependencies.jar" 
for k in {1..15}
do
	$PREFIX compression.grammargenerator.ExhaustiveGrammarExplorer 2 $k inf dowell-benchmark-10-percent
done
for k in {1..6}
do
	$PREFIX compression.grammargenerator.ExhaustiveGrammarExplorer 3 $k inf dowell-benchmark-10-percent
done
$PREFIX compression.grammargenerator.ExhaustiveGrammarExplorer 3 7 1000 dowell-benchmark-10-percent

