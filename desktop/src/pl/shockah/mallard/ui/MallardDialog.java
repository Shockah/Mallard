package pl.shockah.mallard.ui;

import com.badlogic.gdx.graphics.Color;

import javax.annotation.Nonnull;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.AxisConstraint;
import pl.shockah.godwit.constraint.CenterConstraint;
import pl.shockah.godwit.constraint.PinConstraint;
import pl.shockah.godwit.geom.IVec2;
import pl.shockah.godwit.gesture.TapGestureRecognizer;
import pl.shockah.godwit.gl.Gfx;
import pl.shockah.godwit.ui.UiPanel;

public class MallardDialog extends UiPanel {
	@Nonnull
	public final MallardGroupPanel content;

	public boolean tapOutsideToDismiss = true;

	public MallardDialog(@Nonnull State state, @Nonnull String title, @Nonnull Color titleColor) {
		addChild(content = new MallardGroupPanel(state, title, titleColor) {{
			gestureRecognizers.add(new TapGestureRecognizer(this, recognizer -> {}));
		}});
		addConstraint(new PinConstraint(this, state.ui));
		addConstraint(new CenterConstraint(content, state.ui, AxisConstraint.Axis.Horizontal));
		addConstraint(new CenterConstraint(content, state.ui, AxisConstraint.Axis.Vertical));
		gestureRecognizers.add(new TapGestureRecognizer(this, recognizer -> {
			if (tapOutsideToDismiss)
				removeFromParent();
		}));
	}

	@Override
	public void render(@Nonnull Gfx gfx, @Nonnull IVec2 v) {
		gfx.setColor(Color.BLACK.cpy().mul(1f, 1f, 1f, 0.5f));
		gfx.drawFilled(getBounds());
		gfx.setColor(Color.WHITE);
		super.render(gfx, v);
	}
}