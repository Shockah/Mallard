package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramePropertiesController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Frame frame;

	private ListChangeListener<SpriteProject.Frame> listChangeListener;

	public SpriteFramePropertiesController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.spriteController = spriteController;
		this.project = project;
		this.frame = frame;

		setView(new TitledPane("Properties", new GridPane() {{
			setPadding(new Insets(4));
			setHgap(4);
			setVgap(4);

			add(new Label("Origin:"), 0, 0);
			add(new Button("0, 0"), 1, 0);
		}}) {{
			setPadding(new Insets(8));
			setCollapsible(false);
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