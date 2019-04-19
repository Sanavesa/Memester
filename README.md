# Memester
Use in tandem with https://github.com/Psychobagger/knowyourmeme.com-Crawler to generate a meme RDF file, or use your own RDF file of any type!

The process:
RDF2Walks -> Walks2Vec -> Clustering -> Principal Component Analysis

## RDF2Walks
Walks through the RDF graph randomly and generates a walks file, for use in the conversion to vectors.

## Walks2Vec
Generates vectors for each "word" in a walk "sentence", using Word2Vec AI in Python (CBOW model).

## Clustering
Takes the vectors and clusters them into groups to try to find similarities between them.

## Principal Component Analysis (PCA)
The vectors produced have 500 components, so PCA attempts to reduce these vectors to two dimensions for visualization.
