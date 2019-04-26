package memester.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class Walk2VecTab extends Tab
{
	public Walk2VecTab()
	{
		setText("Walk2Vec");
		
		root = new VBox();
		walksLabel = new Label("Walk File");
		walksLoadedLabel = new Label("No file. Drag file here.");
		browseButton = new Button("Browse");
		
		algorithmLabel = new Label("Algorithm");
		skipGramRadioButton = new RadioButton("Skip-Gram");
		cbowRadioButton = new RadioButton("CBOW");
		algorithmToggleGroup = new ToggleGroup();
		workersLabel = new Label("Workers");
		workersSlider = new Slider(1, 10, 5);
		windowLabel = new Label("Window");
		windowSlider = new Slider(1, 20, 10);
		sizeLabel = new Label("Vector Size");
		sizeTextField = new TextField("500");
		negativeSamplingLabel = new Label("Negative Sampling Count");
		negativeSamplingTextField = new TextField("15");
		iterationLabel = new Label("Iterations");
		iterationTextField = new TextField("5");
		vectorizeButton = new Button("Vectorize");
		walkFileChooser = new FileChooser();
		
		initializeLayout();
	}
	
	private void initializeLayout()
	{
		// Configure layout
		HBox row1 = new HBox(30, walksLabel, walksLoadedLabel, browseButton);
		HBox row2 = new HBox(30, algorithmLabel, skipGramRadioButton, cbowRadioButton);
		HBox row3 = new HBox(30, sizeLabel, sizeTextField);
		HBox row4 = new HBox(30, workersLabel, workersSlider);
		HBox row5 = new HBox(30, windowLabel, windowSlider);
		HBox row6 = new HBox(30, negativeSamplingLabel, negativeSamplingTextField);
		HBox row7 = new HBox(30, iterationLabel, iterationTextField);
		HBox row8 = new HBox(30, vectorizeButton);
		
		root.getChildren().addAll(row1, row2, row3, row4, row5, row6, row7, row8);
		root.setPadding(new Insets(30));
		root.setSpacing(30);
		setContent(root);
		
		// Initialize each node
		walkFileChooser.setTitle("Walk File");
		
		FileChooser.ExtensionFilter walkExtFilter = new FileChooser.ExtensionFilter("WALK files (*.walk)", "*.walk");
		walkFileChooser.getExtensionFilters().add(walkExtFilter);
		
		walksLoadedLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
		walksLoadedLabel.setPadding(new Insets(10));
		walksLoadedLabel.setStyle("-fx-border-radius: 1; -fx-border-width: 1; -fx-border-color: black; -fx-text-fill: black;");
		walksLoadedLabel.setMaxWidth(300);
		walksLoadedLabel.setOnDragOver(new EventHandler<DragEvent>()
		{
            @Override
            public void handle(DragEvent event)
            {
            	Dragboard db = event.getDragboard();
                if (event.getGestureSource() != walksLoadedLabel && db.hasFiles())
                {
            		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

		walksLoadedLabel.setOnDragDropped(new EventHandler<DragEvent>()
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
            			setWalkFile(file);
            			success = true;
            			break;
                	}
                }
                
                event.setDropCompleted(success);
                event.consume();
            }
        });
		
		browseButton.setOnAction(e -> onBrowseButtonButtonPressed());
		
		skipGramRadioButton.setToggleGroup(algorithmToggleGroup);
		cbowRadioButton.setToggleGroup(algorithmToggleGroup);
		skipGramRadioButton.setSelected(true);
		
		workersSlider.setMajorTickUnit(1);
		workersSlider.setShowTickLabels(true);
		workersSlider.setShowTickMarks(true);
		workersSlider.setMinorTickCount(0);
		workersSlider.setSnapToTicks(true);
		workersSlider.setMinWidth(300);
		
		windowSlider.setMajorTickUnit(1);
		windowSlider.setShowTickLabels(true);
		windowSlider.setShowTickMarks(true);
		windowSlider.setMinorTickCount(0);
		windowSlider.setSnapToTicks(true);
		windowSlider.setMinWidth(300);
		
		sizeTextField.textProperty().addListener((args, oldValue, newValue) ->
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
				sizeTextField.setText(oldValue);
			}
		});
		
		negativeSamplingTextField.textProperty().addListener((args, oldValue, newValue) ->
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
				negativeSamplingTextField.setText(oldValue);
			}
		});
		
		vectorizeButton.setOnAction(e -> onVectorizeButtonPressed());
	}
	
	private void onBrowseButtonButtonPressed()
	{
		Window window = getTabPane().getScene().getWindow();
		
		File file = walkFileChooser.showOpenDialog(window);
		if(file != null && file.exists())
		{
			setWalkFile(file);
		}
	}
	
	private void onVectorizeButtonPressed()
	{
		String jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath();
		
		System.out.println(jarDir);
		
		// Load the config
		int iterations = Integer.parseInt(iterationTextField.getText());
		int negativeSampling = Integer.parseInt(negativeSamplingTextField.getText());
		int sg = cbowRadioButton.isSelected() ? 0 : 1;
		int size = Integer.parseInt(sizeTextField.getText());
		int window = (int)windowSlider.getValue();
		int workers = (int)workersSlider.getValue();
		
		try(PrintWriter writer = new PrintWriter(new File(jarDir + "\\Walk2Vec\\config.txt")))
		{
			writer.println("{");
			writer.println("\t\"iterations\": " + iterations + ",");
			writer.println("\t\"negative\": " + negativeSampling + ",");
			writer.println("\t\"sg\": " + sg + ",");
			writer.println("\t\"size\": " + size + ",");
			writer.println("\t\"window\": " + window + ",");
			writer.println("\t\"workers\": " + workers);
			writer.println("}");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			Runtime.getRuntime().exec(jarDir + "\\Walk2Vec\\Walk2Vec.exe", null, new File(jarDir + "\\Walk2Vec\\"));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}
	
	private void setWalkFile(File file)
	{
		walkFile = file;
		if(file == null)
		{
			walksLoadedLabel.setText("No file. Drag file here.");
		}
		else
		{
			walksLoadedLabel.setText(walkFile.getAbsolutePath());
		}
		vectorizeButton.setDisable(file == null);
	}
	
	private File walkFile;
	private final VBox root;
	private final FileChooser walkFileChooser;
	private final Label walksLabel;
	private final Label walksLoadedLabel;
	private final Button browseButton;
	private final Label algorithmLabel;
	private final RadioButton skipGramRadioButton;
	private final RadioButton cbowRadioButton;
	private final ToggleGroup algorithmToggleGroup;
	private final Label workersLabel;
	private final Slider workersSlider;
	private final Label windowLabel;
	private final Slider windowSlider;
	private final Label sizeLabel;
	private final TextField sizeTextField;
	private final Label negativeSamplingLabel;
	private final TextField negativeSamplingTextField;
	private final Label iterationLabel;
	private final TextField iterationTextField;
	private final Button vectorizeButton;
}