#!/bin/bash
PREFIX="java -cp ../dist/joint-rna-compression-with-dependencies.jar" 
DATASET="dowell-benchmark"

for model in adaptive static
do
	$PREFIX compression.CompressionBuiltinGrammars $DATASET all true $model $DATASET
done
