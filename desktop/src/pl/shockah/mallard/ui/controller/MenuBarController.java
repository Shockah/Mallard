package pl.shockah.mallard.ui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.annotation.Nonnull;

import javafx.beans.property.Property;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import pl.shockah.jay.JSONParser;
import pl.shockah.jay.JSONPrettyPrinter;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.project.Project;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.UnexpectedException;

public class MenuBarController extends Controller {
	@Nonnull
	public final MenuBar menuBar;

	@Nonnull
	public final Property<Project> project;

	public MenuBarController(@Nonnull Property<Project> project) {
		this.project = project;
		setView(menuBar = new MenuBar());
		menuBar.setUseSystemMenuBar(true);

		menuBar.getMenus().add(new Menu("File") {{
			getItems().add(new MenuItem("New Project") {{
				setOnAction(event -> newAction());
			}});
			getItems().add(new MenuItem("Open") {{
				setOnAction(event -> openAction());
			}});
			getItems().add(new MenuItem("Save") {{
				setOnAction(event -> saveAction());
			}});
			getItems().add(new MenuItem("Save As...") {{
				setOnAction(event -> saveAsAction());
			}});
		}});
	}

	private void newAction() {
		project.setValue(new SpriteProject());
	}

	private void openAction() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open");
		chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Mallard Sprite projects", "mlds"));

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

	private void saveAction() {
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

	private void saveAsAction() {
		if (project.getValue() == null)
			return;

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save As...");
		chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Mallard Sprite projects", "mlds"));

		File file = chooser.showSaveDialog(Mallard.getStage());
		if (file == null)
			return;

		if (!file.getName().endsWith(".mlds"))
			file = new File(file.getParent(), String.format("%s.mlds", file.getName()));

		project.getValue().file.setValue(file);
		saveAction();
	}
}