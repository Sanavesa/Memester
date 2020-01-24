package memester.rdf2walk;

import org.apache.jena.graph.Node;

import memester.app.Constants;

public class NodeValidator
{
	private NodeValidator() {}
	
	public static boolean isInvalidNode(Node node)
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
		
		if(iri.equals(Constants.BASE_IRI))
			return true;
		
		if(iri.equals(Constants.BASE_IRI + "Meme"))
			return true;

		if(!iri.contains(Constants.BASE_IRI))
			return true;
		
		// properties are always lowerCase i.e: relatedMeme
		if(Character.isLowerCase(iri.charAt(Constants.BASE_IRI.length())))
			return true;
		
		if(!iri.endsWith("Meme"))
			return true;

		return false;
	}
}
