package memester.app;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProgressTest extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		MyTask t = new MyTask();
		ProgressBar bar = new ProgressBar(0);
		bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		bar.progressProperty().bind(t.progressProperty());
		bar.progressProperty().addListener((args, oldV, newV) ->
		{
			System.out.println(newV);
		});
		
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.progressProperty().bind(t.progressProperty());
		
		Button btnStart = new Button("Start");
		btnStart.setOnAction(e ->
		{
			new Thread(t).start();
		});
		VBox vbox = new VBox(20, btnStart, bar, indicator);
		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.show();
	}

}

class MyTask extends Task<Void>
{

	@Override
	protected Void call() throws Exception
	{
		final int max = 1000;
        for (int i=1; i<=max; i++)
        {
            if (isCancelled())
            {
               break;
            }
            Thread.sleep((int)(Math.random()*10));
            updateProgress(i, max);
        }
        return null;
	}
}