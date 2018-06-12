package pl.shockah.mallard.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.IVec2;
import pl.shockah.godwit.geom.MutableVec2;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.gl.Gfx;
import pl.shockah.godwit.ui.UiPanel;

public class SpriteDisplayPanel extends UiPanel {
	@Nonnull
	public final Texture texture;

	@Nonnull
	public MutableVec2 scale = new MutableVec2(1f, 1f);

	@Nonnull
	public MutableVec2 anchorPoint = new MutableVec2(0.5f, 0.5f);

	@Nonnull
	public MutableVec2 offset = new MutableVec2();

	@Nonnull
	public MutableVec2 screenAnchorPoint = new MutableVec2(0.5f, 0.5f);

	public SpriteDisplayPanel(@Nonnull Texture texture) {
		this.texture = texture;
	}

	@Override
	public void render(@Nonnull Gfx gfx, @Nonnull IVec2 v) {
		super.render(gfx, v);
		renderBackground(gfx, v);
	}

	protected void renderBackground(@Nonnull Gfx gfx, @Nonnull IVec2 v) {
		Vec2 singleSize = scale.multiply(texture.getWidth(), texture.getHeight());
		int horizontalCount = Math.max((int)Math.ceil(1f * size.x / singleSize.x) + 3, 3);
		int verticalCount = Math.max((int)Math.ceil(1f * size.y / singleSize.y) + 3, 3);
		if (horizontalCount % 2 == 0)
			horizontalCount++;
		if (verticalCount % 2 == 0)
			verticalCount++;
		Vec2 totalSize = singleSize.multiply(horizontalCount, verticalCount);

		//Vec2 camera = getCameraGroup().getCameraPosition();

		Vec2 point = offset.add(v);
		point = point.add(screenAnchorPoint.multiply(size));

		//point = point.subtract(camera);
		Vec2 moduloPoint = new Vec2(point.x % singleSize.x, point.y % singleSize.y);
		point = new Vec2(moduloPoint.x, moduloPoint.y);
		//point = point.add(camera);

		point = point.subtract(anchorPoint.multiply(totalSize));

		gfx.prepareSprites();
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		TextureRegion region = new TextureRegion(texture);
		region.setRegion(0f, 0f, horizontalCount, verticalCount);
		gfx.getSpriteBatch().draw(region, point.x, point.y, totalSize.x, totalSize.y);
	}
}