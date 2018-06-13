package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import pl.shockah.godwit.State;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramesPanel extends MallardPanel {
	@Nonnull
	public final SpriteProject project;

	public SpriteFramesPanel(@Nonnull State state, @Nonnull SpriteProject project) {
		super(state);
		this.project = project;
	}
}