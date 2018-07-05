package pl.shockah.mallard;

import javax.annotation.Nonnull;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import pl.shockah.godwit.geom.Circle;
import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.godwit.geom.polygon.Polygon;
import pl.shockah.mallard.project.Project;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.project.SpriteProjectSerializer;
import pl.shockah.mallard.ui.controller.AppController;

public class Mallard extends Application {
	@Getter
	private static Stage stage;

	@Nonnull
	public static final ShapeManager shapeManager = new ShapeManager();

	@Nonnull
	public static final JSONSerializationManager<Project> projectSerializationManager = new JSONSerializationManager<>();

	public static void main(String[] args) {
		launch(args);
	}

	private void initialize() {
		shapeManager.register("Rectangle", Rectangle.class, new ShapeSerializer.RectangleSerializer(), shape -> null);
		shapeManager.register("Circle", Circle.class, new ShapeSerializer.CircleSerializer(), shape -> null);
		shapeManager.register("Polygon", Polygon.class, new ShapeSerializer.PolygonSerializer(), shape -> null);

		projectSerializationManager.register(SpriteProject.class, new SpriteProjectSerializer(shapeManager.jsonSerializationManager));
	}

	@Override
	public void start(Stage stage) {
		initialize();

		Mallard.stage = stage;
		stage.setTitle("Mallard");
		stage.setScene(new Scene(new AppController().getView(), 1334, 750));
		stage.show();
	}
}