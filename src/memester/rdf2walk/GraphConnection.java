package memester.rdf2walk;

import org.apache.jena.graph.Node;

/**
 * This class represents a connection in an RDF graph.
 * It contains a subject, predicate, and an object.
 * 
 * @author Mohammad Alali
 */
public final class GraphConnection
{
	/**
	 * Create a connection from the given parameters..
	 * @param subject - the origin node
	 * @param predicate - the predicate of the connection
	 * @param object - the receiver node
	 */
	public GraphConnection(GraphNode subject, Node predicate, GraphNode object)
	{
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/**
	 * @return the subject of the connection
	 */
	public GraphNode getSubject()
	{
		return subject;
	}

	/**
	 * @return the predicate of the connection
	 */
	public Node getPredicate()
	{
		return predicate;
	}

	/**
	 * @return the object of the connection
	 */
	public GraphNode getObject()
	{
		return object;
	}

	/**
	 * The format of the string will be <b>[subject predicate object]</b>.
	 * @return a human readable representation of the string
	 */
	@Override
	public String toString()
	{
		return "[" + subject + " " + predicate.toString() + " " + object + "]";
	}

	/**
	 * The subject of the connection.
	 * @see {@link GraphNode}
	 */
	private final GraphNode subject;
	
	/**
	 * The predicate of the connection.
	 * Doesn't use GraphNode like the others because predicates don't have a node on the graph.
	 */
	private final Node predicate;
	
	/**
	 * The object of the connection.
	 * @see {@link GraphNode}
	 */
	private final GraphNode object;
}