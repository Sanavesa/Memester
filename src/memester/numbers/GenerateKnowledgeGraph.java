package memester.numbers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GenerateKnowledgeGraph
{
	public static void main(String[] args) throws FileNotFoundException
	{
		new GenerateKnowledgeGraph(1, 10000);
	}

	public GenerateKnowledgeGraph(int min, int max) throws FileNotFoundException
	{
		List<NumberEntry> entries = new ArrayList<>();
		for (int value = min; value <= max; value++)
		{
			entries.add(new NumberEntry(value));
		}

		// Export
		try (PrintWriter pw = new PrintWriter(new File("numbers_dataprop_instances.owl")))
		{
			pw.println("<!-- Named Individuals -->");
			for (NumberEntry entry : entries)
			{
				pw.println(entry.asDataOWL());
				pw.println();
			}
		}

		// Export
		try (PrintWriter pw = new PrintWriter(new File("numbers_attrprop_instances.owl")))
		{
			pw.println("<!-- Named Individuals -->");
			for (NumberEntry entry : entries)
			{
				pw.println(entry.asAttributeOWL());
				pw.println();
			}
		}
	}

	public class NumberEntry
	{
		private final int value;
		private final boolean isEven; // isOdd = !isEven
		private final boolean isPrime; // isComposite = !isPrime
		private final List<Integer> factors;

		public NumberEntry(int val)
		{
			value = val;
			isEven = (val % 2 == 0);
			factors = calculateFactors();
			isPrime = (getNumFactors() == 2);
		}

		private List<Integer> calculateFactors()
		{
			List<Integer> factors = new ArrayList<>();
			for (int i = 1; i <= value; i++)
			{
				if (value % i == 0)
				{
					factors.add(i);
				}
			}
			return factors;
		}

		public int getValue()
		{
			return value;
		}

		public boolean isEven()
		{
			return isEven;
		}

		public boolean isOdd()
		{
			return !isEven;
		}

		public boolean isPrime()
		{
			return isPrime;
		}

		public boolean isComposite()
		{
			return !isPrime;
		}

		public List<Integer> getFactors()
		{
			return factors;
		}

		public int getNumFactors()
		{
			return factors.size();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(factors, isEven, isPrime, value);
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NumberEntry other = (NumberEntry) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return Objects.equals(factors, other.factors) && isEven == other.isEven && isPrime == other.isPrime && value == other.value;
		}

		private GenerateKnowledgeGraph getEnclosingInstance()
		{
			return GenerateKnowledgeGraph.this;
		}

		@Override
		public String toString()
		{
			return "NumberEntry [value=" + value + ", isEven=" + isEven + ", isPrime=" + isPrime + ", factors=" + factors + "]";
		}

		public String getValueInWord(int value)
		{
			return String.valueOf(value);
//			return numberToWord(value).replace(" ", "").trim();
		}

		public String getValueInWord()
		{
			return getValueInWord(value);
		}

		public String getIRI(int value)
		{
			return "http://erau-semantic-research.com/2020/numbers/0.1/" + getValueInWord(value);
		}

		public String getIRI()
		{
			return getIRI(value);
		}

		public String asAttributeOWL()
		{
			StringBuffer buffer = new StringBuffer();

			buffer.append("<owl:NamedIndividual rdf:about=\"" + getIRI() + "\">\n");
			buffer.append("\t<rdfs:label xml:lang=\"en\">" + getValueInWord() + "</rdfs:label>\n");
			buffer.append("\t<rdf:type rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1/Number\"/>\n");

			buffer.append("\t<value>" + value + "</value>\n");

			for (int factor : factors)
			{
				buffer.append("\t<factorOf rdf:resource=\"" + getIRI(factor) + "\"/>\n");
			}

			if (isPrime())
				buffer.append("\t<hasNumberAttribute rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1/PrimeNumberAttribute\"/>\n");

			if (isComposite())
				buffer.append("\t<hasNumberAttribute rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1/CompositeNumberAttribute\"/>\n");

			if (isEven())
				buffer.append("\t<hasNumberAttribute rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1/EvenNumberAttribute\"/>\n");

			if (isOdd())
				buffer.append("\t<hasNumberAttribute rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1/OddNumberAttribute\"/>\n");

			buffer.append("\t<rdfs:isDefinedBy rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1\"/>\n");

			buffer.append("</owl:NamedIndividual>");

			return buffer.toString();
		}

		public String asDataOWL()
		{
			StringBuffer buffer = new StringBuffer();

			buffer.append("<owl:NamedIndividual rdf:about=\"" + getIRI() + "\">\n");
			buffer.append("\t<rdfs:label xml:lang=\"en\">" + getValueInWord() + "</rdfs:label>\n");
			buffer.append("\t<rdf:type rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1/Number\"/>\n");

			buffer.append("\t<value>" + value + "</value>\n");
			buffer.append("\t<prime>" + (isPrime() ? "True" : "False") + "</prime>\n");
			buffer.append("\t<composite>" + (isComposite() ? "True" : "False") + "</composite>\n");
			buffer.append("\t<even>" + (isEven() ? "True" : "False") + "</even>\n");
			buffer.append("\t<odd>" + (isOdd() ? "True" : "False") + "</odd>\n");

			for (int factor : factors)
			{
				buffer.append("\t<factorOf rdf:resource=\"" + getIRI(factor) + "\"/>\n");
			}

			buffer.append("\t<rdfs:isDefinedBy rdf:resource=\"http://erau-semantic-research.com/2020/numbers/0.1\"/>\n");

			buffer.append("</owl:NamedIndividual>");

			return buffer.toString();
		}

		private final String[] numNames = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen" };
		private final String[] tensNames = { "", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety" };
		private final String[] specialNames = { "", "Thousand", "Million", "Billion" };

		private String numberToWordLessThan1K(int number)
		{
			String current;

			if (number % 100 < 20)
			{
				current = numNames[number % 100];
				number /= 100;
			}
			else
			{
				current = numNames[number % 10];
				number /= 10;

				current = tensNames[number % 10] + current;
				number /= 10;
			}
			if (number == 0)
				return current;
			return numNames[number] + " Hundred" + current;
		}

		public String numberToWord(int number)
		{

			if (number == 0)
			{
				return "Zero";
			}

			String prefix = "";

			if (number < 0)
			{
				number = -number;
				prefix = "negative";
			}

			String current = "";
			int place = 0;

			do
			{
				int n = number % 1000;
				if (n != 0)
				{
					String s = numberToWordLessThan1K(n);
					current = s + specialNames[place] + current;
				}
				place++;
				number /= 1000;
			} while (number > 0);

			return (prefix + current).trim();
		}
	}
}