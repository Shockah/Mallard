package pl.shockah.mallard;

import com.badlogic.gdx.graphics.g2d.Sprite;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.gl.GfxSprite;

public class MallardGfxSprite extends GfxSprite {
	@Nonnull
	public final MallardSprite.Animation.Frame frame;

	public MallardGfxSprite(@Nonnull MallardSprite.Animation.Frame frame) {
		super(new Sprite(frame.subsprite.region));
		setOrigin(frame.subsprite.origin);
		offset.set(frame.subsprite.origin);
		this.frame = frame;
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <S extends Shape.Filled> S getShape(@Nonnull String name) {
		return (S)frame.subsprite.shapes.get(name).copy().translate(-offset.x, -offset.y).scale(getScale()).translate(getX(), getY());
	}
}