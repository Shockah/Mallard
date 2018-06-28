package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteController extends Controller {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	protected final SpriteFramesController framesController;

	@Nonnull
	protected final SpriteAnimationsController animationsController;

	public SpriteController(@Nonnull SpriteProject project) {
		this.project = project;

		framesController = new SpriteFramesController(project);
		animationsController = new SpriteAnimationsController(project);

		setView(new BorderPane() {{
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
}