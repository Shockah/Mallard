package pl.shockah.mallard;

import javax.annotation.Nonnull;

import lombok.experimental.UtilityClass;
import pl.shockah.godwit.asset.TextureAsset;

@UtilityClass
public final class Assets {
	@UtilityClass
	public static final class Ui {
		@Nonnull
		public static final TextureAsset panel = new TextureAsset("ui/panel.png");

		@Nonnull
		public static final TextureAsset buttonNormal = new TextureAsset("ui/button-normal.png");

		@Nonnull
		public static final TextureAsset buttonPressed = new TextureAsset("ui/button-pressed.png");
	}

	@UtilityClass
	public static final class Icon {
		@Nonnull
		public static final TextureAsset newIcon = new TextureAsset("icon/new.png");

		@Nonnull
		public static final TextureAsset save = new TextureAsset("icon/save.png");

		@Nonnull
		public static final TextureAsset load = new TextureAsset("icon/load.png");

		@Nonnull
		public static final TextureAsset plus = new TextureAsset("icon/plus.png");
	}

	@UtilityClass
	public static final class Sprite {
		@Nonnull
		public static final TextureAsset transparencyGrid = new TextureAsset("transparency-grid.png");
	}
}