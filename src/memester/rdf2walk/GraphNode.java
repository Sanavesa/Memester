package memester.rdf2walk;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;

/**
 * This class represents a node in the RDF Graph.
 * It encapsulates an underlying {@link Node} instance which makes this a simple wrapper.
 * However, it also has capabilities to include the connections to other nodes in the graph.
 * 
 * @author Mohammad Alali
 */
public final class GraphNode
{
	/**
	 * Create a graph node from the specified JENA node representation with no connections.
	 * @param node - the JENA node representation
	 */
	public GraphNode(Node node)
	{
		this.node = node;
		connections = new ArrayList<>();
	}

	/**
	 * Adds a bi-directional connection for the specified subject and object.
	 * @param subject - the subject of the connection
	 * @param predicate - the predicate of the connection
	 * @param object - the object of the connection
	 */
	public static void addBiConnection(GraphNode subject, Node predicate, GraphNode object)
	{
		subject.connections.add(new GraphConnection(subject, predicate, object));
		object.connections.add(new GraphConnection(object, predicate, subject));
	}

	/**
	 * @return the JENA node representation
	 */
	public Node getNode()
	{
		return node;
	}

	/**
	 * @return a list of all connections for this node
	 */
	public List<GraphConnection> getConnections()
	{
		return connections;
	}

	/**
	 * The format is a string representation of the JENA node's toString implementation.
	 * @return a human readable string representation
	 * @see {@link Node#toString()}}
	 */
	@Override
	public String toString()
	{
		return node.toString();
	}

	/**
	 * The JENA node representation
	 */
	private final Node node;
	
	/**
	 * A list of all connections for this node in the graph
	 */
	private final List<GraphConnection> connections;
}