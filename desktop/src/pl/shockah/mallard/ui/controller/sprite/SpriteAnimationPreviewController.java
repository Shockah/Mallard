package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteAnimationPreviewController extends AbstractSpritePreviewController {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Animation.Entry animationEntry;

	@Nonnull
	private final ListChangeListener<SpriteProject.Animation.Frame> animationFrameListListener;

	public SpriteAnimationPreviewController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Animation.Entry animationEntry) {
		this.spriteController = spriteController;
		this.project = project;
		this.animationEntry = animationEntry;

		if (!animationEntry.animation.frames.isEmpty())
			frame.setValue(animationEntry.animation.frames.get(0).frame);

		setView(new TitledPane("Animation Preview", new VBox() {{
			getChildren().add(canvas);
			VBox.setVgrow(canvas, Priority.ALWAYS);
		}}) {{
			setPadding(new Insets(8));
			setCollapsible(false);
			setMaxHeight(Double.MAX_VALUE);
		}});

		animationFrameListListener = c -> {
			if (c.next())
				playAnimation();
		};
	}

	@Override
	protected void onAddedToScene(@Nonnull Scene scene) {
		super.onAddedToScene(scene);
		animationEntry.animation.frames.addListener(animationFrameListListener);
	}

	@Override
	protected void onRemovedFromScene(@Nonnull Scene scene) {
		super.onRemovedFromScene(scene);
		animationEntry.animation.frames.removeListener(animationFrameListListener);
	}

	public void playAnimation() {
	}
}