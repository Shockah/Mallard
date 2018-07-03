package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.Project;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.BindUtilities;

public class AppController extends Controller {
	@Nonnull
	public final Property<Project> project = new SimpleObjectProperty<>(this, "project");

	@Nonnull
	private final VBox projectSpecificContainer;

	public AppController() {
		projectSpecificContainer = new VBox();

		setView(new VBox() {{
			setMaxHeight(Double.MAX_VALUE);
			getChildren().add(new MenuBarController(project).getView());
			getChildren().add(projectSpecificContainer);
		}});

		BindUtilities.bind(project, project -> setupProjectSpecificContainer());
	}

	public void setupProjectSpecificContainer() {
		projectSpecificContainer.getChildren().removeAll(projectSpecificContainer.getChildren());

		if (project.getValue() instanceof SpriteProject)
			projectSpecificContainer.getChildren().add(new SpriteController((SpriteProject)project.getValue()).getView());
	}
}