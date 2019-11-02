package memester.rdf2walk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * This class will export walks.
 * 
 * @author Mohammad Alali
 */
public final class WalkExporter
{
	/**
	 * Exports the given walks array to a single file.
	 * Each walk will be on a line.
	 * 
	 * @param outputFilename - the file name for the output file
	 * @param walks - the walks array to export
	 */
	public static void export(String outputFilename, Walk[] walks)
	{
		File outputFile = new File(outputFilename);
		try(PrintWriter writer = new PrintWriter(outputFile))
		{
			final long startTime = System.nanoTime();
			
			System.out.println("Exporting walks to " + outputFilename + "...");
			
			for(Walk walk : walks)
			{
				System.out.println(walk);
				writer.println(walk);
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