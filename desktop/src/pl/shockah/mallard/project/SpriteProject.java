package pl.shockah.mallard.project;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.ui.controller.sprite.editor.SpriteFrameEditor;

public class SpriteProject extends Project {
	@Nonnull
	public final ObservableList<Frame> frames = FXCollections.observableArrayList();

	@Nonnull
	public final ObservableList<Animation.Entry> animations = FXCollections.observableArrayList();

	public static class Frame {
		@Nonnull
		public final Property<Image> image = new SimpleObjectProperty<>(this, "image");

		@Nonnull
		public final Property<Vec2> origin = new SimpleObjectProperty<>(this, "origin");

		@Nonnull
		public final ObservableList<ShapeEntry> shapes = FXCollections.observableArrayList();

		@Nonnull
		public final Property<SpriteFrameEditor> currentEditor = new SimpleObjectProperty<>(this, "currentEditor");

		public Frame(@Nonnull Image image) {
			this.image.setValue(image);
			origin.setValue(Vec2.zero);
		}

		public void trimImage() {
			int x1 = -1;
			int y1 = -1;
			int x2 = -1;
			int y2 = -1;

			Image image = this.image.getValue();
			PixelReader pixels = image.getPixelReader();

			X1:
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					Color color = pixels.getColor(x, y);
					if (color.getOpacity() > 0.0) {
						x1 = x;
						break X1;
					}
				}
			}

			Y1:
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					Color color = pixels.getColor(x, y);
					if (color.getOpacity() > 0.0) {
						y1 = y;
						break Y1;
					}
				}
			}

			X2:
			for (int x = (int)image.getWidth() - 1; x >= 0; x--) {
				for (int y = 0; y < image.getHeight(); y++) {
					Color color = pixels.getColor(x, y);
					if (color.getOpacity() > 0.0) {
						x2 = x;
						break X2;
					}
				}
			}

			Y2:
			for (int y = (int)image.getHeight() - 1; y >= 0; y--) {
				for (int x = 0; x < image.getWidth(); x++) {
					Color color = pixels.getColor(x, y);
					if (color.getOpacity() > 0.0) {
						y2 = y;
						break Y2;
					}
				}
			}

			int w = x2 - x1 + 1;
			int h = y2 - y1 + 1;

			if (x1 == 0 && y1 == 0 && w == image.getWidth() && h == image.getHeight())
				return;

			WritableImage result = new WritableImage(w, h);
			PixelWriter writer = result.getPixelWriter();
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					writer.setColor(x, y, pixels.getColor(x + x1, y + y1));
				}
			}
			this.image.setValue(result);

			origin.setValue(origin.getValue().subtract(x1, y1));
		}

		public static class ShapeEntry {
			@Nonnull
			public final String name;

			@Nonnull
			public final Shape.Filled shape;

			public ShapeEntry(@Nonnull String name, @Nonnull Shape.Filled shape) {
				this.name = name;
				this.shape = shape;
			}
		}
	}

	public static class Animation {
		@Nonnull
		public final List<Frame> frames = new ArrayList<>();

		public float duration = 1f;

		public static class Frame {
			@Nonnull
			public final SpriteProject.Frame frame;

			public float relativeDuration = 1f;

			public Frame(@Nonnull SpriteProject.Frame frame) {
				this.frame = frame;
			}
		}

		public static class Entry {
			@Nonnull
			public final String name;

			@Nonnull
			public final Animation animation;

			public Entry(@Nonnull String name, @Nonnull Animation animation) {
				this.name = name;
				this.animation = animation;
			}
		}
	}
}