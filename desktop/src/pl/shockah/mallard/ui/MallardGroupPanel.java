package pl.shockah.mallard.ui;

import com.badlogic.gdx.graphics.Color;

import javax.annotation.Nonnull;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.AxisConstraint;
import pl.shockah.godwit.constraint.BasicConstraint;
import pl.shockah.godwit.constraint.CenterConstraint;
import pl.shockah.godwit.constraint.Constraint;
import pl.shockah.godwit.constraint.PinConstraint;
import pl.shockah.godwit.gl.GfxFont;
import pl.shockah.godwit.ui.Alignment;
import pl.shockah.godwit.ui.UiLabel;
import pl.shockah.godwit.ui.UiPanel;
import pl.shockah.godwit.ui.Unit;
import pl.shockah.mallard.Assets;

public class MallardGroupPanel extends UiPanel {
	@Nonnull
	public final UiLabel titleLabel;

	@Nonnull
	public final MallardPanel contentPanel;

	public MallardGroupPanel(@Nonnull State state, @Nonnull String text, @Nonnull Color labelBackgroundColor, @Nonnull Color labelColor) {
		MallardPanel titlePanel = new MallardPanel(state);
		addChild(titlePanel);
		titlePanel.addConstraint(new PinConstraint(titlePanel, this));

		contentPanel = new MallardPanel(state);
		addChild(contentPanel);
		contentPanel.addConstraint(new BasicConstraint(contentPanel, Constraint.Attribute.Width, this));
		contentPanel.addConstraint(new BasicConstraint(contentPanel, Constraint.Attribute.Height, this, new Unit.Pixels(-32f)));
		contentPanel.addConstraint(new CenterConstraint(contentPanel, this, AxisConstraint.Axis.Horizontal));
		contentPanel.addConstraint(new BasicConstraint(contentPanel, Constraint.Attribute.Bottom, this));

		state.loadAsset(Assets.font12);
		titleLabel = new UiLabel(new GfxFont(Assets.font12));
		titlePanel.content.addChild(titleLabel);
		titleLabel.addConstraint(new BasicConstraint(titleLabel, Constraint.Attribute.Width, titlePanel.content));
		titleLabel.addConstraint(new BasicConstraint(titleLabel.getAttributes().height, new Unit.Pixels(18f)));
		titleLabel.addConstraint(new CenterConstraint(titleLabel, titlePanel.content, AxisConstraint.Axis.Horizontal));
		titleLabel.addConstraint(new BasicConstraint(titleLabel, Constraint.Attribute.Top, titlePanel.content));
		titleLabel.font.setAlignment(Alignment.Horizontal.Center.and(Alignment.Vertical.Middle));
		titleLabel.font.setColor(labelColor);
		titleLabel.text = text;
	}
}