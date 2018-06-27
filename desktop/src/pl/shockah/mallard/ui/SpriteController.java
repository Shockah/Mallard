package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import javafx.scene.layout.BorderPane;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteController {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final BorderPane view;

	@Nonnull
	protected final SpriteFramesController framesController;

	public SpriteController(@Nonnull SpriteProject project) {
		this.project = project;

		view = new BorderPane();

		framesController = new SpriteFramesController(project);
		view.setLeft(framesController.listView);
	}
}