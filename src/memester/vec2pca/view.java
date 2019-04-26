package memester.vec2pca;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class view extends ApplicationFrame {

/**
 * A demonstration application showing an XY series containing a null value.
 *
 * @param title  the frame title.
 */
	public view(final String title) {
	
	    super(title);
//	    
//	    test t = new test();
//	    t.getMemeVectors();
//	    
//	    final XYSeriesCollection data = new XYSeriesCollection();
//	    //for (int i = 0; i < 200; i++)
//	    for (MemeVector mv : t.memeVectors)
//	    {
//	    	//MemeVector mv = t.memeVectors.get(i);
//	    	
//	    	if (Math.abs(mv.vectorPCA[0]) >= .7 || Math.abs(mv.vectorPCA[1]) >= .7)
//	    	{
//	    		continue;
//	    	}
//	    	
//	    	final XYSeries series = new XYSeries(mv.IRI);
//	    	series.add(mv.vectorPCA[0], mv.vectorPCA[1]);
//	    	data.addSeries(series);
//	    }
//	    
////	    final XYSeriesCollection data = new XYSeriesCollection();
////	    final XYSeries series = new XYSeries("Random Data");
////	    final XYSeries s = new XYSeries("y");
////	    series.add(1.0, 500.2);
////	    series.add(5.0, 694.1);
////	    series.add(4.0, 100.0);
////	    series.add(12.5, 734.4);
////	    series.add(17.3, 453.2);
////	    series.add(21.2, 500.2);
////	    series.add(21.9, null);
////	    series.add(25.6, 734.4);
////	    series.add(-300.0, 453.2);
//	    final JFreeChart chart = ChartFactory.createScatterPlot(
//	        "XY Series Demo",
//	        "X", 
//	        "Y", 
//	        data,
//	        PlotOrientation.VERTICAL,
//	        true,
//	        true,
//	        false
//	    );
//	    chart.removeLegend();
//	    final ChartPanel chartPanel = new ChartPanel(chart);
//	    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//	    setContentPane(chartPanel);
	
	}

	public void testingSimple()
	{
		double[][] arrr = new double[][]
		{
			{0, 0, 6, 1, 4},
			{100, 250, 67, 150, 105},
			{15, 20, 13, 35, 16},
			{10, 30, 100, 200, 1},
			{100, 150, 0, 1, 2},
			{101, 151, 1, 2, 3},
			{91, 141, 6, 7, -1}
		};
		
		List<MemeVector> mvlist = new ArrayList<MemeVector>();
		for (int i = 0; i < arrr.length; i++)
		{
			MemeVector mv = new MemeVector("" + i, arrr[i]);		
			mvlist.add(mv);
		}
		
		final XYSeriesCollection data = new XYSeriesCollection();
		
		EigenDecomposition ed;
		RealVector v;
		
		for (MemeVector mv : mvlist)
		{
			RealMatrix realMatrix = MatrixUtils.createRealDiagonalMatrix(mv.vector);
			Covariance covariance = new Covariance(realMatrix);
			RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
			ed = new EigenDecomposition(covarianceMatrix);	
			
			v = ed.getV().getColumnVector(0);
			mv.vectorPCA[0] = v.getEntry(0);
			mv.vectorPCA[1] = v.getEntry(1);	
			
			final XYSeries series = new XYSeries(mv.IRI);
	    	series.add(mv.vectorPCA[0], mv.vectorPCA[1]);
	    	data.addSeries(series);
		}

		final JFreeChart chart = ChartFactory.createScatterPlot(
		        "XY Series Demo",
		        "X", 
		        "Y", 
		        data,
		        PlotOrientation.VERTICAL,
		        true,
		        true,
		        false
		    );
		    chart.removeLegend();
		    final ChartPanel chartPanel = new ChartPanel(chart);
		    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		    setContentPane(chartPanel);
	}
	
	public static void main(final String[] args) 
	{
	    final view demo = new view("XY Series Demo");
	    demo.pack();
	    RefineryUtilities.centerFrameOnScreen(demo);
	    demo.setVisible(true);
		
		demo.testingSimple();
	}
}