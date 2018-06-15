package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.AxisConstraint;
import pl.shockah.godwit.constraint.BasicConstraint;
import pl.shockah.godwit.constraint.ChainChildrenConstraint;
import pl.shockah.godwit.constraint.FitChildrenConstraint;
import pl.shockah.godwit.constraint.PinConstraint;
import pl.shockah.godwit.ui.UiButton;
import pl.shockah.godwit.ui.UiScroll;
import pl.shockah.godwit.ui.Unit;
import pl.shockah.mallard.Assets;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramesPanel extends MallardPanel {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final UiScroll scroll = new UiScroll(UiScroll.Direction.Vertical);

	public SpriteFramesPanel(@Nonnull State state, @Nonnull SpriteProject project) {
		super(state);
		this.project = project;

		content.addChild(scroll);
		scroll.addConstraint(PinConstraint.create(scroll));

		UiButton addButton = new MallardButton.Icon(state, Assets.Icon.plus, button -> {});
		scroll.content.addChild(addButton);
		addButton.addConstraint(PinConstraint.create(addButton, PinConstraint.Sides.Horizontal));
		addButton.addConstraint(new BasicConstraint(addButton.getAttributes().height, new Unit.Pixels(32f)));

		scroll.content.addConstraint(new FitChildrenConstraint<>(scroll.content, AxisConstraint.Axis.Vertical));
		scroll.content.addConstraint(new ChainChildrenConstraint<>(scroll.content, AxisConstraint.Axis.Vertical));
	}
}