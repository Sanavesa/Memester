package memester.rdf2walk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * This class will export meme nodes.
 * 
 * @author Mohammad Alali
 */
public final class MemeNodesExporter
{
	/**
	 * Exports the given set of meme nodes to a single file.
	 * Each meme node's URI will be on a line.
	 * 
	 * @param outputFilename - the file name for the output file
	 * @param memeNodes - the set of meme nodes to export
	 */
	public static void export(String outputFilename, Set<GraphNode> memeNodes)
	{
		File outputFile = new File(outputFilename);
		try(PrintWriter writer = new PrintWriter(outputFile))
		{
			final long startTime = System.nanoTime();
			
			System.out.println("Exporting meme nodes to " + outputFilename + "...");
			
			for(GraphNode memeNode : memeNodes)
			{
				writer.println(memeNode.getNode());
			}
			
			final double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
			final double fileSize = outputFile.length() / 1024.0 / 1024.0; // Convert bytes to MB
			System.out.println("Done exporting! Elapsed Time: " + elapsedTime + "secs. Export size: " + fileSize + " MB\n");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}