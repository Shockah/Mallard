package pl.shockah.mallard.ui.controller.sprite.editor;

import javax.annotation.Nonnull;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.sprite.SpriteFramePreviewController;

public class RectangleEditor extends ShapeEditor<Rectangle> {
	private int currentPoint = 0;

	@Nonnull
	private final Vec2[] points = new Vec2[2];

	public RectangleEditor(@Nonnull SpriteProject.Frame frame, @Nonnull SpriteProject.Frame.ShapeEntry<Rectangle> entry) {
		super(frame, entry);
	}

	@Override
	public void onBecameActive() {
		super.onBecameActive();
		currentPoint = 0;
		for (int i = 0; i < points.length; i++) {
			points[i] = Vec2.zero;
		}
	}

	@Override
	public void onMouseMove(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY) {
		super.onMouseMove(previewController, mouseX, mouseY);
		points[currentPoint] = new Vec2((float)mouseX, (float)mouseY);
		previewController.canvas.redraw();
	}

	@Override
	public void onMouseClick(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY, @Nonnull MouseButton button) {
		super.onMouseClick(previewController, mouseX, mouseY, button);
		points[currentPoint] = new Vec2((float)mouseX, (float)mouseY);
		currentPoint++;

		if (currentPoint >= points.length) {
			Rectangle result = new Rectangle(points[0], points[1].subtract(points[0]));
			if (result.size.x < 0) {
				result.size.x = -result.size.x;
				result.position.x -= result.size.x;
			}
			if (result.size.y < 0) {
				result.size.y = -result.size.y;
				result.position.y -= result.size.y;
			}
			entry.shape.setValue(result);
			frame.currentEditor.setValue(null);
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
			if (currentPoint == 0) {
				context.strokeLine(x0 + points[0].x * scale - 8, y0 + points[0].y * scale, x0 + points[0].x * scale + 8, y0 + points[0].y * scale);
				context.strokeLine(x0 + points[0].x * scale, y0 + points[0].y * scale - 8, x0 + points[0].x * scale, y0 + points[0].y * scale + 8);
			} else {
				double x1 = x0 + points[0].x * scale;
				double y1 = y0 + points[0].y * scale;
				double w = (points[1].x - points[0].x) * scale;
				double h = (points[1].y - points[0].y) * scale;

				if (w < 0) {
					w = -w;
					x1 -= w;
				}
				if (h < 0) {
					h = -h;
					y1 -= h;
				}

				context.fillRect(x1, y1, w, h);
				context.strokeRect(x1, y1, w, h);

				context.strokeLine(x0 + points[1].x * scale - 5, y0 + points[1].y * scale - 5, x0 + points[1].x * scale + 5, y0 + points[1].y * scale + 5);
				context.strokeLine(x0 + points[1].x * scale + 5, y0 + points[1].y * scale - 5, x0 + points[1].x * scale - 5, y0 + points[1].y * scale + 5);
			}
		} else {
			if (!entry.visible.get())
				return;

			Rectangle shape = entry.shape.getValue();
			if (shape != null) {
				context.fillRect(x0 + shape.position.x * scale, y0 + shape.position.y * scale, shape.size.x * scale, shape.size.y * scale);
				context.strokeRect(x0 + shape.position.x * scale, y0 + shape.position.y * scale, shape.size.x * scale, shape.size.y * scale);
			}
		}
	}
}