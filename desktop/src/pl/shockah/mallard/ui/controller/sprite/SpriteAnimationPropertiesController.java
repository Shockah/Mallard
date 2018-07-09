package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.Controller;

public class SpriteAnimationPropertiesController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteFramesController framesController;

//	@Nonnull
//	public final SpriteFramePreviewController previewController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Animation.Entry animationEntry;

	private ListChangeListener<SpriteProject.Animation.Entry> listChangeListener;

	public SpriteAnimationPropertiesController(@Nonnull SpriteController spriteController, @Nonnull SpriteFramesController framesController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Animation.Entry animationEntry) {
		this.spriteController = spriteController;
		this.framesController = framesController;
		this.project = project;
		this.animationEntry = animationEntry;

		setView(new VBox(8) {{
			setPadding(new Insets(8));

			getChildren().addAll(
					new TitledPane("Properties", new GridPane() {{
						setPadding(new Insets(4));
						setHgap(4);
						setVgap(4);
						setPrefWidth(250);

						add(new Label("Duration:"), 0, 0);
						add(new Spinner<Double>(0.0, Double.MAX_VALUE, 1.0, 0.05) {{
							getValueFactory().valueProperty().bindBidirectional(animationEntry.animation.duration.asObject());
						}}, 1, 0);
					}}) {{
						setCollapsible(false);
					}},
					new TitledPane("Frames", new SpriteAnimationFramesController(spriteController, framesController, project, animationEntry).getView()) {{
						setCollapsible(false);
					}}
			);
		}});
	}

	@Override
	protected void onAddedToScene(@Nonnull Scene scene) {
		super.onAddedToScene(scene);
		project.animations.addListener(listChangeListener = c -> {
			while (c.next()) {
				if (c.getRemoved().contains(animationEntry)) {
					spriteController.setRightPanel(null);
					spriteController.setCenterPanel(null);
				}
			}
		});
	}

	@Override
	protected void onRemovedFromScene(@Nonnull Scene scene) {
		super.onRemovedFromScene(scene);
		project.animations.removeListener(listChangeListener);
	}
}