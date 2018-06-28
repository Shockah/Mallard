package pl.shockah.mallard.project;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;

public class SpriteProject extends Project {
	@Nonnull
	public final ObservableList<Frame> frames = FXCollections.observableArrayList();

	@Nonnull
	public final ObservableList<Animation.Entry> animations = FXCollections.observableArrayList();

	public static class Frame {
		@Nonnull
		public Image image;

		@Nonnull
		public Vec2 origin = Vec2.zero;

		@Nonnull
		public final Map<String, Shape.Filled> shapes = new LinkedHashMap<>();

		public Frame(@Nonnull Image image) {
			this.image = image;
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