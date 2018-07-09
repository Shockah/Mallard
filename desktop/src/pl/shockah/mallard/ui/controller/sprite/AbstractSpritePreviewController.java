package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.BindUtilities;
import pl.shockah.mallard.ui.controller.Controller;
import pl.shockah.mallard.ui.view.ResizableCanvas;

public abstract class AbstractSpritePreviewController extends Controller {
	@Nonnull
	public final ResizableCanvas canvas;

	@Nonnull
	public final Property<Rectangle> spriteBounds = new SimpleObjectProperty<>(this, "spriteBounds");

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

	public double getImageX() {
		return spriteBounds.getValue() == null ? 0.0 : spriteBounds.getValue().position.x;
	}

	public double getImageY() {
		return spriteBounds.getValue() == null ? 0.0 : spriteBounds.getValue().position.y;
	}

	public double getOriginX() {
		return spriteBounds.getValue() == null ? 0.0 : frame.getValue().origin.getValue().x;
	}

	public double getOriginY() {
		return spriteBounds.getValue() == null ? 0.0 : frame.getValue().origin.getValue().y;
	}

	public double getImageWidth() {
		return spriteBounds.getValue() == null ? frame.getValue().image.getValue().getWidth() : spriteBounds.getValue().size.x;
	}

	public double getImageHeight() {
		return spriteBounds.getValue() == null ? frame.getValue().image.getValue().getHeight() : spriteBounds.getValue().size.y;
	}

	public double getImageScale() {
		double padding = 16;
		double h = getImageHeight();

		double scale = (canvas.getWidth() - padding) / getImageWidth();
		if (h * scale > (canvas.getHeight() - padding))
			scale = (canvas.getHeight() - padding) / h;
		return scale;
	}

	public double getLeft() {
		double scale = getImageScale();
		double width = getImageWidth() * scale;
		return canvas.getWidth() * 0.5 - width * 0.5;
	}

	public double getRight() {
		double scale = getImageScale();
		double width = getImageWidth() * scale;
		return canvas.getWidth() * 0.5 + width * 0.5;
	}

	public double getTop() {
		double scale = getImageScale();
		double height = getImageHeight() * scale;
		return canvas.getHeight() * 0.5 - height * 0.5;
	}

	public double getBottom() {
		double scale = getImageScale();
		double height = getImageHeight() * scale;
		return canvas.getHeight() * 0.5 + height * 0.5;
	}

	protected void draw(@Nonnull GraphicsContext context) {
		if (frame.getValue() == null)
			return;

		context.clearRect(0, 0, context.getCanvas().getWidth(), context.getCanvas().getHeight());
		drawGrid(context);
		drawImage(context);
	}

	private void drawGrid(@Nonnull GraphicsContext context) {
		double scale = getImageScale();
		double width = getImageWidth();
		double height = getImageHeight();
		double x1 = getLeft();
		double y1 = getTop();

		context.setFill(Color.WHITE);
		context.fillRect(x1, y1, width * scale, height * scale);

		context.setFill(Color.gray(0.925));
		for (int y = 0; y < height * 2; y++) {
			for (int x = 0; x < width * 2; x++) {
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
		double x1 = getLeft();
		double y1 = getTop();

		double offsetX = getOriginX() + getImageX();
		double offsetY = getOriginY() + getImageY();
		int imageWidth = (int)frame.getValue().image.getValue().getWidth();
		int imageHeight = (int)frame.getValue().image.getValue().getHeight();

		PixelReader pixels = frame.getValue().image.getValue().getPixelReader();
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				context.setFill(pixels.getColor(x, y));

				double px1 = x1 + (x - offsetX) * scale;
				double py1 = y1 + (y - offsetY) * scale;
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