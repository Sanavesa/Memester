package memester.rdf2walk;

import java.util.List;

import org.apache.jena.graph.Node;

/**
 * An implementation of {@link Graphwalker} where it will randomly traverse the graph creating walks.
 * 
 * @author Mohammad Alali
 */
public class RandomWalker implements GraphWalker
{
	
	/**
	 * Walk through an RDF graph and returns the generated walks.
	 * This will generate <code>nodesToWalk * numWalksPernode</code> walks as it will
	 * generate <code>numWalksPerNode</code> walk for <code>nodesToWalk</code> nodes.
	 * Furthermore, the size of each walk will be determined by <code>depth</code>.
	 * The depth is basically how many elements are in the walk.
	 * A walk with depth of 3 means A->B->C.
	 * 
	 * @param graph - the RDF graph
	 * @param nodesToWalk - the number of nodes in the graph to generate walks for
	 * @param numWalksPerNode - the number of walks to create for each node
	 * @param depth - the depth of the walk
	 * @return
	 */
	@Override
	public Walk[] walk(List<GraphNode> graph, int nodesToWalk, int numWalksPerNode, int depth)
	{
		final int totalWalks = nodesToWalk * numWalksPerNode;
		final Walk[] walks = new Walk[totalWalks];
		int walkIndex = 0;
		
		final long startTime = System.nanoTime();
		System.out.println("Starting walk for " + nodesToWalk + " nodes with " + numWalksPerNode + " walks each at a depth of " + depth + "...");
		
		for(final GraphNode graphNode : graph)
		{
			if(walkIndex >= totalWalks)
				break;
			
			final Node node = graphNode.getNode();
			
			if(isInvalidNode(node))
			{
				continue;
			}
			
			for(int i = 0; i < numWalksPerNode; i++)
			{
				final Node[] sequence = new Node[depth];
				
				GraphNode currentGraphNode = graphNode;
				
				for(int j = 0; j < depth; j+=2)
				{
					sequence[j] = currentGraphNode.getNode();
					
					final int randomIndex = (int) (Math.random() * currentGraphNode.getConnections().size());
					GraphConnection conn = currentGraphNode.getConnections().get(randomIndex);
					
					if(j+1 < depth)
					{
						sequence[j+1] = conn.getPredicate();
					}
					
					currentGraphNode = conn.getObject();
				}
				
				final Walk w = new Walk(sequence);
				walks[walkIndex++] = w;
			}
		}
		
		final double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
		System.out.println("Done walking! Elapsed time: " + elapsedTime + " secs\n");
		
		return walks;
	}
	
	private boolean isInvalidNode(Node node)
	{
		if(node == null)
			return true;
		
		if(node.isLiteral())
			return true;
		
		String iri = "";
		try
		{
			iri = node.getURI();
		}
		catch(Exception e)
		{
			return true;
		}
		
		if(!iri.contains("http://erau-semantic-research.com/2019/memo/0.1/"))
			return true;
		
		// properties are always lowerCase i.e: relatedMeme
		if(Character.isLowerCase(iri.charAt("http://erau-semantic-research.com/2019/memo/0.1/".length())))
			return true;
		
		if(!iri.contains("Meme"))
			return true;
		
		return false;
	}
}