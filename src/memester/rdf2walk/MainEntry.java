package memester.rdf2walk;

import java.util.List;
import java.util.Scanner;

import org.apache.jena.query.Dataset;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MainEntry
{
	public static void main(String[] args)
	{
		System.out.println("-- RDF2Walk --");
		
		// Turn off JENA's logging
		Logger.getRootLogger().setLevel(Level.OFF);
		
		// Get user input
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Ontology File Absolute Directory: ");
		String directory = scanner.nextLine();
		
		System.out.print("Nodes To Walk (1+): ");
		int nodesToWalk = scanner.nextInt();
		
		System.out.print("Number of Walks per Node (1+): ");
		int numWalksPerNode = scanner.nextInt();
		
		System.out.print("Walk Depth (3+): ");
		int depth = scanner.nextInt();
		
		scanner.close();
		
		System.out.println("\n===============================================\n");
		
		// Load RDF & create a graph representation
		Dataset dataset = RDFLoader.loadFiles(directory, "Meme Ontology v0.5");
		List<GraphNode> graph = GraphCreator.createGraph(dataset);
		
		// Create a random walker and generate them
		GraphWalker walker = new RandomWalker();
		Walk[] walks = walker.walk(graph, nodesToWalk, numWalksPerNode, depth);
		
		// Export walks
		WalkExporter.export("walks.txt", walks);
	}
}