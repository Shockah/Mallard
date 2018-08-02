package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.javafx.Controller;

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
		animationsController = new SpriteAnimationsController(this, framesController, project);

		setRoot(borderPane = new BorderPane() {{
			setLeft(new VBox(8) {{
				setPadding(new Insets(8));

				getChildren().addAll(
						new TitledPane("Frames", new HBox() {{
							setPadding(new Insets(4));
							getChildren().add(framesController.getRoot());
						}}) {{
							setCollapsible(false);
						}},
						new TitledPane("Animations", new HBox() {{
							setPadding(new Insets(4));
							getChildren().add(animationsController.getRoot());
						}}) {{
							setCollapsible(false);
						}}
				);
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