package pl.shockah.mallard.ui.view;

import javax.annotation.Nonnull;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class ResizableCanvas extends Canvas {
	public ResizableCanvas() {
		parentProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				redraw();
		});
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public double prefWidth(double height) {
		return 0.0;
	}

	@Override
	public double prefHeight(double width) {
		return 0.0;
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		redraw();
	}

	public final void redraw() {
		draw(getGraphicsContext2D());
	}

	protected void draw(@Nonnull GraphicsContext context) {
	}
}