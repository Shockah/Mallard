package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.collection.Box;
import pl.shockah.unicorn.javafx.Controller;
import pl.shockah.unicorn.javafx.ListViews;

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

		setRoot(new VBox(4) {{
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

		@Nonnull
		public final Spinner<Double> offsetXSpinner;

		@Nonnull
		public final Spinner<Double> offsetYSpinner;

		public Cell() {
			super();

			imageView = new ImageView();

			// HACK, doesn't seem to work otherwise
			DoubleProperty widthMax = new SimpleDoubleProperty(this, "width_max_hack", Double.MAX_VALUE);
			DoubleProperty width60 = new SimpleDoubleProperty(this, "width_60_hack", 60);

			relativeDurationSpinner = new Spinner<>(0.0, Double.MAX_VALUE, 1.0, 0.05);
			relativeDurationSpinner.setEditable(true);
			relativeDurationSpinner.maxWidthProperty().bind(widthMax);

			offsetXSpinner = new Spinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 0.5);
			offsetXSpinner.setEditable(true);
			offsetXSpinner.prefWidthProperty().bind(width60);

			offsetYSpinner = new Spinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 0.5);
			offsetYSpinner.setEditable(true);
			offsetYSpinner.prefWidthProperty().bind(width60);

			setGraphic(new HBox(4) {{
				setAlignment(Pos.CENTER);

				getChildren().addAll(
						imageView,
						new Pane() {{
							HBox.setHgrow(this, Priority.ALWAYS);
						}},
						new VBox(4) {{
							setAlignment(Pos.CENTER);

							getChildren().addAll(
									relativeDurationSpinner,
									new HBox(4) {{
										setAlignment(Pos.CENTER);

										getChildren().addAll(
												new Label("X:"),
												offsetXSpinner,
												new Label("Y:"),
												offsetYSpinner
										);
									}}
							);
						}}
				);
			}});

			ListViews.setupDragAndDropReorder(this, ListViews.ReorderMethod.RemoveAndInsert);
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

				item.offset.unbind();
				item.offset.bind(Bindings.createObjectBinding(() -> {
					return new Vec2(offsetXSpinner.getValue().floatValue(), offsetYSpinner.getValue().floatValue());
				}, offsetXSpinner.valueProperty(), offsetYSpinner.valueProperty()));
			}
		}
	}
}