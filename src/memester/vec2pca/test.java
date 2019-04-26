package memester.vec2pca;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;

public class test
{
	public List<MemeVector> memeVectors;
	
	public void getMemeVectors()
	{
		double[][] pointsArray = new double[][] 
		{ 
		    new double[] {10, 20}, 
		    new double[] {-20, 5},
		};

		//see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
				
		File folder = new File("Vectors");
				
		memeVectors = new ArrayList<MemeVector>();
		
		int j = 0;
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				continue;				
			}
						
			try
			{
				List<String> lines = Files.readAllLines(file.toPath());
				List<Double> values = new ArrayList<Double>();
				
				String iri = lines.get(0);
				
				for (int i = 1; i < lines.size(); i++)
				{
					String line = lines.get(i);
					
					if (i == 1)
					{
						line = line.substring(1);
					}
					else if (i == lines.size() - 1)
					{
						line = line.substring(0, line.length() - 1);
					}
					
					Scanner scanner = new Scanner(line);
					while (scanner.hasNextDouble())
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
				
				MemeVector mv = new MemeVector(iri, convertedValues);
				memeVectors.add(mv);
			}
			catch (Exception e)
			{
			}
		}
		
		System.out.println("files read: " + memeVectors.size());
		
		RealMatrix realMatrix;
		Covariance covariance;
		RealMatrix covarianceMatrix;
		EigenDecomposition ed;
		RealVector v;
		
		for (MemeVector mv : memeVectors)
		//for (int i = 0; i < 200; i++)
		{
			// createRealMatrix is original, needs a [][] array though, not a []
			//MemeVector mv = memeVectors.get(i);
			realMatrix = MatrixUtils.createRealDiagonalMatrix(mv.vector);
			covariance = new Covariance(realMatrix);
			covarianceMatrix = covariance.getCovarianceMatrix();
			ed = new EigenDecomposition(covarianceMatrix);
			
//			try
//			{
				v = ed.getV().getColumnVector(0);
				mv.vectorPCA[0] = v.getEntry(0);
				mv.vectorPCA[1] = v.getEntry(1);		
				System.out.println(Arrays.toString(mv.vectorPCA));
//			}
//			catch (Exception e)
//			{
//				continue;
//			}
		}
	}
}