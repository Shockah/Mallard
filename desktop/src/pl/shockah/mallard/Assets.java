package pl.shockah.mallard;

import com.badlogic.gdx.graphics.Texture;

import javax.annotation.Nonnull;

import lombok.experimental.UtilityClass;
import pl.shockah.godwit.asset.FreeTypeFontAsset;
import pl.shockah.godwit.asset.FreeTypeFontLoader;
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

		@Nonnull
		public static final TextureAsset textbox = new TextureAsset("ui/textbox.png");
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

	@Nonnull
	public static final FreeTypeFontAsset font12 = new FreeTypeFontAsset("CooperHewitt-Medium.ttf", new FreeTypeFontLoader.FreeTypeFontParameter() {{
		size = 12;
		minFilter = magFilter = Texture.TextureFilter.Linear;
	}});

	@Nonnull
	public static final FreeTypeFontAsset font10 = new FreeTypeFontAsset("CooperHewitt-Medium-2.ttf", new FreeTypeFontLoader.FreeTypeFontParameter() {{
		size = 10;
		minFilter = magFilter = Texture.TextureFilter.Linear;
	}});
}