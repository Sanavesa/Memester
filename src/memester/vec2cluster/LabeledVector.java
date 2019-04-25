package memester.vec2cluster;

public class LabeledVector extends Vector
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