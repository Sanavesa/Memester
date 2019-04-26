package memester.vec2pca;

public class MemeVector
{
	public MemeVector(String anIRI, double[] aVector)
	{
		IRI = anIRI;
		
		vector = aVector;
		vectorPCA = new double[2];
	}
	
	public double[] vector;
	public double[] vectorPCA;
	public String IRI;
}