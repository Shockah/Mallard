package pl.shockah.mallard.ui.controller.sprite.editor;

import javax.annotation.Nonnull;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import pl.shockah.godwit.geom.Circle;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.sprite.SpriteFramePreviewController;

public class CircleEditor extends ShapeEditor<Circle> {
	private boolean hasPointSet = false;

	@Nonnull
	private Vec2 point = Vec2.zero;

	@Nonnull
	private Vec2 radiusPoint = Vec2.zero;

	private float radius;

	public CircleEditor(@Nonnull SpriteProject.Frame frame, @Nonnull SpriteProject.Frame.ShapeEntry<Circle> entry) {
		super(frame, entry);
	}

	@Override
	public void onBecameActive() {
		super.onBecameActive();
		hasPointSet = false;
	}

	@Override
	public void onMouseMove(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY) {
		super.onMouseMove(previewController, mouseX, mouseY);

		if (hasPointSet) {
			radiusPoint = new Vec2((float)mouseX, (float)mouseY);
			radius = radiusPoint.subtract(point).getLength();
		} else {
			point = new Vec2((float)mouseX, (float)mouseY);
		}
		previewController.canvas.redraw();
	}

	@Override
	public void onMouseClick(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY, @Nonnull MouseButton button) {
		super.onMouseClick(previewController, mouseX, mouseY, button);

		if (hasPointSet) {
			radiusPoint = new Vec2((float)mouseX, (float)mouseY);
			radius = radiusPoint.subtract(point).getLength();
			entry.shape.setValue(new Circle(point, radius));
			frame.currentEditor.setValue(null);
		} else {
			point = new Vec2((float)mouseX, (float)mouseY);
			hasPointSet = true;
		}
		previewController.canvas.redraw();
	}

	@Override
	public void draw(@Nonnull SpriteFramePreviewController previewController, @Nonnull GraphicsContext context) {
		super.draw(previewController, context);

		double x0 = previewController.getLeft();
		double y0 = previewController.getTop();
		double scale = previewController.getImageScale();

		context.setStroke(entry.color.getValue());
		context.setFill(entry.color.getValue().deriveColor(0.0, 1.0, 1.0, 0.5));

		if (isActive()) {
			if (hasPointSet) {
				context.fillOval(x0 + (point.x - radius) * scale, y0 + (point.y - radius) * scale, radius * 2 * scale, radius * 2 * scale);
				context.strokeOval(x0 + (point.x - radius) * scale, y0 + (point.y - radius) * scale, radius * 2 * scale, radius * 2 * scale);

				context.strokeLine(x0 + point.x * scale, y0 + point.y * scale, x0 + radiusPoint.x * scale, y0 + radiusPoint.y * scale);
			} else {
				context.strokeLine(x0 + point.x * scale - 8, y0 + point.y * scale, x0 + point.x * scale + 8, y0 + point.y * scale);
				context.strokeLine(x0 + point.x * scale, y0 + point.y * scale - 8, x0 + point.x * scale, y0 + point.y * scale + 8);
			}
		} else {
			if (!entry.visible.get())
				return;

			Circle shape = entry.shape.getValue();
			if (shape != null) {
				context.fillOval(x0 + (shape.position.x - shape.radius) * scale, y0 + (shape.position.y - shape.radius) * scale, shape.radius * 2 * scale, shape.radius * 2 * scale);
				context.strokeOval(x0 + (shape.position.x - shape.radius) * scale, y0 + (shape.position.y - shape.radius) * scale, shape.radius * 2 * scale, shape.radius * 2 * scale);
			}
		}
	}
}