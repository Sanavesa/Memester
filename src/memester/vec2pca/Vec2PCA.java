package memester.vec2pca;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;

public class Vec2PCA
{
	public static void computePCA(List<MemeVector> memeVectors)
	{
		RealMatrix realMatrix;
		Covariance covariance;
		RealMatrix covarianceMatrix;
		EigenDecomposition ed;
		RealVector v;
		
		for (MemeVector mv : memeVectors)
		{
			realMatrix = MatrixUtils.createRealDiagonalMatrix(mv.vector);
			covariance = new Covariance(realMatrix);
			covarianceMatrix = covariance.getCovarianceMatrix();
			ed = new EigenDecomposition(covarianceMatrix);
			v = ed.getV().getColumnVector(0);
			mv.vectorPCA[0] = v.getEntry(0);
			mv.vectorPCA[1] = v.getEntry(1);		
		}
	}
	
	public static List<MemeVector> readVectors(File directory)
	{
		List<MemeVector> memeVectors = new ArrayList<MemeVector>();
		
		for (File file : directory.listFiles())
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
				e.printStackTrace();
			}
		}
		
		return memeVectors;
	}
}
