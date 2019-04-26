package memester.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception
	{
		CustomBorderPane root = new CustomBorderPane();
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.setResizable(true);
		stage.setMinWidth(Constants.APP_MIN_WIDTH);
		stage.setMinHeight(Constants.APP_MIN_HEIGHT);
		stage.setTitle(Constants.APP_NAME + " - Version " + Constants.APP_VERSION);
		stage.show();
	}
}
