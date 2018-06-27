package pl.shockah.mallard.ui;

import java.io.File;

import javax.annotation.Nonnull;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import pl.shockah.mallard.project.SpriteProject;

public class SpriteFramesController {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final ListView<SpriteProject.Subsprite> listView;

	public SpriteFramesController(@Nonnull SpriteProject project) {
		this.project = project;

		listView = new ListView<>();
		listView.setCellFactory(listView -> new Cell());
		listView.setItems(project.subsprites);
		listView.setOnDragOver(event -> {
			if (event.getGestureSource() != listView && (event.getDragboard().hasImage() || event.getDragboard().hasFiles()))
				event.acceptTransferModes(TransferMode.ANY);
			event.consume();
		});
		listView.setOnDragDropped(event -> {
			if (event.getDragboard().hasImage()) {
				project.subsprites.add(new SpriteProject.Subsprite(event.getDragboard().getImage()));
				event.setDropCompleted(true);
			} else if (event.getDragboard().hasFiles()) {
				for (File file : event.getDragboard().getFiles()) {
					Image image = new Image(file.toURI().toString());
					project.subsprites.add(new SpriteProject.Subsprite(image));
				}
				event.setDropCompleted(true);
			} else {
				event.setDropCompleted(false);
			}
			event.consume();
		});
	}

	public static final class Cell extends ListCell<SpriteProject.Subsprite> {
		@Nonnull
		public final ImageView imageView;

		public Cell() {
			super();

			imageView = new ImageView();
			getChildren().add(imageView);
		}

		@Override
		protected void updateItem(SpriteProject.Subsprite item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				imageView.setImage(null);
			} else {
				imageView.setImage(item.image);
			}
		}
	}
}