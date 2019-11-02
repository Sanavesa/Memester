package memester.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import memester.vec2cluster.Cluster;
import memester.vec2cluster.KMeans;
import memester.vec2cluster.LabeledVector;
import memester.vec2cluster.Vector;
import memester.vec2pca.MemeVector;
import memester.vec2pca.Vec2PCA;

public class Snippet
{
	public static void main(String[] args)
	{
		List<LabeledVector> vectors = readVectors(new File("C:\\Users\\Sanavesa\\Documents\\GitHub\\Memester\\Build\\Vectors2"));
		int dimensions = vectors.get(0).getDimensions();
		
		KMeans kmeans = new KMeans(6, dimensions, vectors.toArray(new LabeledVector[vectors.size()]));
		for(int i = 0; i < 50; i++)
		{
			kmeans.update();
		}
		
		try(PrintWriter writer = new PrintWriter(new File("C:\\Users\\Sanavesa\\Documents\\GitHub\\Memester\\Build\\cityClusters.txt")))
		{
			for(Cluster cluster : kmeans.getFilteredClusters())
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
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		
		List<MemeVector> vectors2 = Vec2PCA.readVectors(new File("C:\\Users\\Sanavesa\\Documents\\GitHub\\Memester\\Build\\Vectors2"));
		Vec2PCA.computePCA(vectors2);
		try(PrintWriter writer = new PrintWriter(new File("C:\\Users\\Sanavesa\\Documents\\GitHub\\Memester\\Build\\exported.csv")))
		{
			writer.println("x, y, iri");
			for(MemeVector vec : vectors2)
			{
				writer.println(vec.vectorPCA[0] + "," + vec.vectorPCA[1] + "," + vec.IRI);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static List<LabeledVector> readVectors(File folder)
	{
		List<LabeledVector> vectors = new ArrayList<>();
		
		for(File file : folder.listFiles())
		{
			if(file.isDirectory())
				continue;
			
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
				e.printStackTrace();
			}
		}
		
		return vectors;
	}
}