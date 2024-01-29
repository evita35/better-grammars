#!/bin/bash
PREFIX="java -cp ./dist/joint-rna-compression-with-dependencies.jar" 
DATASET="dowell-benchmark"

for model in adaptive static
do
	$PREFIX compression.Compressions $DATASET random-good true $model $DATASET
done
