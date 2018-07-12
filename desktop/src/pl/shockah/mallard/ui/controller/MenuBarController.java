package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import pl.shockah.mallard.project.Project;

public class MenuBarController extends Controller {
	@Nonnull
	public final AppController appController;

	@Nonnull
	private final MenuBar menuBar;

	public MenuBarController(@Nonnull AppController appController) {
		this.appController = appController;

		menuBar = new MenuBar() {{
			setUseSystemMenuBar(true);
		}};
		setView(menuBar);
		setup();

		appController.project.addListener((observable, oldValue, newValue) -> {
			setup();
		});
	}

	public void setup() {
		Menu fileMenu;

		menuBar.getMenus().removeAll(menuBar.getMenus());
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