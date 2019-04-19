# Memester
Use in tandem with https://github.com/Psychobagger/knowyourmeme.com-Crawler to generate a meme RDF file, or use your own RDF file of any type!

The process:
RDF2Walks -> Walks2Vec -> Clustering -> Principal Component Analysis

### RDF2Walks
Walks through the RDF graph randomly and generates a walks file, for use in the conversion to vectors. Works with .rdf, .owl, .ttl, .n3, etc. files. Just make a folder, put your ontology file in it and select that folder to work with it.

### Walks2Vec
Generates vectors for each "word" in a walk "sentence", using Word2Vec AI in Python (CBOW model). The Python code is compiled as an executable file, so unless you want to work with the source directly, having the Python executable is enough- you do not need to download all of the prerequisites.

### Clustering
Takes the vectors and clusters them into groups to try to find similarities between them.

### Principal Component Analysis (PCA)
The vectors produced have 500 components, so PCA attempts to reduce these vectors to two dimensions for visualization.

## Ya like DFDs, kid?
![alt text](https://github.com/Sanavesa/Memester/blob/master/resources/level2_DFD_2.png)
