#!/bin/bash
PREFIX="java -cp ./dist/joint-rna-compression-with-dependencies.jar" 
DATASET="dowell-benchmark-10-percent"

PARAMS=""
for k in {3..15}
do
	PARAMS="$PARAMS 2NTs-${k}rules"
done
for k in {3..6}
do
	PARAMS="$PARAMS 3NTs-${k}rules"
done

GRAMMARS=""
for p in $PARAMS
do
	GRAMMARS="$GRAMMARS all-${p}-adaptive-model-dowell-benchmark-10-percent"
done
# Could add more here, but data not used in Figures 2 and 3.
#GRAMMARS="$GRAMMARS best-1000-3NTs-7rules-adaptive-model-dowell-benchmark-10-percent"
#GRAMMARS="$GRAMMARS best-1000-3NTs-8rules-adaptive-model-dowell-benchmark-10-percent"

for grammarFolder in $GRAMMARS
do
	echo $grammarFolder
	$PREFIX compression.Compressions $DATASET $grammarFolder true adaptive
	$PREFIX compression.Compressions $DATASET $grammarFolder true static $DATASET
done
