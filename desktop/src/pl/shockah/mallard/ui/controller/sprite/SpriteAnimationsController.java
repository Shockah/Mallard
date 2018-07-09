package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.Controller;
import pl.shockah.unicorn.collection.Box;

public class SpriteAnimationsController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteFramesController framesController;

	@Nonnull
	public final SpriteProject project;

	public SpriteAnimationsController(@Nonnull SpriteController spriteController, @Nonnull SpriteFramesController framesController, @Nonnull SpriteProject project) {
		this.spriteController = spriteController;
		this.framesController = framesController;
		this.project = project;

		Box<ListView<SpriteProject.Animation.Entry>> listView = new Box<>();
		Box<Button> removeButton = new Box<>();

		setView(new VBox(4) {{
			setMaxHeight(Double.MAX_VALUE);
			getChildren().addAll(
					new HBox(4) {{
						getChildren().addAll(
								new Button("Add") {{
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);
									setOnAction(event -> {
										new TextInputDialog() {{
											setTitle("Add animation");
											setHeaderText("Add animation");
											setContentText("Name:");
										}}.showAndWait().ifPresent(result -> {
											project.animations.add(new SpriteProject.Animation.Entry(result, new SpriteProject.Animation()));
										});
									});
								}},
								new Button("Remove") {{
									removeButton.value = this;
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);
									setDisable(true);
									setOnAction(event -> {
										project.animations.remove(listView.value.getSelectionModel().selectedItemProperty().get());
									});
								}}
						);
					}},
					new ListView<SpriteProject.Animation.Entry>() {{
						listView.value = this;
						setMaxHeight(Double.MAX_VALUE);
						setCellFactory(self2 -> new Cell());
						setItems(project.animations);
						setOnMouseClicked(event -> {
							if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 2)
								return;

							SpriteProject.Animation.Entry selected = getSelectionModel().getSelectedItem();
							if (selected == null)
								return;

							//SpriteFramePreviewController previewController = new SpriteFramePreviewController(spriteController, project, selected);
							spriteController.setRightPanel(new SpriteAnimationPropertiesController(spriteController, framesController, project, selected).getView());
							//spriteController.setCenterPanel(previewController.getView());
						});

						getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
							removeButton.value.setDisable(newValue == null);
						});
					}}
			);
		}});
	}

	public static final class Cell extends ListCell<SpriteProject.Animation.Entry> {
		public Cell() {
			super();
		}

		@Override
		protected void updateItem(SpriteProject.Animation.Entry item, boolean empty) {
			super.updateItem(item, empty);

			if (empty || item == null) {
				setText("");
			} else {
				setText(item.name);
			}
		}
	}
}