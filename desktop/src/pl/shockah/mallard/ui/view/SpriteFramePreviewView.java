package pl.shockah.mallard.ui.view;

import javax.annotation.Nonnull;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.Math2;

public class SpriteFramePreviewView extends Region {
	@Nonnull
	public final SpriteProject.Frame frame;

	@Nonnull
	protected final ResizableCanvas canvas;

	@Nonnull
	public final BooleanProperty editingOrigin = new SimpleBooleanProperty(this, "editingOrigin");

	protected boolean additionalOriginPrecision = false;

	@Nonnull
	protected Vec2 newOrigin = Vec2.zero;

	public SpriteFramePreviewView(@Nonnull SpriteProject.Frame frame) {
		this.frame = frame;

		canvas = new ResizableCanvas() {
			@Override
			protected void draw(@Nonnull GraphicsContext context) {
				super.draw(context);
				SpriteFramePreviewView.this.draw(context);
			}
		};
		getChildren().add(canvas);

		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
		canvas.setFocusTraversable(true);

		frame.image.addListener((observable, oldValue, newValue) -> {
			canvas.redraw();
		});

		canvas.setOnMouseMoved(event -> {
			double scale = getImageScale();
			double x1 = getLeft();
			double y1 = getTop();
			double x2 = getRight();
			double y2 = getBottom();

			double extra = scale * 0.5;
			if (additionalOriginPrecision)
				extra /= 4;

			double mx = Math2.clamp(event.getX() + extra, x1, x2);
			double my = Math2.clamp(event.getY() + extra, y1, y2);
			double fx = (mx - x1) / (x2 - x1);
			double fy = (my - y1) / (y2 - y1);

			double newX = fx * frame.image.getValue().getWidth();
			double newY = fy * frame.image.getValue().getHeight();

			if (additionalOriginPrecision) {
				newX *= 4;
				newY *= 4;
			}

			newX = (int)newX;
			newY = (int)newY;

			if (additionalOriginPrecision) {
				newX /= 4;
				newY /= 4;
			}

			newOrigin = new Vec2((float)newX, (float)newY);
			canvas.redraw();
		});

		canvas.setOnMouseClicked(event -> {
			if (editingOrigin.get()) {
				if (event.getButton() == MouseButton.PRIMARY) {
					frame.origin.setValue(newOrigin);
					editingOrigin.set(false);
				}
			}
		});

		canvas.setOnKeyPressed(event -> {
			additionalOriginPrecision = event.isAltDown();
		});

		canvas.setOnKeyReleased(event -> {
			additionalOriginPrecision = event.isAltDown();
		});

		editingOrigin.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				canvas.setFocusTraversable(true);
				canvas.requestFocus();
			} else {
				Mallard.getStage().requestFocus();
				canvas.setFocusTraversable(false);
			}
		});
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		canvas.setLayoutX(0);
		canvas.setLayoutY(0);
	}

	private double getImageScale() {
		double padding = 16;

		double scale = (canvas.getWidth() - padding) / frame.image.getValue().getWidth();
		if (frame.image.getValue().getHeight() * scale > (canvas.getHeight() - padding))
			scale = (canvas.getHeight() - padding) / frame.image.getValue().getHeight();
		return scale;
	}

	private double getLeft() {
		double scale = getImageScale();
		double width = frame.image.getValue().getWidth() * scale;
		return canvas.getWidth() * 0.5 - width * 0.5;
	}

	private double getRight() {
		double scale = getImageScale();
		double width = frame.image.getValue().getWidth() * scale;
		return canvas.getWidth() * 0.5 + width * 0.5;
	}

	private double getTop() {
		double scale = getImageScale();
		double height = frame.image.getValue().getHeight() * scale;
		return canvas.getHeight() * 0.5 - height * 0.5;
	}

	private double getBottom() {
		double scale = getImageScale();
		double height = frame.image.getValue().getHeight() * scale;
		return canvas.getHeight() * 0.5 + height * 0.5;
	}

	private void draw(@Nonnull GraphicsContext context) {
		context.clearRect(0, 0, getWidth(), getHeight());
		drawGrid(context);
		drawImage(context);
		drawOrigin(context);
	}

	private void drawGrid(@Nonnull GraphicsContext context) {
		double scale = getImageScale();
		double width = frame.image.getValue().getWidth() * scale;
		double height = frame.image.getValue().getHeight() * scale;
		double x1 = canvas.getWidth() * 0.5 - width * 0.5;
		double y1 = canvas.getHeight() * 0.5 - height * 0.5;

		context.setFill(Color.WHITE);
		context.fillRect(x1, y1, width, height);

		context.setFill(Color.gray(0.925));
		for (int y = 0; y < frame.image.getValue().getHeight() * 2; y++) {
			for (int x = 0; x < frame.image.getValue().getWidth() * 2; x++) {
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
		double width = frame.image.getValue().getWidth() * scale;
		double height = frame.image.getValue().getHeight() * scale;
		double x1 = canvas.getWidth() * 0.5 - width * 0.5;
		double y1 = canvas.getHeight() * 0.5 - height * 0.5;

		PixelReader pixels = frame.image.getValue().getPixelReader();
		for (int y = 0; y < frame.image.getValue().getHeight(); y++) {
			for (int x = 0; x < frame.image.getValue().getWidth(); x++) {
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

	private void drawOrigin(@Nonnull GraphicsContext context) {
		double scale = getImageScale();
		double x1 = getLeft();
		double y1 = getTop();

		context.setStroke(Color.RED);
		context.setLineWidth(1);

		Vec2 origin = frame.origin.getValue();
		if (editingOrigin.get())
			origin = newOrigin;

		context.strokeLine(0, y1 + origin.y * scale, canvas.getWidth(), y1 + origin.y * scale);
		context.strokeLine(x1 + origin.x * scale, 0, x1 + origin.x * scale, canvas.getHeight());
	}
}