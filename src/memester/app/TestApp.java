package memester.app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestApp extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Label label = new Label("Drag a file to me.");
        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label,dropped);
        dragTarget.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dragTarget
                        && event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        dragTarget.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    dropped.setText(db.getFiles().toString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });


        StackPane root = new StackPane();
        root.getChildren().add(dragTarget);

        Scene scene = new Scene(root, 500, 250);

        primaryStage.setTitle("Drag Test");
        primaryStage.setScene(scene);
        primaryStage.show();
	}
}