package pl.shockah.mallard.ui.controller;

import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.unicorn.collection.Box;

public class SpriteFrameShapesController extends Controller {
	@Nonnull
	public final SpriteProject project;

	@Nonnull
	public final SpriteProject.Frame frame;

	public SpriteFrameShapesController(@Nonnull SpriteProject project, @Nonnull SpriteProject.Frame frame) {
		this.project = project;
		this.frame = frame;

		Box<ListView<SpriteProject.Frame.ShapeEntry>> listView = new Box<>();
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
																//TODO: shape controller creation
																//frame.shapes.add(new SpriteProject.Frame.ShapeEntry(result, entry.))
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
					new ListView<SpriteProject.Frame.ShapeEntry>() {{
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

	public static final class Cell extends ListCell<SpriteProject.Frame.ShapeEntry> {
		public Cell() {
			super();
		}

		@Override
		protected void updateItem(SpriteProject.Frame.ShapeEntry item, boolean empty) {
			super.updateItem(item, empty);

			if (empty || item == null) {
				setText("");
			} else {
				setText(item.name);
			}
		}
	}
}