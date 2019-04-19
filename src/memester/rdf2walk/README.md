# Overview
Mostly home-grown code, but uses the Apache Jena library for RDF-related work and builds atop this with understandable abstractions meant to be extended.

### GraphConnection
An abstraction for a node's connection to others. Includes the RDF concept of subject, predicate and object.

### GraphCreator
Takes the Jena RDF graph and converts it into a graph that is easier and faster to work with in Java.

### GraphNode
A wrapper for Jena's Node class.

### GraphWalker
An interface to be implemented with different types of Walkers. For now, there is a RandomWalker, which walks with no direction.

### MainEntry
Where the user specifies the path for the ontology file, how many walks to perform, the depth of the walk for each node, how many nodes to walk, etc. 

### RDFLoader
Loads in the specified ontology file and creates a Jena dataset to be worked with.

### RandomWalker
An implementation of GraphWalker; walks by picking random directions.

### Walk
Representation of a single walk, literally one line of text, where each traversal 'hop' is the node, and each node 'hop' is indicated by a '->'.

### WalkExporter
Exports all walk objects into a single text file for further processing. The final step of the RDF2Walk process.
