package pl.shockah.mallard.ui.controller.sprite.editor;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Shape;
import pl.shockah.mallard.project.SpriteProject;

public abstract class ShapeEditor<S extends Shape.Filled> extends SpriteFrameEditor {
	@Nonnull
	public final SpriteProject.Frame.ShapeEntry<S> entry;

	public ShapeEditor(@Nonnull SpriteProject.Frame frame, @Nonnull SpriteProject.Frame.ShapeEntry<S> entry) {
		super(frame);
		this.entry = entry;
	}
}