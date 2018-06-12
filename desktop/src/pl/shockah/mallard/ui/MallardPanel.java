package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.BasicConstraint;
import pl.shockah.godwit.constraint.Constraint;
import pl.shockah.godwit.geom.IVec2;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.gl.Gfx;
import pl.shockah.godwit.gl.NinePatch;
import pl.shockah.godwit.ui.Padding;
import pl.shockah.godwit.ui.UiPanel;
import pl.shockah.godwit.ui.Unit;
import pl.shockah.mallard.Assets;

public class MallardPanel extends UiPanel {
	@Nonnull
	public final NinePatch ninePatch;

	@Nonnull
	public final UiPanel content;

	public MallardPanel(@Nonnull State state) {
		ninePatch = new NinePatch(state.requestAsset(Assets.Ui.panel), new Padding(9f, 9f));

		content = new UiPanel();
		addChild(content);
		content.addConstraint(BasicConstraint.withParent(content, Constraint.Attribute.Width, new Unit.Pixels(-18f), 1f));
		content.addConstraint(BasicConstraint.withParent(content, Constraint.Attribute.Height, new Unit.Pixels(-18f), 1f));
		content.addConstraint(BasicConstraint.withParent(content, Constraint.Attribute.CenterX));
		content.addConstraint(BasicConstraint.withParent(content, Constraint.Attribute.CenterY));
	}

	@Override
	public void render(@Nonnull Gfx gfx, @Nonnull IVec2 v) {
		ninePatch.rectangle = getBounds().copy().translate(v.negate());
		ninePatch.render(gfx, v);
		super.render(gfx, Vec2.zero);
	}
}