package memester.rdf2walk;

import java.util.List;
import java.util.Set;

/**
 * An interface used to walk through an RDF graph.
 * 
 * @author Mohammad Alali
 */
public interface GraphWalker
{
	/**
	 * Walk through an RDF graph and returns the generated walks.
	 * This will generate <code>nodesToWalk * numWalksPernode</code> walks as it will
	 * generate <code>numWalksPerNode</code> walk for <code>nodesToWalk</code> nodes.
	 * Furthermore, the size of each walk will be determined by <code>depth</code>.
	 * The depth is basically how many elements are in the walk.
	 * A walk with depth of 3 means A->B->C.
	 * 
	 * @param graph - the RDF graph to traverse that also contains the meme nodes in the graph
	 * @param nodesToWalk - the number of memes to generate walks for
	 * @param numWalksPerNode - the number of walks for each node
	 * @param depth - the depth of each walk
	 * @return the generated walks
	 */
	public Walk[] walk(RDFGraph graph, int nodesToWalk, int numWalksPerNode, int depth);
}