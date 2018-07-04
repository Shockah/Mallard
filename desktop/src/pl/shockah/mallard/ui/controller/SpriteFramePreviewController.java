package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.view.SpriteFramePreviewView;

public class SpriteFramePreviewController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Frame frame;

	@Nonnull
	protected final SpriteFramePreviewView previewView;

	public SpriteFramePreviewController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.spriteController = spriteController;
		this.project = project;
		this.frame = frame;

		setView(new TitledPane("Frame Preview", previewView = new SpriteFramePreviewView(frame)) {{
			setPadding(new Insets(8));
			setCollapsible(false);
			setMaxHeight(Double.MAX_VALUE);
		}});
	}

	public void setEditingOrigin(boolean editing) {
		previewView.editingOrigin.set(editing);
	}
}