package memester.rdf2walk;

import org.apache.jena.graph.Node;

/**
 * This class represents a single walk in the graph.
 * A walk contains a sequence of nodes of a specified depth.
 * 
 * @author Mohammad Alali
 */
public final class Walk
{
	/**
	 * Create a walk instance from the provided node instances.
	 * The depth will be automatically detected based on the amount of instances passed.
	 * @param walk - the instances in the walk
	 */
	public Walk(Node... walk)
	{
		this.walk = walk;
	}
	
	/**
	 * Create an empty walk instance with the provided depth.
	 * can use {@link #getWalk()} to access the walk and set each index manually.
	 * @param depth
	 */
	public Walk(int depth)
	{
		walk = new Node[depth];
	}
	
	/**
	 * @return the walk sequence
	 */
	public Node[] getWalk()
	{
		return walk;
	}
	
	/**
	 * @return the size of the walk's sequence
	 */
	public int getDepth()
	{
		return walk.length;
	}
	
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < walk.length; i++)
		{
			Node node = walk[i];
			stringBuilder.append(node.toString());
			
			if(i != walk.length - 1)
			{
				stringBuilder.append(sequenceSeparator);
			}
		}
		
		return stringBuilder.toString();
	}

	/**
	 * The sequence of the walk as represented by JENA's Node instance.
	 */
	private final Node[] walk;
	
	/**
	 * The separator for the walk's sequence as used in {@link #toString()}.
	 * Default value is "->".
	 */
	public static String sequenceSeparator = "->";
}