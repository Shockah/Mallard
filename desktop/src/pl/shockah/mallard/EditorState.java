package pl.shockah.mallard;

import javax.annotation.Nullable;

import pl.shockah.godwit.State;
import pl.shockah.godwit.constraint.BasicConstraint;
import pl.shockah.godwit.constraint.Constraint;
import pl.shockah.godwit.constraint.PinConstraint;
import pl.shockah.godwit.ui.UiButton;
import pl.shockah.godwit.ui.UiPanel;
import pl.shockah.godwit.ui.Unit;
import pl.shockah.mallard.ui.MallardButton;
import pl.shockah.mallard.ui.MallardPanel;
import pl.shockah.mallard.ui.SpriteDisplayPanel;

public final class EditorState extends State {
	@Nullable
	public AssetType assetType;

	@Nullable
	public UiPanel assetContainer;

	public EditorState() {
		ui.addChild(new SpriteDisplayPanel(requestAsset(Assets.Sprite.transparencyGrid)) {
			{
				scale.set(8f, 8f);
				anchorPoint.set(0f, 0f);
				screenAnchorPoint.set(0f, 0f);

				addConstraint(new PinConstraint(this, ui));
			}
		});

		ui.addChild(createToolPanel());
	}

	private MallardPanel createToolPanel() {
		MallardPanel panel = new MallardPanel(this);
		panel.addConstraint(new BasicConstraint(panel, Constraint.Attribute.Width, new Unit.Pixels(48f)));
		panel.addConstraint(new BasicConstraint(panel, Constraint.Attribute.Height, ui, new Unit.Pixels(-24f)));
		panel.addConstraint(new BasicConstraint(panel, Constraint.Attribute.Left, ui, new Unit.Pixels(12f)));
		panel.addConstraint(new BasicConstraint(panel, Constraint.Attribute.Top, ui, new Unit.Pixels(12f)));

		UiButton newButton = new MallardButton.Icon(this, Assets.Icon.newIcon, button -> {
			System.out.println("new button pressed");
		});
		panel.content.addChild(newButton);
		newButton.addConstraint(new BasicConstraint(newButton, Constraint.Attribute.Width, new Unit.Pixels(32f)));
		newButton.addConstraint(new BasicConstraint(newButton, Constraint.Attribute.Height, new Unit.Pixels(32f)));
		newButton.addConstraint(BasicConstraint.withParent(newButton, Constraint.Attribute.Top));
		newButton.addConstraint(BasicConstraint.withParent(newButton, Constraint.Attribute.CenterX));

		return panel;
	}
}