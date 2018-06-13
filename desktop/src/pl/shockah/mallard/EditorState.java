package pl.shockah.mallard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.AxisConstraint;
import pl.shockah.godwit.constraint.BasicConstraint;
import pl.shockah.godwit.constraint.BetweenConstraint;
import pl.shockah.godwit.constraint.ChainChildrenConstraint;
import pl.shockah.godwit.constraint.Constraint;
import pl.shockah.godwit.constraint.PinConstraint;
import pl.shockah.godwit.ui.UiPanel;
import pl.shockah.godwit.ui.Unit;
import pl.shockah.mallard.project.Project;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.MallardButton;
import pl.shockah.mallard.ui.MallardPanel;
import pl.shockah.mallard.ui.SpriteDisplayPanel;

public final class EditorState extends State {
	@Nullable
	public Project project;

	@Nonnull
	public final UiPanel filePanel;

	@Nullable
	public UiPanel projectContainer;

	public EditorState() {
		ui.addChild(new SpriteDisplayPanel(requestAsset(Assets.Sprite.transparencyGrid)) {
			{
				scale.set(8f, 8f);
				anchorPoint.set(0f, 0f);
				screenAnchorPoint.set(0f, 0f);

				addConstraint(new PinConstraint(this, ui));
			}
		});

		ui.addChild(filePanel = createFilePanel());
		setupSpriteProject();
	}

	private MallardPanel createFilePanel() {
		MallardPanel panel = new MallardPanel(this);
		panel.addConstraint(new BasicConstraint(panel.getAttributes().width, new Unit.Pixels(48f)));
		panel.addConstraint(new BasicConstraint(panel, Constraint.Attribute.Height, ui, new Unit.Pixels(-24f)));
		panel.addConstraint(new BasicConstraint(panel, Constraint.Attribute.Left, ui, new Unit.Pixels(12f)));
		panel.addConstraint(new BasicConstraint(panel, Constraint.Attribute.Top, ui, new Unit.Pixels(12f)));

		new MallardButton.Icon(this, Assets.Icon.newIcon, button -> {
			System.out.println("new button pressed");
		}) {
			{
				panel.content.addChild(this);
				addConstraint(new BasicConstraint(getAttributes().width, new Unit.Pixels(32f)));
				addConstraint(new BasicConstraint(getAttributes().height, new Unit.Pixels(32f)));
				addConstraint(BasicConstraint.withParent(this, Constraint.Attribute.CenterX));
			}
		};

		new MallardButton.Icon(this, Assets.Icon.load, button -> {
			System.out.println("load button pressed");
		}) {
			{
				panel.content.addChild(this);
				addConstraint(new BasicConstraint(getAttributes().width, new Unit.Pixels(32f)));
				addConstraint(new BasicConstraint(getAttributes().height, new Unit.Pixels(32f)));
				addConstraint(BasicConstraint.withParent(this, Constraint.Attribute.CenterX));
			}
		};

		new MallardButton.Icon(this, Assets.Icon.save, button -> {
			System.out.println("save button pressed");
		}) {
			{
				panel.content.addChild(this);
				addConstraint(new BasicConstraint(getAttributes().width, new Unit.Pixels(32f)));
				addConstraint(new BasicConstraint(getAttributes().height, new Unit.Pixels(32f)));
				addConstraint(BasicConstraint.withParent(this, Constraint.Attribute.CenterX));
			}
		};

		panel.content.addConstraint(new ChainChildrenConstraint<>(panel.content, AxisConstraint.Axis.Vertical, new Unit.Pixels(4f), 0f));

		return panel;
	}

	private void setupSpriteProject() {
		SpriteProject project = new SpriteProject();
		this.project = project;

		if (projectContainer != null)
			projectContainer.removeFromParent();

		projectContainer = new UiPanel();
		projectContainer.addConstraint(new BetweenConstraint(projectContainer, filePanel.getAttributes().right, ui.getAttributes().right));
		projectContainer.addConstraint(new PinConstraint(projectContainer, ui, PinConstraint.Sides.Vertical));
		ui.addChild(projectContainer);
	}
}