package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import pl.shockah.mallard.project.Project;
import pl.shockah.unicorn.javafx.Controller;

public class MenuBarController extends Controller {
	@InjectedParent
	private AppController appController;

	@FXML
	private MenuBar menuBar;

	@Override
	protected void onLoaded() {
		super.onLoaded();
		menuBar.setUseSystemMenuBar(true);

		setup();
		appController.project.addListener((observable, oldValue, newValue) -> {
			setup();
		});
	}

	public void setup() {
		Menu fileMenu;

		menuBar.getMenus().clear();
		menuBar.getMenus().add(fileMenu = new Menu("File") {{
			getItems().addAll(
					new MenuItem("New Project") {{
						setOnAction(event -> appController.newAction());
					}},
					new MenuItem("Open") {{
						setOnAction(event -> appController.openAction());
					}},
					new MenuItem("Save") {{
						setOnAction(event -> appController.saveAction());
						disableProperty().bind(Bindings.createBooleanBinding(() -> {
							return appController.project.getValue() == null;
						}, appController.project));
					}},
					new MenuItem("Save As...") {{
						setOnAction(event -> appController.saveAsAction());
						disableProperty().bind(Bindings.createBooleanBinding(() -> {
							return appController.project.getValue() == null;
						}, appController.project));
					}}
			);
		}});

		Project project = appController.project.getValue();
		if (project != null)
			project.setupMenuBar(menuBar, fileMenu);
	}
}