package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramePropertiesController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteFramePreviewController previewController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Frame frame;

	private ListChangeListener<SpriteProject.Frame> listChangeListener;

	public SpriteFramePropertiesController(@Nonnull SpriteController spriteController, @Nonnull SpriteFramePreviewController previewController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.spriteController = spriteController;
		this.previewController = previewController;
		this.project = project;
		this.frame = frame;

		setView(new VBox(8) {{
			setPadding(new Insets(8));

			getChildren().add(new TitledPane("Actions", new VBox(4) {{
				setPadding(new Insets(4));

				getChildren().add(new Button("Trim") {{
					setMaxWidth(Double.MAX_VALUE);
					setOnAction(event -> frame.trimImage());
				}});
			}}) {{
				setCollapsible(false);
			}});

			getChildren().add(new TitledPane("Properties", new GridPane() {{
				setPadding(new Insets(4));
				setHgap(4);
				setVgap(4);
				setPrefWidth(250);

				add(new Label("Origin:"), 0, 0);
				add(new Button() {{
					textProperty().bind(Bindings.createStringBinding(
							() -> String.format("%.0f, %.0f", frame.origin.getValue().x, frame.origin.getValue().y),
							frame.origin
					));
					setOnAction(event -> previewController.setEditingOrigin(true));
				}}, 1, 0);
			}}) {{
				setCollapsible(false);
			}});
		}});
	}

	@Override
	protected void onAddedToScene(@Nonnull Scene scene) {
		super.onAddedToScene(scene);
		project.frames.addListener(listChangeListener = c -> {
			while (c.next()) {
				if (c.getRemoved().contains(frame)) {
					spriteController.setRightPanel(null);
					spriteController.setCenterPanel(null);
				}
			}
		});
	}

	@Override
	protected void onRemovedFromScene(@Nonnull Scene scene) {
		super.onRemovedFromScene(scene);
		project.frames.removeListener(listChangeListener);
	}
}