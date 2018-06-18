package pl.shockah.mallard.project;

import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;

public class SpriteProject extends Project {
	@Nonnull
	public final List<Subsprite> subsprites = new ArrayList<>();

	@Nonnull
	public final Map<String, Animation> animations = new LinkedHashMap<>();

	public static class Subsprite {
		@Nonnull
		public Pixmap pixmap;

		@Nonnull
		public Vec2 origin = Vec2.zero;

		@Nonnull
		public final Map<String, Shape.Filled> shapes = new LinkedHashMap<>();

		public Subsprite(@Nonnull Pixmap pixmap) {
			this.pixmap = pixmap;
		}
	}

	public static class Animation {
		@Nonnull
		public final List<Frame> frames = new ArrayList<>();

		public float duration = 1f;

		public static class Frame {
			@Nonnull
			public final Subsprite subsprite;

			public float relativeDuration = 1f;

			public Frame(@Nonnull Subsprite subsprite) {
				this.subsprite = subsprite;
			}
		}
	}
}