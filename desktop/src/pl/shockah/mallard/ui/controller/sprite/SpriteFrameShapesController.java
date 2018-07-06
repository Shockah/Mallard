package pl.shockah.mallard.ui.controller.sprite;

import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.ShapeManager;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.Controller;
import pl.shockah.mallard.ui.controller.sprite.editor.SpriteFrameEditor;
import pl.shockah.unicorn.collection.Box;

public class SpriteFrameShapesController extends Controller {
	@Nonnull
	public final SpriteFramePreviewController previewController;

	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Frame frame;

	@SuppressWarnings("unchecked")
	public SpriteFrameShapesController(@Nonnull SpriteFramePreviewController previewController, @Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.previewController = previewController;
		this.project = project;
		this.frame = frame;

		Box<ListView<SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>>> listView = new Box<>();
		Box<Button> removeButton = new Box<>();

		setView(new VBox(4) {{
			setPadding(new Insets(4));
			setMaxHeight(Double.MAX_VALUE);
			getChildren().addAll(
					new HBox(4) {{
						getChildren().addAll(
								new MenuButton("Add") {{
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);
									getItems().addAll(
											Mallard.shapeManager.types.stream()
													.map(entry -> new MenuItem(entry.name) {{
														setOnAction(event -> {
															new TextInputDialog() {{
																setTitle(String.format("Add `%s` shape", entry.name));
																setHeaderText(String.format("Add `%s` shape", entry.name));
																setContentText("Name:");
															}}.showAndWait().ifPresent(result -> {
																ShapeManager.Entry<Shape.Filled> rawEntry = (ShapeManager.Entry<Shape.Filled>) entry;

																ListChangeListener<SpriteFrameEditor> temporaryListener = c -> {
																	while (c.next()) {
																		for (SpriteFrameEditor editor : c.getAddedSubList()) {
																			editor.setActive();
																			break;
																		}
																	}
																};

																previewController.editors.addListener(temporaryListener);
																frame.shapes.add(new SpriteProject.Frame.ShapeEntry<>(frame, rawEntry, result, null));
																previewController.editors.removeListener(temporaryListener);
															});
														});
													}})
													.collect(Collectors.toList())
									);
								}},
								new Button("Remove") {{
									removeButton.value = this;
									setMaxWidth(Double.MAX_VALUE);
									HBox.setHgrow(this, Priority.ALWAYS);
									setDisable(true);
									setOnAction(event -> {
										frame.shapes.remove(listView.value.getSelectionModel().selectedItemProperty().get());
									});
								}}
						);
					}},
					new ListView<SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>>() {{
						listView.value = this;
						setMaxHeight(Double.MAX_VALUE);
						setCellFactory(self2 -> new Cell());
						setItems(frame.shapes);

						getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
							removeButton.value.setDisable(newValue == null);
						});
					}}
			);
		}});
	}

	public final class Cell extends ListCell<SpriteProject.Frame.ShapeEntry<? extends Shape.Filled>> {
		@Nonnull
		private final Label label;

		@Nonnull
		private final ColorPicker colorPicker;

		@Nonnull
		private final CheckBox visibleCheckbox;

		public Cell() {
			super();

			label = new Label();
			colorPicker = new ColorPicker();
			visibleCheckbox = new CheckBox();

			colorPicker.getStyleClass().add("button");
			colorPicker.setStyle("-fx-color-label-visible: false;");

			setGraphic(new HBox(4) {{
				setAlignment(Pos.CENTER);

				getChildren().addAll(
						label,
						new Pane() {{
							HBox.setHgrow(this, Priority.ALWAYS);
						}},
						colorPicker,
						visibleCheckbox
				);
			}});

			setOnMouseClicked(event -> {
				SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> item = getItem();
				if (item == null)
					return;

				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
					item.editor.setActive();
			});
		}

		@Override
		protected void updateItem(SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> item, boolean empty) {
			super.updateItem(item, empty);

			visibleCheckbox.selectedProperty().unbind();
			colorPicker.valueProperty().unbind();

			if (empty || item == null) {
				getGraphic().setVisible(false);
			} else {
				getGraphic().setVisible(true);
				label.setText(item.name);
				visibleCheckbox.selectedProperty().bindBidirectional(item.visible);
				colorPicker.valueProperty().bindBidirectional(item.color);
			}
		}
	}
}