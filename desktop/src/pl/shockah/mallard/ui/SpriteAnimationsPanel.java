package pl.shockah.mallard.ui;

import com.badlogic.gdx.graphics.Color;

import javax.annotation.Nonnull;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.AxisConstraint;
import pl.shockah.godwit.constraint.BasicConstraint;
import pl.shockah.godwit.constraint.ChainChildrenConstraint;
import pl.shockah.godwit.constraint.Constraint;
import pl.shockah.godwit.constraint.FitChildrenConstraint;
import pl.shockah.godwit.constraint.PinConstraint;
import pl.shockah.godwit.gl.ColorUtil;
import pl.shockah.godwit.ui.UiButton;
import pl.shockah.godwit.ui.UiScroll;
import pl.shockah.godwit.ui.Unit;
import pl.shockah.mallard.Assets;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.Math2;
import pl.shockah.unicorn.color.HSLuvColorSpace;
import pl.shockah.unicorn.color.LCHColorSpace;
import pl.shockah.unicorn.color.LabColorSpace;

public class SpriteAnimationsPanel extends MallardGroupPanel {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Animation animation;

	@Nonnull
	public final UiScroll scroll = new UiScroll(UiScroll.Direction.Vertical);

	public SpriteAnimationsPanel(@Nonnull State state, @Nonnull SpriteProject project, @Nonnull SpriteProject.Animation animation) {
		super(state, "Animations", Color.GOLD);
		this.project = project;
		this.animation = animation;

		contentPanel.content.addChild(scroll);
		scroll.addConstraint(PinConstraint.create(scroll));

		HSLuvColorSpace hsl = HSLuvColorSpace.from(LCHColorSpace.from(LabColorSpace.from(ColorUtil.toXYZ(Color.GOLD))));
		hsl.l = Math2.clamp(hsl.l + 0.05f, 0f, 1f);
		Color dialogTitleColor = ColorUtil.toGdx(hsl.toRGB());

		UiButton addButton = new MallardButton.Icon(state, Assets.Icon.plus, button -> {
			state.ui.addChild(new MallardDialog(state, "Add animation", dialogTitleColor) {{
				content.addConstraint(content.getAttributes().width.constraint(new Unit.Pixels(320f)));
				content.addConstraint(content.getAttributes().height.constraint(new Unit.Pixels(240f)));

				MallardTextbox nameTextbox = new MallardTextbox(state);
				content.contentPanel.content.addChild(nameTextbox);
				nameTextbox.addConstraint(BasicConstraint.withParent(nameTextbox, Constraint.Attribute.Width));
				nameTextbox.addConstraint(nameTextbox.getAttributes().height.constraint(new Unit.Pixels(32f)));
			}});
		});
		scroll.content.addChild(addButton);
		addButton.addConstraint(PinConstraint.create(addButton, PinConstraint.Sides.Horizontal));
		addButton.addConstraint(new BasicConstraint(addButton.getAttributes().height, new Unit.Pixels(32f)));

		scroll.content.addConstraint(new FitChildrenConstraint<>(scroll.content, AxisConstraint.Axis.Vertical, new Unit.Pixels(4f)));
		scroll.content.addConstraint(new ChainChildrenConstraint<>(scroll.content, AxisConstraint.Axis.Vertical));
	}
}