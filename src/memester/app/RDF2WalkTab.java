package memester.app;

import java.io.File;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import org.apache.jena.query.Dataset;

import memester.rdf2walk.GraphCreator;
import memester.rdf2walk.GraphNode;
import memester.rdf2walk.GraphWalker;
import memester.rdf2walk.RDFLoader;
import memester.rdf2walk.RandomWalker;
import memester.rdf2walk.Walk;
import memester.rdf2walk.WalkExporter;

public class RDF2WalkTab extends Tab
{
	public RDF2WalkTab()
	{
		setText("RDF2Walk");
		
		root = new VBox();
		ontologyLabel = new Label("Ontology Directory");
		ontologyLoadedLabel = new Label("No directory. Drag directory here.");
		browseButton = new Button("Browse");
		walksDepthLabel = new Label("Walks Depth");
		walksDepthSlider = new Slider(3, 20, 5);
		numWalksLabel = new Label("Number of Nodes to Walk");
		numWalksTextField = new TextField("100");
		walksPerNodeLabel = new Label("Number of Walks per Node");
		walksPerNodeTextField = new TextField("50");
		exportButton = new Button("Export");
		walkButton = new Button("Walk");
		exportProgressIndicator = new ProgressIndicator(0);
		ontologyDirectoryChooser = new DirectoryChooser();
		exportFileChooser = new  FileChooser();
		
		initializeLayout();
		setOntologyDirectory(null);
	}
	
	private void initializeLayout()
	{
		// Configure layout
		HBox row1 = new HBox(30, ontologyLabel, ontologyLoadedLabel, browseButton);
		HBox row2 = new HBox(30, walksDepthLabel, walksDepthSlider);
		HBox row3 = new HBox(30, numWalksLabel, numWalksTextField);
		HBox row4 = new HBox(30, walksPerNodeLabel, walksPerNodeTextField);
		HBox row5 = new HBox(30, exportButton, walkButton, exportProgressIndicator);
		
		root.getChildren().addAll(row1, row2, row3, row4, row5);
		root.setPadding(new Insets(30));
		root.setSpacing(30);
		setContent(root);
		
		// Initialize each node
		
		ontologyDirectoryChooser.setTitle("Ontology Directory");
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("WALK files (*.walk)", "*.walk");
		exportFileChooser.getExtensionFilters().add(extFilter);
		
		ontologyLoadedLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
		ontologyLoadedLabel.setPadding(new Insets(10));
		ontologyLoadedLabel.setStyle("-fx-border-radius: 1; -fx-border-width: 1; -fx-border-color: black; -fx-text-fill: black;");
		ontologyLoadedLabel.setMaxWidth(300);
		ontologyLoadedLabel.setOnDragOver(new EventHandler<DragEvent>()
		{
            @Override
            public void handle(DragEvent event)
            {
            	Dragboard db = event.getDragboard();
                if (event.getGestureSource() != ontologyLoadedLabel && db.hasFiles())
                {
                	boolean isDirectory = false;
                	for(File file : db.getFiles())
                	{
                		if(file.isDirectory())
                		{
                			isDirectory = true;
                		}
                	}
                	
                	if(isDirectory)
                	{
                		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                	}
                }
                event.consume();
            }
        });

		ontologyLoadedLabel.setOnDragDropped(new EventHandler<DragEvent>()
		{
            @Override
            public void handle(DragEvent event)
            {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles())
                {
                	for(File file : db.getFiles())
                	{
                		if(file.isDirectory())
                		{
                			setOntologyDirectory(file);
                			success = true;
                			break;
                		}
                	}
                }
                
                event.setDropCompleted(success);
                event.consume();
            }
        });
		
		browseButton.setOnAction(e -> onBrowseButtonButtonPressed());
		
		walksDepthSlider.setMajorTickUnit(1);
		walksDepthSlider.setShowTickLabels(true);
		walksDepthSlider.setShowTickMarks(true);
		walksDepthSlider.setMinorTickCount(0);
		walksDepthSlider.setSnapToTicks(true);
		walksDepthSlider.setMinWidth(300);
		
		numWalksTextField.textProperty().addListener((args, oldValue, newValue) ->
		{
			try
			{
				int value = Integer.parseInt(newValue);
				if(value < 0)
				{
					throw new NumberFormatException();
				}
			}
			catch(NumberFormatException e)
			{
				numWalksTextField.setText(oldValue);
			}
		});
		
		walksPerNodeTextField.textProperty().addListener((args, oldValue, newValue) ->
		{
			try
			{
				int value = Integer.parseInt(newValue);
				if(value < 0)
				{
					throw new NumberFormatException();
				}
			}
			catch(NumberFormatException e)
			{
				walksPerNodeTextField.setText(oldValue);
			}
		});
		
		exportButton.setOnAction(e -> onExportButtonPressed());
		
		walkButton.setOnAction(e -> onWalkButtonPressed());
		
		exportProgressIndicator.setVisible(false);
	}
	
	private void onBrowseButtonButtonPressed()
	{
		Window window = getTabPane().getScene().getWindow();
		File directory = ontologyDirectoryChooser.showDialog(window);
		setOntologyDirectory(directory);
	}
	
	private void onExportButtonPressed()
	{
		Window window = getTabPane().getScene().getWindow();
		File file = exportFileChooser.showSaveDialog(window);
		if(file != null)
		{
			WalkExporter.export(file.getAbsolutePath(), walks);
		}
	}
	
	private void onWalkButtonPressed()
	{
		showProgressIndicator(true);
		int nodesToWalk = Integer.parseInt(numWalksTextField.getText());
		int numWalksPerNode = Integer.parseInt(walksPerNodeTextField.getText());
		int depth = (int)walksDepthSlider.getValue();
		
		new Thread(() ->
		{
			dataset = RDFLoader.loadFiles(ontologyDirectory.getAbsolutePath(), "Meme Ontology");
			List<GraphNode> graph = GraphCreator.createGraph(dataset);
			GraphWalker walker = new RandomWalker();
			Platform.runLater(() -> updateProgressIndicator(0));
			walks = walker.walk(graph, nodesToWalk, numWalksPerNode, depth);
			Platform.runLater(() -> updateProgressIndicator(1));
		}).start();
	}
	
	private void setOntologyDirectory(File directory)
	{
		ontologyDirectory = directory;
		if(directory == null)
		{
			ontologyLoadedLabel.setText("No directory. Drag directory here.");
		}
		else
		{
			ontologyLoadedLabel.setText(ontologyDirectory.getAbsolutePath());
		}
		exportButton.setDisable(directory == null);
		walkButton.setDisable(directory == null);
	}
	
	private void showProgressIndicator(boolean show)
	{
		exportProgressIndicator.setVisible(show);
	}
	
	private void updateProgressIndicator(double progress)
	{
		exportProgressIndicator.setProgress(progress);
	}
	
	private File ontologyDirectory;
	private Dataset dataset;
	private Walk[] walks;
	
	private final VBox root;
	private final FileChooser exportFileChooser;
	private final DirectoryChooser ontologyDirectoryChooser;
	private final Label ontologyLabel;
	private final Label ontologyLoadedLabel;
	private final Button browseButton;
	private final Label walksDepthLabel;
	private final Slider walksDepthSlider;
	private final Label numWalksLabel;
	private final TextField numWalksTextField;
	private final Label walksPerNodeLabel;
	private final TextField walksPerNodeTextField;
	private final Button exportButton;
	private final Button walkButton;
	private final ProgressIndicator exportProgressIndicator;
}