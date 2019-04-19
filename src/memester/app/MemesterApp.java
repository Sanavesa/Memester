package memester.app;

import java.io.File;
import java.util.List;

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
		cluster2PCATab = new Tab("Cluster2PCA");
		
		tabPane = new TabPane(rdf2WalkTab, walk2VecTab, vec2ClusterTab, cluster2PCATab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		scene = new Scene(tabPane, 640, 480);
		stage.setScene(scene);
		stage.show();
		stage.setTitle("Memester");
		
		setupRDF2Walk();
	}
	
	Dataset dataset = null;
	File ontologyDirectory = null;
	TextField textFieldNodesToWalk = null;
	TextField textFieldWalksPerNode = null;
	TextField textFieldWalkDepth = null;
	
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
	
	private Stage stage;
	private Scene scene;
	private TabPane tabPane;
	private Tab rdf2WalkTab;
	private Tab walk2VecTab;
	private Tab vec2ClusterTab;
	private Tab cluster2PCATab;
}
