package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarController extends Controller {
	@Nonnull
	public final AppController appController;

	public MenuBarController(@Nonnull AppController appController) {
		this.appController = appController;
		setView(new MenuBar() {{
			setUseSystemMenuBar(true);

			getMenus().add(new Menu("File") {{
				getItems().addAll(
						new MenuItem("New Project") {{
							setOnAction(event -> appController.newAction());
						}},
						new MenuItem("Open") {{
							setOnAction(event -> appController.openAction());
						}},
						new MenuItem("Save") {{
							setOnAction(event -> appController.saveAction());
						}},
						new MenuItem("Save As...") {{
							setOnAction(event -> appController.saveAsAction());
						}}
				);
			}});
		}});
	}
}