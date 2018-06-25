package pl.shockah.mallard.ui;

import com.badlogic.gdx.graphics.Color;

import javax.annotation.Nonnull;

import pl.shockah.godwit.State;
import pl.shockah.godwit.geom.IVec2;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.gl.Gfx;
import pl.shockah.godwit.gl.GfxFont;
import pl.shockah.godwit.gl.NinePatch;
import pl.shockah.godwit.ui.Alignment;
import pl.shockah.godwit.ui.Padding;
import pl.shockah.godwit.ui.UiTextbox;
import pl.shockah.mallard.Assets;

public class MallardTextbox extends UiTextbox {
	@Nonnull
	public final NinePatch ninePatch;

	@Nonnull
	public final GfxFont font;

	@Nonnull
	public final GfxFont blinkFont;

	protected float blinkAlpha = 0f;

	public MallardTextbox(@Nonnull State state) {
		ninePatch = new NinePatch(state.requestAsset(Assets.Ui.textbox), new Padding(8f));

		state.loadAsset(Assets.font10);

		font = new GfxFont(Assets.font10);
		font.setAlignment(Alignment.Horizontal.Left.and(Alignment.Vertical.Middle));
		font.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));

		blinkFont = new GfxFont(Assets.font10);
		blinkFont.setAlignment(font.getAlignment());
		blinkFont.setColor(font.getColor());
	}

	@Override
	public void render(@Nonnull Gfx gfx, @Nonnull IVec2 v) {
		super.render(gfx, v);

		ninePatch.rectangle = getBounds();
		ninePatch.render(gfx, Vec2.zero);

		font.setText(text);
		font.render(gfx, getBounds().position.add(8f, size.y * 0.5f));

		if (isFocus()) {
			blinkAlpha -= 0.02f;
			if (blinkAlpha < 0f)
				blinkAlpha += 1f;
			blinkFont.setText("|");
			blinkFont.setColor(font.getColor().cpy().mul(1f, 1f, 1f, blinkAlpha));
			blinkFont.render(gfx, getBounds().position.add(8f, size.y * 0.5f).add(font.getSize().x, 0f));
		}
	}

	@Override
	public void onFocus() {
		super.onFocus();
		blinkAlpha = 1f;
	}

	@Override
	public void onBlur() {
		super.onBlur();
		blinkAlpha = 0f;
	}
}