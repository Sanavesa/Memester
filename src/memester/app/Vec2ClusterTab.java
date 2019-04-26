package memester.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
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

import memester.vec2cluster.Cluster;
import memester.vec2cluster.KMeans;
import memester.vec2cluster.LabeledVector;
import memester.vec2cluster.Vector;

public class Vec2ClusterTab extends Tab
{
	public Vec2ClusterTab()
	{
		setText("Vec2Cluster");
		
		root = new VBox();
		vectorsLabel = new Label("Vectors Directory");
		vectorsLoadedLabel = new Label("No directory. Drag directory here.");
		browseButton = new Button("Browse");
		numClustersLabel = new Label("Number of Clusters");
		numClustersTextField = new TextField("50");
		numIterationsLabel = new Label("Number of Iterations");
		numIterationsTextField = new TextField("10");
		exportButton = new Button("Export");
		clusterButton = new Button("Cluster");
		exportProgressIndicator = new ProgressIndicator(0);
		vectorsDirectoryChooser = new DirectoryChooser();
		exportFileChooser = new  FileChooser();
		
		initializeLayout();
		setVectorDirectory(null);
	}
	
	private void initializeLayout()
	{
		// Configure layout
		HBox row1 = new HBox(30, vectorsLabel, vectorsLoadedLabel, browseButton);
		HBox row2 = new HBox(30, numClustersLabel, numClustersTextField);
		HBox row3 = new HBox(30, numIterationsLabel, numIterationsTextField);
		HBox row4 = new HBox(30, exportButton, clusterButton, exportProgressIndicator);
		
		root.getChildren().addAll(row1, row2, row3, row4);
		root.setPadding(new Insets(30));
		root.setSpacing(30);
		setContent(root);
		
		// Initialize each node
		
		vectorsDirectoryChooser.setTitle("Vectors Directory");
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CLUSTER files (*.cluster)", "*.cluster");
		exportFileChooser.getExtensionFilters().add(extFilter);
		
		vectorsLoadedLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
		vectorsLoadedLabel.setPadding(new Insets(10));
		vectorsLoadedLabel.setStyle("-fx-border-radius: 1; -fx-border-width: 1; -fx-border-color: black; -fx-text-fill: black;");
		vectorsLoadedLabel.setMaxWidth(300);
		vectorsLoadedLabel.setOnDragOver(new EventHandler<DragEvent>()
		{
            @Override
            public void handle(DragEvent event)
            {
            	Dragboard db = event.getDragboard();
                if (event.getGestureSource() != vectorsLoadedLabel && db.hasFiles())
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

		vectorsLoadedLabel.setOnDragDropped(new EventHandler<DragEvent>()
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
                			setVectorDirectory(file);
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
		
		numClustersTextField.textProperty().addListener((args, oldValue, newValue) ->
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
				numClustersTextField.setText(oldValue);
			}
		});
		
		numIterationsTextField.textProperty().addListener((args, oldValue, newValue) ->
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
				numIterationsTextField.setText(oldValue);
			}
		});
		
		exportButton.setOnAction(e -> onExportButtonPressed());
		
		clusterButton.setOnAction(e -> onClusterButtonPressed());
		
		exportProgressIndicator.setVisible(false);
	}
	
	private void onBrowseButtonButtonPressed()
	{
		Window window = getTabPane().getScene().getWindow();
		File directory = vectorsDirectoryChooser.showDialog(window);
		setVectorDirectory(directory);
	}
	
	private void onExportButtonPressed()
	{
		Window window = getTabPane().getScene().getWindow();
		File file = exportFileChooser.showSaveDialog(window);
		if(file != null)
		{
			try(PrintWriter writer = new PrintWriter(file))
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
		}
	}
	
	public List<LabeledVector> readVectors(File folder)
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
	
	private void onClusterButtonPressed()
	{
		showProgressIndicator(true);
		int numClusters = Integer.parseInt(numClustersTextField.getText());
		int numIterations = Integer.parseInt(numIterationsTextField.getText());
		
		new Thread(() ->
		{
			Platform.runLater(() -> updateProgressIndicator(0));
			List<LabeledVector> vectors = readVectors(vectorsDirectory);
			int dimensions = vectors.get(0).getDimensions();
			
			kmeans = new KMeans(numClusters, dimensions, vectors.toArray(new LabeledVector[vectors.size()]));
			for(int i = 0; i < numIterations; i++)
			{
				kmeans.update();
			}
			Platform.runLater(() -> updateProgressIndicator(1));
		}).start();
	}
	
	private void setVectorDirectory(File directory)
	{
		vectorsDirectory = directory;
		if(directory == null)
		{
			vectorsLoadedLabel.setText("No directory. Drag directory here.");
		}
		else
		{
			vectorsLoadedLabel.setText(vectorsDirectory.getAbsolutePath());
		}
		exportButton.setDisable(directory == null);
		clusterButton.setDisable(directory == null);
	}
	
	private void showProgressIndicator(boolean show)
	{
		exportProgressIndicator.setVisible(show);
	}
	
	private void updateProgressIndicator(double progress)
	{
		exportProgressIndicator.setProgress(progress);
	}
	
	private File vectorsDirectory;
	private KMeans kmeans;
	private final VBox root;
	private final FileChooser exportFileChooser;
	private final DirectoryChooser vectorsDirectoryChooser;
	private final Label vectorsLabel;
	private final Label vectorsLoadedLabel;
	private final Button browseButton;
	private final Label numClustersLabel;
	private final TextField numClustersTextField;
	private final Label numIterationsLabel;
	private final TextField numIterationsTextField;
	private final Button exportButton;
	private final Button clusterButton;
	private final ProgressIndicator exportProgressIndicator;
}