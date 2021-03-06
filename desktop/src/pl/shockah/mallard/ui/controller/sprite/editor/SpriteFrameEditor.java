package pl.shockah.mallard.ui.controller.sprite.editor;

import javax.annotation.Nonnull;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.sprite.SpriteFramePreviewController;

public class SpriteFrameEditor {
	@Nonnull
	public final SpriteProject.Frame frame;

	@Nonnull
	public final ChangeListener<SpriteFrameEditor> changeListener = (observable, oldValue, newValue) -> {
		if (oldValue == this && newValue != this)
			onBecameInactive();
		else if (oldValue != this && newValue == this)
			onBecameActive();
	};

	public SpriteFrameEditor(@Nonnull SpriteProject.Frame frame) {
		this.frame = frame;
	}

	public final boolean isActive() {
		return frame.currentEditor.getValue() == this;
	}

	public final void setActive() {
		frame.currentEditor.setValue(this);
	}

	public void onBecameActive() {
	}

	public void onBecameInactive() {
	}

	public void onCancel(@Nonnull SpriteFramePreviewController previewController) {
	}

	public void onMouseMove(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY) {
	}

	public void onMouseClick(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY, @Nonnull MouseButton button) {
		previewController.canvas.requestFocus();
	}

	public void draw(@Nonnull SpriteFramePreviewController previewController, @Nonnull GraphicsContext context) {
	}
}