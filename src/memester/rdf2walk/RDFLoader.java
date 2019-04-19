package memester.rdf2walk;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

/**
 * This class will load RDF/OWL files using JENA. 
 * 
 * @author Mohammad Alali
 */
public final class RDFLoader
{
	/**
	 * Loads all the rdf-valid files in the specified directory and gives the dataset
	 * the specified name.
	 * 
	 * @param directory - the directory to look for the rdf-valid files
	 * @param datasetName - an internal name to use for the dataset
	 * @return the loaded dataset representation in JENA format
	 */
	public static Dataset loadFiles(final String directory, final String datasetName)
	{
		final long startTime = System.nanoTime();
		
		final Dataset dataset = TDBFactory.createDataset(datasetName);
		final Model model = dataset.getDefaultModel();
		
		final File folder = new File(directory);
		
		System.out.println("Loading RDF/OWL files from " + directory + "...");
		
		int loadedFilesCount = 0;

		for(final File file : folder.listFiles())
		{
			String extension = FilenameUtils.getExtension(file.getAbsolutePath());
			
			// Ignore invalid extensions and folders
			if(!isValidFileExtension(extension) || file.isDirectory())
				continue;
			
			System.out.println("\tProcessing " + file.getName());
			FileManager.get().readModel(model, file.getPath());
			loadedFilesCount++;
		}
		
		final double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
		System.out.println("Loaded " + loadedFilesCount + " files! Elapsed Time: " + elapsedTime + "secs\n");
		
		return dataset;
	}
	
	/**
	 * Will test if the extension is valid. Note that <code>extension</code> is without the period.
	 * 
	 * @param extension - the extension to be tested
	 * @return true if valid extension, false otherwise
	 */
	private static boolean isValidFileExtension(String extension)
	{
		return 	extension.equals("rdf") ||
				extension.equals("owl") ||
				extension.equals("n3") ||
				extension.equals("nt") ||
				extension.equals("ttl");
	}
}
