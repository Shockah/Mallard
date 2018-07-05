package pl.shockah.mallard.ui.controller.sprite.editor;

import javax.annotation.Nonnull;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.sprite.SpriteFramePreviewController;

public class OriginEditor extends SpriteFrameEditor {
	@Nonnull
	private Vec2 newOrigin = Vec2.zero;

	public OriginEditor(@Nonnull SpriteProject.Frame frame) {
		super(frame);
	}

	@Override
	public void onMouseMove(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY) {
		super.onMouseMove(previewController, mouseX, mouseY);
		newOrigin = new Vec2((float)mouseX, (float)mouseY);
		previewController.canvas.redraw();
	}

	@Override
	public void onMouseClick(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY, @Nonnull MouseButton button) {
		super.onMouseClick(previewController, mouseX, mouseY, button);
		if (button != MouseButton.PRIMARY)
			return;

		newOrigin = new Vec2((float)mouseX, (float)mouseY);
		frame.origin.setValue(newOrigin);
		frame.currentEditor.setValue(null);
	}

	@Override
	public void draw(@Nonnull SpriteFramePreviewController previewController, @Nonnull GraphicsContext context) {
		double scale = previewController.getImageScale();
		double x1 = previewController.getLeft();
		double y1 = previewController.getTop();

		context.setStroke(Color.RED);
		context.setLineWidth(1);

		Vec2 origin = frame.origin.getValue();
		if (isActive())
			origin = newOrigin;

		context.strokeLine(0, y1 + origin.y * scale, context.getCanvas().getWidth(), y1 + origin.y * scale);
		context.strokeLine(x1 + origin.x * scale, 0, x1 + origin.x * scale, context.getCanvas().getHeight());
	}
}