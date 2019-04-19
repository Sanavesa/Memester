package memester.rdf2walk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.sparql.core.Quad;

/**
 * This class will create a graph representation for a JENA's RDF representation.
 * @see Dataset
 * 
 * @author Mohammad Alali
 */
public final class GraphCreator
{
	/**
	 * Will create a graph representation of the provided dataset.
	 * The benefits of this is that the graph stores all non-literal nodes and their
	 * connection to other nodes, making it much easier to traverse than SPARQL.
	 * 
	 * @param dataset - the JENA dataset of the RDF file
	 * @return the graph representation
	 */
	public static List<GraphNode> createGraph(Dataset dataset)
	{
		dataset.begin(ReadWrite.READ);
		Map<Node, GraphNode> graph = new HashMap<>();
		
		final long startTime = System.nanoTime();
		System.out.println("Creating graph...");

		try
		{
			Iterator<Quad> iter = dataset.asDatasetGraph().find();
			while (iter.hasNext())
			{
				Quad quad = iter.next();

				Node subjectNode = quad.getSubject();
				Node predicateNode = quad.getPredicate();
				Node objectNode = quad.getObject();

				if (!graph.containsKey(subjectNode))
				{
					graph.put(subjectNode, new GraphNode(subjectNode));
				}

				if (!graph.containsKey(objectNode))
				{
					graph.put(objectNode, new GraphNode(objectNode));
				}

				GraphNode subject = graph.get(subjectNode);
				GraphNode object = graph.get(objectNode);

				GraphNode.addBiConnection(subject, predicateNode, object);
			}
		}
		finally
		{
			dataset.end();
		}
		
		List<GraphNode> doneGraph = new ArrayList<>(graph.values());
		final double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
		System.out.println("Done graph construction. Size: " + doneGraph.size() + " nodes. Elapsed Time: " + elapsedTime + "secs\n");
		
		return doneGraph;
	}
}