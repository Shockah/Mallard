package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramePreviewController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Frame frame;

	public SpriteFramePreviewController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.spriteController = spriteController;
		this.project = project;
		this.frame = frame;

		setView(new TitledPane("Frame Preview", new SpriteFramePreviewView(frame)) {{
			setPadding(new Insets(8));
			setCollapsible(false);
			setMaxHeight(Double.MAX_VALUE);
		}});
	}
}