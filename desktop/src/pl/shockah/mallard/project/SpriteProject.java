package pl.shockah.mallard.project;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.experimental.var;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.ShapeManager;
import pl.shockah.mallard.ui.controller.sprite.editor.ShapeEditor;
import pl.shockah.mallard.ui.controller.sprite.editor.SpriteFrameEditor;
import pl.shockah.unicorn.color.HSLuvColorSpace;
import pl.shockah.unicorn.color.RGBColorSpace;
import pl.shockah.unicorn.func.Func2;
import pl.shockah.unicorn.rand.Randomizer;

public class SpriteProject extends Project {
	@Nonnull
	public final ObservableList<Frame> frames = FXCollections.observableArrayList();

	@Nonnull
	public final ObservableList<Animation.Entry> animations = FXCollections.observableArrayList();

	public static class Frame {
		@Nonnull
		public final Property<Image> image = new SimpleObjectProperty<>(this, "image");

		@Nonnull
		public final Property<Vec2> origin = new SimpleObjectProperty<>(this, "origin", Vec2.zero);

		@Nonnull
		public final ObservableList<ShapeEntry<? extends Shape.Filled>> shapes = FXCollections.observableArrayList();

		@Nonnull
		public final Property<SpriteFrameEditor> currentEditor = new SimpleObjectProperty<>(this, "currentEditor");

		public Frame(@Nonnull Image image) {
			this.image.setValue(image);
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

		public static class ShapeEntry<S extends Shape.Filled> {
			@Nonnull
			public final ShapeManager.Entry<S> shapeManagerEntry;

			@Nonnull
			public final ShapeEditor<S> editor;

			@Nonnull
			public final String name;

			@Nonnull
			public final Property<S> shape = new SimpleObjectProperty<>(this, "shape");

			@Nonnull
			public final Property<Color> color = new SimpleObjectProperty<>(this, "color");

			@Nonnull
			public final BooleanProperty visible = new SimpleBooleanProperty(this, "visible", true);

			@SuppressWarnings("unchecked")
			public ShapeEntry(@Nonnull Frame frame, @Nonnull ShapeManager.Entry<S> shapeManagerEntry, @Nonnull String name, @Nullable S shape) {
				this.shapeManagerEntry = shapeManagerEntry;
				this.name = name;
				this.shape.setValue(shape);

				Func2<?, ?, ?> wildcardFactory = shapeManagerEntry.editorFactory;
				var rawFactory = (Func2<SpriteProject.Frame, SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>, ShapeEditor<Shape.Filled>>) wildcardFactory;
				editor = (ShapeEditor<S>)rawFactory.call(frame, this);

				Randomizer randomizer = new Randomizer(new Random(name.hashCode() * name.hashCode()));
				HSLuvColorSpace hsl = new HSLuvColorSpace(
						randomizer.getFloatRangeGenerator(0f, 1f).generate(),
						randomizer.getFloatRangeGenerator(0.6f, 1f).generate(),
						randomizer.getFloatRangeGenerator(0.3f, 0.7f).generate()
				);
				RGBColorSpace rgb = hsl.toRGB();

				color.setValue(new Color(rgb.r, rgb.g, rgb.b, 1.0));
			}
		}
	}

	public static class Animation {
		@Nonnull
		public final ObservableList<Frame> frames = FXCollections.observableArrayList();

		@Nonnull
		public final DoubleProperty duration = new SimpleDoubleProperty(this, "duration", 1.0);

		public static class Frame {
			@Nonnull
			public final SpriteProject.Frame frame;

			@Nonnull
			public final DoubleProperty relativeDuration = new SimpleDoubleProperty(this, "relativeDuration", 1.0);

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