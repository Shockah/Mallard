package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarController extends Controller {
	@Nonnull
	public final MenuBar menuBar;

	@Nonnull
	public final AppController appController;

	public MenuBarController(@Nonnull AppController appController) {
		this.appController = appController;
		setView(menuBar = new MenuBar());
		menuBar.setUseSystemMenuBar(true);

		menuBar.getMenus().add(new Menu("File") {{
			getItems().add(new MenuItem("New Project") {{
				setOnAction(event -> appController.newAction());
			}});
			getItems().add(new MenuItem("Open") {{
				setOnAction(event -> appController.openAction());
			}});
			getItems().add(new MenuItem("Save") {{
				setOnAction(event -> appController.saveAction());
			}});
			getItems().add(new MenuItem("Save As...") {{
				setOnAction(event -> appController.saveAsAction());
			}});
		}});
	}
}