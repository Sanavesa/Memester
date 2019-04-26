For the config.txt file:
    -size
	is the dimensionality of the feature vectors. (size of the output vector)
	Default: 500

    -workers
	is the number of threads used
	Default: 5

    -window
	is the maximum distance between current and predicted word within a sentence think of it as field of view in the sentence
	Default: 10

    -sg
	defines the training algorithm. sg=0 is CBOW, whereas sg=1 is skip-gram
	Default: 1

    -negative
	uses negative sampling (google it)
	Default: 15

    -iterations
	number of iterations (epochs) over the corpus
	Default: 5

NOTE: You can delete the config.txt file and it will reconstruct itself to the default values if absent.
NOTE2: Output vector files are in the directory above under the Vectors folder.