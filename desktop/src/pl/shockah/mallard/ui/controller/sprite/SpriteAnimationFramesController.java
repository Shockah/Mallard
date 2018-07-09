package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.ListViewUtilities;
import pl.shockah.mallard.ui.controller.Controller;
import pl.shockah.unicorn.collection.Box;

public class SpriteAnimationFramesController extends Controller {
	@Nonnull
	public final SpriteController spriteController;

	@Nonnull
	public final SpriteFramesController framesController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Animation.Entry animationEntry;

	public SpriteAnimationFramesController(@Nonnull SpriteController spriteController, @Nonnull SpriteFramesController framesController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Animation.Entry animationEntry) {
		this.spriteController = spriteController;
		this.framesController = framesController;
		this.project = project;
		this.animationEntry = animationEntry;

		Box<ListView<SpriteProject.Animation.Frame>> listView = new Box<>();
		Box<Button> removeButton = new Box<>();

		setView(new VBox(4) {{
			setPadding(new Insets(4));
			setMaxHeight(Double.MAX_VALUE);
			getChildren().addAll(
					new HBox(4) {{
						getChildren().addAll(
								new Button("Add") {{
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);
									disableProperty().bind(Bindings.createBooleanBinding(() -> {
										return framesController.frameList.getSelectionModel().getSelectedItem() == null;
									}, framesController.frameList.getSelectionModel().selectedItemProperty()));
									setOnAction(event -> {
										SpriteProject.Frame selected = framesController.frameList.getSelectionModel().getSelectedItem();
										if (selected != null)
											animationEntry.animation.frames.add(new SpriteProject.Animation.Frame(selected));
									});
								}},
								new Button("Remove") {{
									removeButton.value = this;
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);
									setDisable(true);
									setOnAction(event -> {
										animationEntry.animation.frames.remove(listView.value.getSelectionModel().getSelectedItem());
									});
								}}
						);
					}},
					new ListView<SpriteProject.Animation.Frame>() {{
						listView.value = this;
						setMaxHeight(Double.MAX_VALUE);
						setCellFactory(self2 -> new Cell());
						setItems(animationEntry.animation.frames);
						setOnMouseClicked(event -> {
							if (event.getButton() != MouseButton.PRIMARY || event.getClickCount() != 2)
								return;

							SpriteProject.Animation.Frame selected = getSelectionModel().getSelectedItem();
							if (selected == null)
								return;

							//TODO: show specific frame preview
						});

						getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
							removeButton.value.setDisable(newValue == null);
						});
					}}
			);
		}});
	}

	public static final class Cell extends ListCell<SpriteProject.Animation.Frame> {
		@Nonnull
		public final ImageView imageView;

		@Nonnull
		public final Spinner<Double> relativeDurationSpinner;

		public Cell() {
			super();

			imageView = new ImageView();

			relativeDurationSpinner = new Spinner<>(0.0, Double.MAX_VALUE, 1.0, 0.05);
			relativeDurationSpinner.setEditable(true);

			setGraphic(new HBox(4) {{
				setAlignment(Pos.CENTER);

				getChildren().addAll(
						imageView,
						new Pane() {{
							HBox.setHgrow(this, Priority.ALWAYS);
						}},
						relativeDurationSpinner
				);
			}});

			ListViewUtilities.setupDragAndDropReorder(this, ListViewUtilities.ReorderMethod.RemoveAndInsert);
		}

		@Override
		protected void updateItem(SpriteProject.Animation.Frame item, boolean empty) {
			super.updateItem(item, empty);

			imageView.imageProperty().unbind();

			if (empty || item == null) {
				getGraphic().setVisible(false);
			} else {
				getGraphic().setVisible(true);
				imageView.imageProperty().bind(item.frame.image);
				item.relativeDuration.unbind();
				item.relativeDuration.bind(relativeDurationSpinner.valueProperty());
			}
		}
	}
}