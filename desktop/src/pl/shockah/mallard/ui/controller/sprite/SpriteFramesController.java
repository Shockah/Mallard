package pl.shockah.mallard.ui.controller.sprite;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
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
import pl.shockah.mallard.ui.ListViewUtilities;
import pl.shockah.mallard.ui.controller.Controller;
import pl.shockah.unicorn.collection.Box;

public class SpriteFramesController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final ListView<SpriteProject.Frame> frameList;

	public SpriteFramesController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project) {
		this.spriteController = spriteController;
		this.project = project;

		Box<ListView<SpriteProject.Frame>> listView = new Box<>();
		Box<Button> removeButton = new Box<>();

		frameList = new ListView<SpriteProject.Frame>() {{
			ListView<SpriteProject.Frame> self = this;
			listView.value = this;
			setMaxHeight(Double.MAX_VALUE);
			setCellFactory(self2 -> new Cell());
			setItems(project.frames);
			setOnDragOver(event -> {
				if (event.getGestureSource() != self && (event.getDragboard().hasImage() || event.getDragboard().hasFiles())) {
					event.acceptTransferModes(TransferMode.COPY, TransferMode.LINK);
					event.consume();
				}
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
					event.consume();
				}
			});
			setOnMouseClicked(event -> {
				if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 2)
					return;

				SpriteProject.Frame selected = getSelectionModel().getSelectedItem();
				if (selected == null)
					return;

				SpriteFramePreviewController previewController = new SpriteFramePreviewController(spriteController, project, selected);
				spriteController.setRightPanel(new SpriteFramePropertiesController(spriteController, previewController, project, selected).getView());
				spriteController.setCenterPanel(previewController.getView());
			});

			getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				removeButton.value.setDisable(newValue == null);
			});
		}};

		setView(new VBox(4) {{
			setMaxHeight(Double.MAX_VALUE);
			getChildren().addAll(
					new HBox(4) {{
						getChildren().addAll(
								new SplitMenuButton() {{
									setText("Add");
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);

									setOnAction(event -> {
										FileChooser chooser = new FileChooser();
										chooser.setTitle("Add frame");
										chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Loss-less images", "*.png", "*.bmp", "*.gif"));
										chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));

										List<File> files = chooser.showOpenMultipleDialog(Mallard.getStage());
										if (files != null) {
											for (File file : files) {
												Image image = new Image(file.toURI().toString());
												project.frames.add(new SpriteProject.Frame(image));
											}
										}
									});

									getItems().add(
											new MenuItem("Separator") {{
												setOnAction(event -> {
													project.frames.add(null);
												});
											}}
									);
								}},
								new Button("Remove") {{
									removeButton.value = this;
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);
									setDisable(true);
									setOnAction(event -> {
										project.frames.remove(listView.value.getSelectionModel().getSelectedItem());
									});
								}}
						);
					}},
					frameList
			);
		}});
	}

	public static final class Cell extends ListCell<SpriteProject.Frame> {
		@Nonnull
		public final ImageView imageView;

		public Cell() {
			super();

			imageView = new ImageView();
			setGraphic(imageView);

			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setAlignment(Pos.CENTER);

			ListViewUtilities.setupDragAndDropReorder(this, ListViewUtilities.ReorderMethod.RemoveAndInsert);
		}

		@Override
		protected void updateItem(SpriteProject.Frame item, boolean empty) {
			super.updateItem(item, empty);

			imageView.imageProperty().unbind();
			if (empty || item == null) {
				prefHeight(8);
				imageView.setImage(null);
			} else {
				prefHeight(USE_COMPUTED_SIZE);
				imageView.imageProperty().bind(item.image);
			}
		}
	}
}