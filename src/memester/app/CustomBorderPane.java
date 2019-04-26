package memester.app;

import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;

public class CustomBorderPane extends BorderPane
{
	public CustomBorderPane()
	{
		tabs = new TabPane();
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabs.getTabs().add(new RDF2WalkTab());
		tabs.getTabs().add(new Walk2VecTab());
		tabs.getTabs().add(new Vec2ClusterTab());
		tabs.getTabs().add(new Vec2PCATab());
		setTop(tabs);
	}

	private final TabPane tabs;
}