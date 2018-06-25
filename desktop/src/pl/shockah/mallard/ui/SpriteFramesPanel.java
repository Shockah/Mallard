package pl.shockah.mallard.ui;

import com.badlogic.gdx.graphics.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.AxisConstraint;
import pl.shockah.godwit.constraint.BasicConstraint;
import pl.shockah.godwit.constraint.BetweenConstraint;
import pl.shockah.godwit.constraint.ChainChildrenConstraint;
import pl.shockah.godwit.constraint.Constraint;
import pl.shockah.godwit.constraint.FitChildrenConstraint;
import pl.shockah.godwit.constraint.PinConstraint;
import pl.shockah.godwit.ui.UiButton;
import pl.shockah.godwit.ui.UiPanel;
import pl.shockah.godwit.ui.UiScroll;
import pl.shockah.godwit.ui.Unit;
import pl.shockah.mallard.Assets;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramesPanel extends MallardGroupPanel {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final UiScroll scroll = new UiScroll(UiScroll.Direction.Vertical);

	@Nullable
	public UiPanel nextContainer;

	public SpriteFramesPanel(@Nonnull State state, @Nonnull SpriteProject project) {
		super(state, "Frames", Color.SKY);
		this.project = project;

		contentPanel.content.addChild(scroll);
		scroll.addConstraint(PinConstraint.create(scroll));

		UiButton animationsButton = new MallardButton.Label(state, "Animations", button -> {
			setupAnimationsPanel(state);
		});
		scroll.content.addChild(animationsButton);
		animationsButton.addConstraint(PinConstraint.create(animationsButton, PinConstraint.Sides.Horizontal));
		animationsButton.addConstraint(new BasicConstraint(animationsButton.getAttributes().height, new Unit.Pixels(32f)));

		UiButton addButton = new MallardButton.Icon(state, Assets.Icon.plus, button -> {});
		scroll.content.addChild(addButton);
		addButton.addConstraint(PinConstraint.create(addButton, PinConstraint.Sides.Horizontal));
		addButton.addConstraint(new BasicConstraint(addButton.getAttributes().height, new Unit.Pixels(32f)));

		scroll.content.addConstraint(new FitChildrenConstraint<>(scroll.content, AxisConstraint.Axis.Vertical, new Unit.Pixels(4f)));
		scroll.content.addConstraint(new ChainChildrenConstraint<>(scroll.content, AxisConstraint.Axis.Vertical));
	}

	private void setupAnimationsPanel(@Nonnull State state) {
		if (nextContainer != null)
			nextContainer.removeFromParent();

		nextContainer = new UiPanel();
		nextContainer.addConstraint(new BetweenConstraint(nextContainer, getAttributes().right, state.ui.getAttributes().right));
		nextContainer.addConstraint(new PinConstraint(nextContainer, state.ui, PinConstraint.Sides.Vertical));
		getParent().addChild(nextContainer);

		MallardGroupPanel framesPanel = new SpriteAnimationsPanel(state, project, new SpriteProject.Animation());
		nextContainer.addChild(framesPanel);
		framesPanel.addConstraint(new BasicConstraint(framesPanel.getAttributes().width, new Unit.Pixels(128f)));
		framesPanel.addConstraint(BasicConstraint.withParent(framesPanel, Constraint.Attribute.Height, new Unit.Pixels(-24f)));
		framesPanel.addConstraint(BasicConstraint.withParent(framesPanel, Constraint.Attribute.Left, new Unit.Pixels(12f)));
		framesPanel.addConstraint(BasicConstraint.withParent(framesPanel, Constraint.Attribute.Top, new Unit.Pixels(12f)));
	}
}