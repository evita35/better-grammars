`dowell-benchmark`: the benchmark dataset from Dowell&Eddy 2004 with `()` in their secondary structure are replaced with `..`

`dowell-benchmark-10-percent`: A random 10% subsample of `dowell-benchmark`


`SmallDataSet`: subset from friemel, chosen to be diverse representation. Has no non-canonical pairs

`small-dataset`: `SmallDataSet` but with RNA > 1000 bases removed

`parsable`: subset of `SmallDataSet` (apart from one file), most tiny RNAs removed.

`minimal-parsable`: synthetic dataset with some tiny structures chosen to quickly sieve out non-sense grammars


`friemel-modified`: same as dataset as Jonas Friemel provided it to us;  he already randomly replaced ambiguous bases and non-canonical bonds; the data is taken from Cannone et al. 2002. `friemel-modified` additionally has empty hairpins remove (in RNAs with empty hairpin loops `()` in their secondary structure are replaced with `..`)

`dowell-mixed80`: the mixed80 dataset from Dowell&Eddy 2004, but all unknown bases have been randomly replaced with actual bases A,C,G,U compatible with the ambiguous base symbol


`TestDataSet`: This dataset is created for unit tests. It contains 6 RNAs, 2 from Benchmark("AGR.TUM", "Agro.tume"), 2 from mixed80("AB012589", "AB013269") and 2 from Friemel-modified ("9_1480_c", "10_552_c").

`TestTrainingData`: This dataset is created for unit tests which require training dataset. it contains 6 RNAs, 2 each from Friemel, Benchmark and Mixed80 
