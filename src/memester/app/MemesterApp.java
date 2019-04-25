package memester.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.jena.query.Dataset;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import memester.rdf2walk.GraphCreator;
import memester.rdf2walk.GraphNode;
import memester.rdf2walk.GraphWalker;
import memester.rdf2walk.RDFLoader;
import memester.rdf2walk.RandomWalker;
import memester.rdf2walk.Walk;
import memester.rdf2walk.WalkExporter;
import memester.vec2cluster.Cluster;
import memester.vec2cluster.KMeans;
import memester.vec2cluster.LabeledVector;
import memester.vec2cluster.Vector;

public class MemesterApp extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception
	{
		this.stage = stage;
		// Turn off JENA's logging
		Logger.getRootLogger().setLevel(Level.OFF);
				
		rdf2WalkTab = new Tab("RDF2Walk");
		walk2VecTab = new Tab("Walk2Vec");
		vec2ClusterTab = new Tab("Vec2Cluster");
//		cluster2PCATab = new Tab("Cluster2PCA");
		
		tabPane = new TabPane(rdf2WalkTab, walk2VecTab, vec2ClusterTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		scene = new Scene(tabPane, 640, 480);
		stage.setScene(scene);
		stage.show();
		stage.setTitle("Memester");
		
		setupRDF2Walk();
		setupWalk2Vec();
		setupVec2Cluster();
	}
	
	private void setupRDF2Walk()
	{
		DirectoryChooser directoryChooserOntology = new DirectoryChooser();
		
		FileChooser fileChooserWalks = new FileChooser(); 
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooserWalks.getExtensionFilters().add(extFilter);
		
		Button btnBrowseOntology = new Button("Browse Ontology");
		btnBrowseOntology.setOnAction(e ->
		{
			ontologyDirectory = directoryChooserOntology.showDialog(stage);
		});
		
		Button btnGenerateWalks = new Button("Generate Walks");
		btnGenerateWalks.setOnAction(e ->
		{
			if(ontologyDirectory == null)
				return;
			
			int nodesToWalk = Integer.parseInt(textFieldNodesToWalk.getText());
			int numWalksPerNode = Integer.parseInt(textFieldWalksPerNode.getText());
			int depth = Integer.parseInt(textFieldWalkDepth.getText());
			
			Walk[] walks = generateWalks(ontologyDirectory.getPath(), nodesToWalk, numWalksPerNode, depth);
			fileChooserWalks.setInitialFileName("Walks.txt");
			File exportFile = fileChooserWalks.showSaveDialog(stage);
			saveWalks(exportFile.getPath(), walks);
		});
		
		Label labelNodesToWalk = new Label("Number of Nodes to Walk");
		textFieldNodesToWalk = new TextField("10");
		textFieldNodesToWalk.textProperty().addListener((args, oldV, newV) ->
		{
			try
			{
				int x = Integer.parseInt(textFieldNodesToWalk.getText());
				if(x <= 0)
					throw new NumberFormatException();
			}
			catch(NumberFormatException e)
			{
				textFieldNodesToWalk.setText(oldV);
			}
		});
		
		Label labelNumberOfWalksPerNode = new Label("Number of Walks per Node");
		textFieldWalksPerNode = new TextField("10");
		textFieldWalksPerNode.textProperty().addListener((args, oldV, newV) ->
		{
			try
			{
				int x = Integer.parseInt(textFieldWalksPerNode.getText());
				if(x <= 0)
					throw new NumberFormatException();
			}
			catch(NumberFormatException e)
			{
				textFieldWalksPerNode.setText(oldV);
			}
		});
		
		Label labelWalkDepth = new Label("Walk Depth");
		textFieldWalkDepth = new TextField("5");
		textFieldWalkDepth.textProperty().addListener((args, oldV, newV) ->
		{
			try
			{
				int x = Integer.parseInt(textFieldWalkDepth.getText());
				if(x <= 0)
					throw new NumberFormatException();
			}
			catch(NumberFormatException e)
			{
				textFieldWalkDepth.setText(oldV);
			}
		});
		
		VBox vbox = new VBox(btnBrowseOntology, btnGenerateWalks, labelNodesToWalk, textFieldNodesToWalk, 
				labelNumberOfWalksPerNode, textFieldWalksPerNode, labelWalkDepth, textFieldWalkDepth);
		rdf2WalkTab.setContent(vbox);
	}
	
	private Walk[] generateWalks(String ontologyDirectory, int nodesToWalk, int numWalksPerNode, int depth)
	{
		// Load RDF & create a graph representation
		dataset = RDFLoader.loadFiles(ontologyDirectory, "Meme Ontology");
		List<GraphNode> graph = GraphCreator.createGraph(dataset);
		
		// Create a random walker and generate them
		GraphWalker walker = new RandomWalker();
		Walk[] walks = walker.walk(graph, nodesToWalk, numWalksPerNode, depth);
		return walks;
	}
	
	private void saveWalks(String filename, Walk[] walks)
	{
		// Export walks
		WalkExporter.export(filename, walks);
	}
	
	private void setupWalk2Vec()
	{
		Button btnVectorize = new Button("Vectorize");
		btnVectorize.setOnAction(e ->
		{
			try
			{
				Runtime.getRuntime().exec("C:\\Users\\Sanavesa\\Desktop\\Meeting Files\\Walk2Vec\\dist\\Walk2Vec\\Walk2Vec.exe", null, new File("C:\\Users\\Sanavesa\\Desktop\\Meeting Files\\Walk2Vec\\dist\\Walk2Vec\\"));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		});
		
		VBox vbox = new VBox(btnVectorize);
		walk2VecTab.setContent(vbox);
	}
	
	private void setupVec2Cluster()
	{
		DirectoryChooser directoryChooserVectors = new DirectoryChooser();
		
		Label labelNumberOfClusters = new Label("Number of Clusters");
		textFieldNumberOfClusters = new TextField("10");
		textFieldNumberOfClusters.textProperty().addListener((args, oldV, newV) ->
		{
			try
			{
				int x = Integer.parseInt(textFieldNumberOfClusters.getText());
				if(x <= 0)
					throw new NumberFormatException();
			}
			catch(NumberFormatException e1)
			{
				textFieldNumberOfClusters.setText(oldV);
			}
		});
		
		Label labelNumberOfIterations = new Label("Number of Iterations");
		textFieldNumberOfIterations = new TextField("10");
		textFieldNumberOfIterations.textProperty().addListener((args, oldV, newV) ->
		{
			try
			{
				int x = Integer.parseInt(textFieldNumberOfIterations.getText());
				if(x <= 0)
					throw new NumberFormatException();
			}
			catch(NumberFormatException e1)
			{
				textFieldNumberOfIterations.setText(oldV);
			}
		});
		
		Button btnClusterize = new Button("Clusterize");
		btnClusterize.setOnAction(e ->
		{
			File vectorDirectory = directoryChooserVectors.showDialog(stage);
			
			List<LabeledVector> vectors = readVectors(vectorDirectory);
			int dimensions = vectors.get(0).getDimensions();
			
			int clusterCount = Integer.parseInt(textFieldNumberOfClusters.getText());
			int iterations = Integer.parseInt(textFieldNumberOfIterations.getText());
			
			KMeans kmeans = new KMeans(clusterCount, dimensions, vectors.toArray(new LabeledVector[vectors.size()]));
			for(int i = 0; i < iterations; i++)
			{
				System.out.println("Cluster Iteration " + (i+1) + " / " + iterations);
				kmeans.update();
			}
			
			System.out.println("Done clustering!");
			
			System.out.println("Exporting clusters!");
			
			FileChooser fileChooserWalks = new FileChooser(); 
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
			fileChooserWalks.getExtensionFilters().add(extFilter);
			fileChooserWalks.setInitialFileName("Clusters.txt");
			File exportFile = fileChooserWalks.showSaveDialog(stage);
			
			try(PrintWriter writer = new PrintWriter(exportFile))
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
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		});
		
		VBox vbox = new VBox(labelNumberOfClusters, textFieldNumberOfClusters, labelNumberOfIterations, textFieldNumberOfIterations, btnClusterize);
		vec2ClusterTab.setContent(vbox);
	}
	
	public List<LabeledVector> readVectors(File folder)
	{
		List<LabeledVector> vectors = new ArrayList<>();
		System.out.println("Starting to read vectors");
		
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
		
		System.out.println("Done reading");
		
		return vectors;
	}
	
	private Stage stage;
	private Scene scene;
	private TabPane tabPane;
	private Tab rdf2WalkTab;
	private Tab walk2VecTab;
	private Tab vec2ClusterTab;
	private Tab cluster2PCATab;
	
	Dataset dataset = null;
	File ontologyDirectory = null;
	TextField textFieldNodesToWalk = null;
	TextField textFieldWalksPerNode = null;
	TextField textFieldWalkDepth = null;
	TextField textFieldNumberOfClusters = null;
	TextField textFieldNumberOfIterations = null;
}
