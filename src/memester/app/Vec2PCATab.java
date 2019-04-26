package memester.app;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tab;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import memester.vec2pca.MemeVector;
import memester.vec2pca.Vec2PCA;

public class Vec2PCATab extends Tab
{
	public Vec2PCATab()
	{
		setText("Vec2PCA");
		
		root = new VBox();
		vectorsLabel = new Label("Vectors Directory");
		vectorsLoadedLabel = new Label("No directory. Drag directory here.");
		browseButton = new Button("Browse");
		exportButton = new Button("Export as CSV");
		computePCAButton = new Button("Compute PCA");
		vectorsDirectoryChooser = new DirectoryChooser();
		exportFileChooser = new FileChooser();
		
		initializeLayout();
		setVectorsDirectory(null);
	}
	
	private void initializeLayout()
	{
		// Configure layout
		HBox row1 = new HBox(30, vectorsLabel, vectorsLoadedLabel, browseButton);
		HBox row2 = new HBox(30, exportButton, computePCAButton);
		
		root.getChildren().addAll(row1, row2);
		root.setPadding(new Insets(30));
		root.setSpacing(30);
		setContent(root);
		
		// Initialize each node
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		exportFileChooser.getExtensionFilters().add(extFilter);
		
		vectorsDirectoryChooser.setTitle("Vectors Directory");
		
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
                			setVectorsDirectory(file);
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
		computePCAButton.setOnAction(e -> onComputePCAButtonPressed());
		exportButton.setOnAction(e -> onExportButtonClicked());
	}
	
	private void onBrowseButtonButtonPressed()
	{
		Window window = getTabPane().getScene().getWindow();
		File directory = vectorsDirectoryChooser.showDialog(window);
		setVectorsDirectory(directory);
	}
	
	private void onExportButtonClicked()
	{
		Window window = getTabPane().getScene().getWindow();
		File file = exportFileChooser.showSaveDialog(window);
		if(file != null)
		{
			try(PrintWriter writer = new PrintWriter(file))
			{
				writer.println("x, y, iri");
				for(MemeVector vec : vectors)
				{
					writer.println(vec.vectorPCA[0] + "," + vec.vectorPCA[1] + "," + vec.IRI);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void onComputePCAButtonPressed()
	{
		vectors = Vec2PCA.readVectors(vectorsDirectory);
		Vec2PCA.computePCA(vectors);
	}
	
	private void setVectorsDirectory(File directory)
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
		computePCAButton.setDisable(directory == null);
		exportButton.setDisable(directory == null);
	}
	
	private File vectorsDirectory;
	private List<MemeVector> vectors;
	
	private final VBox root;
	private final FileChooser exportFileChooser;
	private final DirectoryChooser vectorsDirectoryChooser;
	private final Label vectorsLabel;
	private final Label vectorsLoadedLabel;
	private final Button browseButton;
	private final Button computePCAButton;
	private final Button exportButton;
}