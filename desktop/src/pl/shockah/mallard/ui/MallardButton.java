package pl.shockah.mallard.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import pl.shockah.godwit.State;
import pl.shockah.godwit.asset.Asset;
import pl.shockah.godwit.geom.IVec2;
import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.gl.Gfx;
import pl.shockah.godwit.gl.GfxFont;
import pl.shockah.godwit.gl.GfxSprite;
import pl.shockah.godwit.gl.NinePatch;
import pl.shockah.godwit.ui.Alignment;
import pl.shockah.godwit.ui.Padding;
import pl.shockah.godwit.ui.UiButton;
import pl.shockah.mallard.Assets;

public class MallardButton extends UiButton.NinePatchButton {
	public MallardButton(@Nonnull State state, @Nonnull Listener listener) {
		super(
				new NinePatch(state.requestAsset(Assets.Ui.buttonNormal), new Padding(9f, 9f)),
				new NinePatch(state.requestAsset(Assets.Ui.buttonPressed), new Padding(9f, 9f)),
				listener
		);
	}

	public static class Icon extends MallardButton {
		@Nonnull
		public final GfxSprite sprite;

		@Nonnull
		public Padding iconPadding = new Padding(6f, 6f, 6f, 9f);

		public Icon(@Nonnull State state, @Nonnull Asset<Texture> icon, @Nonnull Listener listener) {
			super(state, listener);
			sprite = new GfxSprite(new Sprite(state.requestAsset(icon)));
			sprite.center();
		}

		@Override
		public void render(@Nonnull Gfx gfx, @Nonnull IVec2 v) {
			super.render(gfx, v);

			Rectangle iconBounds = getBounds().withoutPadding(iconPadding);

			float scale = iconBounds.size.x / sprite.getRegionWidth();
			if (sprite.getRegionHeight() * scale > iconBounds.size.y)
				scale = iconBounds.size.y / sprite.getRegionHeight();

			Vec2 iconPosition = iconBounds.getCenter();
			if (isPressed)
				iconPosition = iconPosition.add(0, 4);

			sprite.setScale(scale);
			sprite.render(gfx, iconPosition);
		}
	}

	public static class Label extends MallardButton {
		@Nonnull
		public final GfxFont font;

		@Nullable
		public String text;

		public Label(@Nonnull State state, @Nonnull String text, @Nonnull Listener listener) {
			super(state, listener);
			state.loadAsset(Assets.font10);
			font = new GfxFont(Assets.font10);
			font.setAlignment(Alignment.Horizontal.Center.and(Alignment.Vertical.Middle));
			font.setColor(Color.BLACK);
			this.text = text;
		}

		@Override
		public void render(@Nonnull Gfx gfx, @Nonnull IVec2 v) {
			super.render(gfx, v);

			font.setText(text);
			if (text != null && !text.equals("")) {
				font.render(gfx, getBounds().position.add(0, -2).add(0, isPressed ? 4 : 0).add(size.multiply(font.getAlignment().getVector())));
			}
		}
	}
}