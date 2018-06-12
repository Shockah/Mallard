package pl.shockah.mallard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.unicorn.collection.Array1D;

public class MallardSprite {
	@Nonnull
	public final Texture texture;

	@Nonnull
	public final Array1D<Subsprite> subsprites;

	@Nonnull
	public final Map<String, Animation> animations;

	public MallardSprite(@Nonnull Texture texture, @Nonnull Subsprite[] subsprites, @Nonnull Map<String, Animation> animations) {
		this.texture = texture;
		this.subsprites = new Array1D<>(subsprites);
		this.animations = Collections.unmodifiableMap(new LinkedHashMap<>(animations));
	}

	public static class Subsprite {
		@Nonnull
		public final TextureRegion region;

		@Nonnull
		public final Vec2 origin;

		@Nonnull
		public final Map<String, Shape.Filled> shapes;

		public Subsprite(@Nonnull TextureRegion region, @Nonnull Vec2 origin, @Nonnull Map<String, Shape.Filled> shapes) {
			this.region = region;
			this.origin = origin;
			this.shapes = Collections.unmodifiableMap(new LinkedHashMap<>(shapes));
		}
	}

	public static class Animation {
		@Nonnull
		public final Array1D<Frame> frames;

		public final float duration;

		public Animation(@Nonnull Frame[] frames, float duration) {
			for (int i = 1; i < frames.length; i++) {
				if (frames[0].subsprite.region.getTexture() != frames[i].subsprite.region.getTexture())
					throw new IllegalArgumentException("Frames must be on the same texture.");
			}

			this.frames = new Array1D<>(frames);
			this.duration = duration;
		}

		public static class Frame {
			@Nonnull
			public final Subsprite subsprite;

			public final float relativeDuration;

			public Frame(@Nonnull Subsprite subsprite, float relativeDuration) {
				this.subsprite = subsprite;
				this.relativeDuration = relativeDuration;
			}
		}
	}
}