package pl.shockah.mallard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import java8.util.Spliterators;
import java8.util.stream.RefStreams;
import java8.util.stream.StreamSupport;
import lombok.Getter;
import pl.shockah.godwit.fx.Fx;
import pl.shockah.godwit.fx.RunnableFx;
import pl.shockah.godwit.fx.SequenceFx;
import pl.shockah.godwit.fx.WaitFx;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.gl.GfxSprite;
import pl.shockah.unicorn.collection.Array1D;
import pl.shockah.unicorn.func.Action1;

public class MallardSprite {
	public static final int VERSION = 1;

	@Nonnull
	@Getter
	public final Texture texture;

	@Nonnull
	@Getter
	public final Array1D<Subsprite> subsprites;

	@Nonnull
	@Getter
	public final Map<String, Animation> animations;

	public MallardSprite(@Nonnull Texture texture, @Nonnull Subsprite[] subsprites, @Nonnull Map<String, Animation> animations) {
		for (int i = 1; i < subsprites.length; i++) {
			if (subsprites[0].region.getTexture() != subsprites[i].region.getTexture())
				throw new IllegalArgumentException("Subsprites must be on the same texture.");
		}

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

		@Nonnull
		public GfxSprite getGfxSprite() {
			GfxSprite sprite = new GfxSprite(new Sprite(region));
			sprite.setOrigin(origin);
			sprite.offset.set(origin);
			return sprite;
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

		@Nonnull
		public Frame getFrame(float f) {
			float sum = (float)StreamSupport.stream(Spliterators.spliteratorUnknownSize(frames.iterator(), 0), false)
					.mapToDouble(frame -> frame.relativeDuration)
					.sum();
			float find = f * sum;

			for (Frame frame : frames) {
				if (find < frame.relativeDuration)
					return frame;
				find -= frame.relativeDuration;
			}
			return frames.get(frames.length - 1);
		}

		@Nonnull
		public Fx asFx(@Nonnull Action1<Frame> func) {
			List<Fx> list = new ArrayList<>();
			float sum = (float)RefStreams.of(frames).mapToDouble(frame -> frame.length).sum();
			for (Frame frame : frames) {
				list.add(new RunnableFx(() -> func.call(frame)));
				list.add(new WaitFx(duration * frame.relativeDuration / sum));
			}
			return new SequenceFx(list);
		}

		public static class Frame {
			@Nonnull
			public final Subsprite subsprite;

			public final float relativeDuration;

			@Nonnull
			public final Vec2 offset;

			public Frame(@Nonnull Subsprite subsprite, float relativeDuration, @Nonnull Vec2 offset) {
				this.subsprite = subsprite;
				this.relativeDuration = relativeDuration;
				this.offset = offset;
			}

			@Nonnull
			public MallardGfxSprite getGfxSprite() {
				return new MallardGfxSprite(this);
			}
		}
	}
}