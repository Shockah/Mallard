package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.BindUtilities;
import pl.shockah.mallard.ui.controller.Controller;
import pl.shockah.mallard.ui.view.ResizableCanvas;

public abstract class AbstractSpritePreviewController extends Controller {
	@Nonnull
	public final ResizableCanvas canvas;

	@Nonnull
	public final Property<SpriteProject.Frame> frame = new SimpleObjectProperty<>(this, "frame");

	public AbstractSpritePreviewController() {
		canvas = new ResizableCanvas() {
			{
				parentProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						BindUtilities.bind(((Region)newValue).widthProperty(), width -> setWidth(width.doubleValue()));
						BindUtilities.bind(((Region)newValue).heightProperty(), height -> setHeight(height.doubleValue()));
					}
				});

				frame.addListener((observable, oldValue, newValue) -> {
					redraw();
				});
			}

			@Override
			protected void draw(@Nonnull GraphicsContext context) {
				super.draw(context);
				AbstractSpritePreviewController.this.draw(context);
			}
		};
	}

	public double getImageScale() {
		double padding = 16;

		double scale = (canvas.getWidth() - padding) / frame.getValue().image.getValue().getWidth();
		if (frame.getValue().image.getValue().getHeight() * scale > (canvas.getHeight() - padding))
			scale = (canvas.getHeight() - padding) / frame.getValue().image.getValue().getHeight();
		return scale;
	}

	public double getLeft() {
		double scale = getImageScale();
		double width = frame.getValue().image.getValue().getWidth() * scale;
		return canvas.getWidth() * 0.5 - width * 0.5;
	}

	public double getRight() {
		double scale = getImageScale();
		double width = frame.getValue().image.getValue().getWidth() * scale;
		return canvas.getWidth() * 0.5 + width * 0.5;
	}

	public double getTop() {
		double scale = getImageScale();
		double height = frame.getValue().image.getValue().getHeight() * scale;
		return canvas.getHeight() * 0.5 - height * 0.5;
	}

	public double getBottom() {
		double scale = getImageScale();
		double height = frame.getValue().image.getValue().getHeight() * scale;
		return canvas.getHeight() * 0.5 + height * 0.5;
	}

	protected void draw(@Nonnull GraphicsContext context) {
		context.clearRect(0, 0, context.getCanvas().getWidth(), context.getCanvas().getHeight());
		drawGrid(context);
		drawImage(context);
	}

	private void drawGrid(@Nonnull GraphicsContext context) {
		double scale = getImageScale();
		double width = frame.getValue().image.getValue().getWidth() * scale;
		double height = frame.getValue().image.getValue().getHeight() * scale;
		double x1 = canvas.getWidth() * 0.5 - width * 0.5;
		double y1 = canvas.getHeight() * 0.5 - height * 0.5;

		context.setFill(Color.WHITE);
		context.fillRect(x1, y1, width, height);

		context.setFill(Color.gray(0.925));
		for (int y = 0; y < frame.getValue().image.getValue().getHeight() * 2; y++) {
			for (int x = 0; x < frame.getValue().image.getValue().getWidth() * 2; x++) {
				if ((x + y) % 2 == 0)
					continue;

				double px1 = x1 + x * scale * 0.5;
				double py1 = y1 + y * scale * 0.5;
				double px2 = px1 + scale * 0.5;
				double py2 = py1 + scale * 0.5;

				px1 = Math.round(px1);
				py1 = Math.round(py1);
				px2 = Math.round(px2);
				py2 = Math.round(py2);

				if (px2 - px1 < 1 || py2 - py1 < 1)
					continue;
				context.fillRect(px1, py1, px2 - px1, py2 - py1);
			}
		}
	}

	private void drawImage(@Nonnull GraphicsContext context) {
		double scale = getImageScale();
		double width = frame.getValue().image.getValue().getWidth() * scale;
		double height = frame.getValue().image.getValue().getHeight() * scale;
		double x1 = canvas.getWidth() * 0.5 - width * 0.5;
		double y1 = canvas.getHeight() * 0.5 - height * 0.5;

		PixelReader pixels = frame.getValue().image.getValue().getPixelReader();
		for (int y = 0; y < frame.getValue().image.getValue().getHeight(); y++) {
			for (int x = 0; x < frame.getValue().image.getValue().getWidth(); x++) {
				Color color = pixels.getColor(x, y);
				context.setFill(color);

				double px1 = x1 + x * scale;
				double py1 = y1 + y * scale;
				double px2 = px1 + scale;
				double py2 = py1 + scale;

				px1 = Math.round(px1);
				py1 = Math.round(py1);
				px2 = Math.round(px2);
				py2 = Math.round(py2);

				if (px2 - px1 < 1 || py2 - py1 < 1)
					continue;
				context.fillRect(px1, py1, px2 - px1, py2 - py1);
			}
		}
	}
}