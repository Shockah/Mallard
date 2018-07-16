package pl.shockah.mallard;

import javax.annotation.Nonnull;

import lombok.experimental.Delegate;
import pl.shockah.godwit.asset.SingleAsset;

public class MallardSpriteAsset extends SingleAsset<MallardSprite> {
	public MallardSpriteAsset(@Nonnull String fileName) {
		super(fileName, MallardSprite.class);
	}

	@Nonnull
	@Override
	@Delegate
	public MallardSprite get() {
		return super.get();
	}
}