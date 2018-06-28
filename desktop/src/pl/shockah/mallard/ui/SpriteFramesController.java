package pl.shockah.mallard.ui;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	public SpriteFramesController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project) {
		this.spriteController = spriteController;
		this.project = project;

		Box<ListView<SpriteProject.Frame>> listView = new Box<>();
		Box<Button> removeButton = new Box<>();

		setView(new VBox(4) {{
			setMaxHeight(Double.MAX_VALUE);
			getChildren().add(new HBox(4) {{
				getChildren().add(new Button("Add") {{
					setMaxWidth(Double.MAX_VALUE);
					HBox.setHgrow(this, Priority.ALWAYS);
					setOnAction(event -> {
						FileChooser chooser = new FileChooser();
						chooser.setTitle("Add frame");
						chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Loss-less images", "png", "bmp", "gif"));

						List<File> files = chooser.showOpenMultipleDialog(Mallard.getStage());
						if (files != null) {
							for (File file : files) {
								Image image = new Image(file.toURI().toString());
								project.frames.add(new SpriteProject.Frame(image));
							}
						}
					});
				}});
				getChildren().add(new Button("Remove") {{
					removeButton.value = this;
					setMaxWidth(Double.MAX_VALUE);
					HBox.setHgrow(this, Priority.ALWAYS);
					setDisable(true);
					setOnAction(event -> {
						project.frames.remove(listView.value.getSelectionModel().selectedItemProperty().get());
					});
				}});
			}});
			getChildren().add(new ListView<SpriteProject.Frame>() {{
				ListView<SpriteProject.Frame> self = this;
				listView.value = this;
				setMaxHeight(Double.MAX_VALUE);
				setCellFactory(self2 -> new Cell());
				setItems(project.frames);
				setOnDragOver(event -> {
					if (event.getGestureSource() != self && (event.getDragboard().hasImage() || event.getDragboard().hasFiles()))
						event.acceptTransferModes(TransferMode.ANY);
					event.consume();
				});
				setOnDragDropped(event -> {
					if (event.getDragboard().hasImage()) {
						project.frames.add(new SpriteProject.Frame(event.getDragboard().getImage()));
						event.setDropCompleted(true);
					} else if (event.getDragboard().hasFiles()) {
						for (File file : event.getDragboard().getFiles()) {
							Image image = new Image(file.toURI().toString());
							project.frames.add(new SpriteProject.Frame(image));
						}
						event.setDropCompleted(true);
					} else {
						event.setDropCompleted(false);
					}
					event.consume();
				});
				setOnMouseClicked(event -> {
					if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 2)
						return;

					SpriteProject.Frame selected = getSelectionModel().getSelectedItem();
					if (selected == null)
						return;

					spriteController.setRightPanel(new SpriteFramePropertiesController(spriteController, project, selected).getView());
					spriteController.setCenterPanel(new SpriteFramePreviewController(spriteController, project, selected).getView());
				});

				getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
					removeButton.value.setDisable(newValue == null);
				});
			}});
		}});
	}

	public static final class Cell extends ListCell<SpriteProject.Frame> {
		@Nonnull
		public final ImageView imageView;

		public Cell() {
			super();

			imageView = new ImageView();
			setGraphic(imageView);
		}

		@Override
		protected void updateItem(SpriteProject.Frame item, boolean empty) {
			super.updateItem(item, empty);

			if (empty || item == null) {
				imageView.setImage(null);
			} else {
				imageView.setImage(item.image);
			}
		}
	}
}