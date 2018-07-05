package pl.shockah.mallard.ui.controller.sprite.editor;

import javax.annotation.Nonnull;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import pl.shockah.godwit.geom.IVec2;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.geom.polygon.Polygon;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.sprite.SpriteFramePreviewController;

public class PolygonEditor extends ShapeEditor<Polygon> {
	@Nonnull
	private Polygon newPolygon = new Polygon();

	@Nonnull
	private Vec2 newPoint = Vec2.zero;

	public PolygonEditor(@Nonnull SpriteProject.Frame frame, @Nonnull SpriteProject.Frame.ShapeEntry<Polygon> entry) {
		super(frame, entry);
	}

	@Override
	public void onBecameActive() {
		super.onBecameActive();
		newPolygon = new Polygon();
		newPolygon.closed = true;
	}

	@Override
	public void onMouseMove(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY) {
		super.onMouseMove(previewController, mouseX, mouseY);
		newPoint = new Vec2((float)mouseX, (float)mouseY);
		previewController.canvas.redraw();
	}

	@Override
	public void onMouseClick(@Nonnull SpriteFramePreviewController previewController, double mouseX, double mouseY, @Nonnull MouseButton button) {
		super.onMouseClick(previewController, mouseX, mouseY, button);
		newPoint = new Vec2((float)mouseX, (float)mouseY);
		newPolygon.addPoint(newPoint);
		previewController.canvas.redraw();
	}

	@Override
	public void onCancel(@Nonnull SpriteFramePreviewController previewController) {
		super.onCancel(previewController);

		if (newPolygon.getPointCount() >= 3)
			entry.shape.setValue(newPolygon);
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
			for (int i = 0; i < newPolygon.getPointCount(); i++) {
				IVec2 previous = newPolygon.get(i);
				IVec2 current = i == newPolygon.getPointCount() - 1 ? newPoint : newPolygon.get(i + 1);
				context.strokeLine(x0 + previous.x() * scale, y0 + previous.y() * scale, x0 + current.x() * scale, y0 + current.y() * scale);
			}

			context.strokeLine(x0 + newPoint.x * scale - 8, y0 + newPoint.y * scale, x0 + newPoint.x * scale + 8, y0 + newPoint.y * scale);
			context.strokeLine(x0 + newPoint.x * scale, y0 + newPoint.y * scale - 8, x0 + newPoint.x * scale, y0 + newPoint.y * scale + 8);
		} else {
			Polygon shape = entry.shape.getValue();
			if (shape != null) {
				double[] xs = new double[shape.getPointCount()];
				double[] ys = new double[xs.length];
				for (int i = 0; i < xs.length; i++) {
					IVec2 point = shape.get(i);
					xs[i] = x0 + point.x() * scale;
					ys[i] = y0 + point.y() * scale;
				}
				context.fillPolygon(xs, ys, xs.length);
				context.strokePolygon(xs, ys, xs.length);
			}
		}
	}
}