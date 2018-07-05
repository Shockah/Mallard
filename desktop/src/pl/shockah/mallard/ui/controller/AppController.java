package pl.shockah.mallard.ui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.annotation.Nonnull;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pl.shockah.jay.JSONParser;
import pl.shockah.jay.JSONPrettyPrinter;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.project.Project;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.BindUtilities;
import pl.shockah.mallard.ui.controller.sprite.SpriteController;
import pl.shockah.unicorn.UnexpectedException;

public class AppController extends Controller {
	@Nonnull
	public final Property<Project> project = new SimpleObjectProperty<>(this, "project");

	@Nonnull
	private final VBox projectSpecificContainer;

	public AppController() {
		projectSpecificContainer = new VBox();
		VBox.setVgrow(projectSpecificContainer, Priority.ALWAYS);

		setView(new VBox() {{
			setMaxHeight(Double.MAX_VALUE);
			getChildren().addAll(
					new MenuBarController(AppController.this).getView(),
					projectSpecificContainer
			);
		}});

		BindUtilities.bind(project, project -> setupProjectSpecificContainer());
	}

	public void setupProjectSpecificContainer() {
		projectSpecificContainer.getChildren().removeAll(projectSpecificContainer.getChildren());

		if (project.getValue() instanceof SpriteProject)
			projectSpecificContainer.getChildren().add(new SpriteController((SpriteProject)project.getValue()).getView());
		else
			projectSpecificContainer.getChildren().add(new WelcomeController(this).getView());
	}

	public void newAction() {
		project.setValue(new SpriteProject());
	}

	public void openAction() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open");
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mallard Sprite projects", "*.mlds"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));

		File file = chooser.showOpenDialog(Mallard.getStage());
		if (file == null)
			return;

		try {
			project.setValue(Mallard.projectSerializationManager.deserialize(new JSONParser().parseObject(new String(Files.readAllBytes(file.toPath()), "UTF-8"))));
			project.getValue().file.setValue(file);
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public void saveAction() {
		if (project.getValue() == null)
			return;

		if (project.getValue().file.getValue() == null) {
			saveAsAction();
			return;
		}

		File file = project.getValue().file.getValue();
		try {
			Files.write(file.toPath(), new JSONPrettyPrinter().toString(Mallard.projectSerializationManager.serialize(project.getValue())).getBytes("UTF-8"));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	public void saveAsAction() {
		if (project.getValue() == null)
			return;

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save As...");
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mallard Sprite projects", "*.mlds"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));

		File file = chooser.showSaveDialog(Mallard.getStage());
		if (file == null)
			return;

		if (!file.getName().endsWith(".mlds"))
			file = new File(file.getParent(), String.format("%s.mlds", file.getName()));

		project.getValue().file.setValue(file);
		saveAction();
	}
}