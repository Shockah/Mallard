package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteController extends Controller {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	protected final SpriteFramesController framesController;

	@Nonnull
	protected final SpriteAnimationsController animationsController;

	@Nonnull
	protected final BorderPane borderPane;

	public SpriteController(@Nonnull SpriteProject project) {
		this.project = project;

		framesController = new SpriteFramesController(this, project);
		animationsController = new SpriteAnimationsController(this, project);

		setView(borderPane = new BorderPane() {{
			setLeft(new VBox(8) {{
				setPadding(new Insets(8));

				getChildren().add(new TitledPane("Frames", new HBox() {{
					setPadding(new Insets(4));
					getChildren().add(framesController.getView());
				}}) {{
					setCollapsible(false);
				}});

				getChildren().add(new TitledPane("Animations", new HBox() {{
					setPadding(new Insets(4));
					getChildren().add(animationsController.getView());
				}}) {{
					setCollapsible(false);
				}});
			}});
		}});
	}

	public void setCenterPanel(Region view) {
		borderPane.setCenter(view);
	}

	public void setRightPanel(Region view) {
		borderPane.setRight(view);
	}
}