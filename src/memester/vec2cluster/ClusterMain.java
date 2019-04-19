package memester.vec2cluster;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClusterMain
{
	public static void main(String[] args)
	{
		System.out.println("-- Vec2Cluster --");
		
		// Get user input
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Vectors Absolute Directory: ");
		String directory = scanner.nextLine();
		
		System.out.print("Number of Iterations: ");
		int iterations = scanner.nextInt();
		
		System.out.print("Number of Clusters: ");
		int clusterCount = scanner.nextInt();
		
		scanner.close();
		
		List<LabeledVector> vectors = readVectors(new File(directory));
		int dimensions = vectors.get(0).getDimensions();
		
		KMeans kmeans = new KMeans(clusterCount, dimensions, vectors.toArray(new LabeledVector[vectors.size()]));
		for(int i = 0; i < iterations; i++)
		{
			System.out.println("Cluster Iteration " + (i+1) + " / " + iterations);
			kmeans.update();
		}
		
		System.out.println("Done clustering!");
		
		System.out.println("Exporting to clusters.txt");
		
		try(PrintWriter writer = new PrintWriter("clusters.txt"))
		{
			for(Cluster cluster : kmeans.getClusters())
			{
				writer.println("Cluster has: ");
				
				for(Vector vector : cluster.getPoints())
				{
					LabeledVector point = (LabeledVector) vector;
					writer.println("\t" + point.getName() + "\t" + point);
				}
				
				writer.println("=============================================================================");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Done!");
		
//		System.out.println("Dimensions = " + dimensions);
//		System.out.println("Cluster Size,Error");
//		for(int clusterSize = 1; clusterSize < 130; clusterSize++)
//		{
//			KMeans kmeans = new KMeans(clusterSize, dimensions, vectors);
//			for(int i = 0; i < 100; i++)
//			{
//				kmeans.update();
//			}
//			System.out.println(clusterSize + "," + kmeans.getError());
//		}
		
//		for(Cluster cluster : kmeans.getClusters())
//		{
//			System.out.println(cluster.getPoints().size());
//		}
		
	}
	
	public static List<LabeledVector> readVectors(File folder)
	{
		List<LabeledVector> vectors = new ArrayList<>();
		System.out.println("Starting to read");
		
		for(File file : folder.listFiles())
		{
			if(file.isDirectory())
				continue;
			
//			System.out.println(file.getAbsolutePath());
			
			try
			{
				List<String> lines = Files.readAllLines(file.toPath());
				List<Double> values = new ArrayList<>();
				
				// First line is always the iri of the node
				String iri = lines.get(0);
				
				for(int i = 1; i < lines.size(); i++)
				{
					String line = lines.get(i);
					
					// Special case for first and last line
					if(i == 1)
					{
						line = line.substring(1);
					}
					else if(i == lines.size() - 1)
					{
						line = line.substring(0, line.length() - 1);
					}
					
					// Process numbers
					Scanner scanner = new Scanner(line);
					while(scanner.hasNextDouble())
					{
						double readValue = scanner.nextDouble();
						values.add(readValue);
					}
					scanner.close();
				}
				
				double[] convertedValues = new double[values.size()];
				for(int i = 0; i < values.size(); i++)
				{
					convertedValues[i] = values.get(i).doubleValue();
				}
				
				LabeledVector labeledVector = new LabeledVector(iri, convertedValues);
				vectors.add(labeledVector);
			}
			catch (IOException e)
			{
//				e.printStackTrace();
			}
		}
		
		System.out.println("Done reading");
		
		return vectors;
	}
}

class LabeledVector extends Vector
{
	public LabeledVector(String name, double... values)
	{
		super(values);
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	private final String name;
}