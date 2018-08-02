package pl.shockah.mallard.ui.controller.sprite;

import java.text.DecimalFormat;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.javafx.Controller;

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

		setRoot(new VBox(8) {{
			setPadding(new Insets(8));

			getChildren().addAll(
					new TitledPane("Actions", new VBox(4) {{
						setPadding(new Insets(4));

						getChildren().addAll(
								new Button("Trim") {{
									setMaxWidth(Double.MAX_VALUE);
									setOnAction(event -> frame.trimImage());
								}}
						);
					}}) {{
						setCollapsible(false);
					}},
					new TitledPane("Properties", new GridPane() {{
						setPadding(new Insets(4));
						setHgap(4);
						setVgap(4);
						setPrefWidth(250);

						add(new Label("Origin:"), 0, 0);
						add(new Button() {{
							textProperty().bind(Bindings.createStringBinding(
									() -> String.format(
											"%s, %s",
											new DecimalFormat("#.##").format(frame.origin.getValue().x),
											new DecimalFormat("#.##").format(frame.origin.getValue().y)
									),
									frame.origin
							));
							setOnAction(event -> previewController.originEditor.setActive());
						}}, 1, 0);
					}}) {{
						setCollapsible(false);
					}},
					new TitledPane("Shapes", new SpriteFrameShapesController(previewController, project, frame).getRoot()) {{
						setCollapsible(false);
					}}
			);
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