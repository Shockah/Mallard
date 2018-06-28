package pl.shockah.mallard.ui;

import java.io.File;

import javax.annotation.Nonnull;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.collection.Box;

public class SpriteFramesController extends Controller {
	@Nonnull
	public final SpriteProject project;

	public SpriteFramesController(@Nonnull SpriteProject project) {
		this.project = project;

		Box<ListView<SpriteProject.Subsprite>> listView = new Box<>();
		Box<Button> removeButton = new Box<>();

		setView(new VBox(4) {{
			getChildren().add(new HBox(4) {{
				getChildren().add(new Button("Add") {{
					setMaxWidth(Double.MAX_VALUE);
					HBox.setHgrow(this, Priority.ALWAYS);
					setOnAction(event -> {
						FileChooser chooser = new FileChooser();
						chooser.setTitle("Add frame");
						chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Loss-less images", "png", "bmp", "gif"));
						for (File file : chooser.showOpenMultipleDialog(Mallard.getStage())) {
							Image image = new Image(file.toURI().toString());
							project.subsprites.add(new SpriteProject.Subsprite(image));
						}
					});
				}});
				getChildren().add(new Button("Remove") {{
					removeButton.value = this;
					setMaxWidth(Double.MAX_VALUE);
					HBox.setHgrow(this, Priority.ALWAYS);
					setDisable(true);
					setOnAction(event -> {
						project.subsprites.remove(listView.value.getSelectionModel().selectedItemProperty().get());
					});
				}});
			}});
			getChildren().add(new ListView<SpriteProject.Subsprite>() {{
				ListView<SpriteProject.Subsprite> self = this;
				listView.value = this;
				setCellFactory(self2 -> new Cell());
				setItems(project.subsprites);
				setOnDragOver(event -> {
					if (event.getGestureSource() != self && (event.getDragboard().hasImage() || event.getDragboard().hasFiles()))
						event.acceptTransferModes(TransferMode.ANY);
					event.consume();
				});
				setOnDragDropped(event -> {
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

				getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
					removeButton.value.setDisable(newValue == null);
				});
			}});
		}});
	}

	public static final class Cell extends ListCell<SpriteProject.Subsprite> {
		@Nonnull
		public final ImageView imageView;

		public Cell() {
			super();

			imageView = new ImageView();
			setGraphic(imageView);
		}

		@Override
		protected void updateItem(SpriteProject.Subsprite item, boolean empty) {
			super.updateItem(item, empty);

			if (empty || item == null) {
				imageView.setImage(null);
			} else {
				imageView.setImage(item.image);
			}
		}
	}
}