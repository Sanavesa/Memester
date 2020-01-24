package memester.rdf2walk;

import java.util.List;
import java.util.Set;

public class RDFGraph
{
	public RDFGraph(List<GraphNode> graph, Set<GraphNode> memeNodes)
	{
		this.graph = graph;
		this.memeNodes = memeNodes;
	}
	
	public List<GraphNode> getGraph()
	{
		return graph;
	}
	public Set<GraphNode> getMemeNodes()
	{
		return memeNodes;
	}

	private final List<GraphNode> graph;
	private final Set<GraphNode> memeNodes;
}
