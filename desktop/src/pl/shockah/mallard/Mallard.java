package pl.shockah.mallard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.SpriteController;

public class Mallard extends Application {
	@Getter
	private static Stage stage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Mallard.stage = stage;
		stage.setTitle("Mallard");

		BorderPane root = new BorderPane();
		root.setTop(new ToolBar(
				new Button("New"),
				new Button("Load"),
				new Button("Save")
		));
		root.setCenter(new SpriteController(new SpriteProject()).getView());

		stage.setScene(new Scene(root, 1334, 750));
		stage.show();
	}
}