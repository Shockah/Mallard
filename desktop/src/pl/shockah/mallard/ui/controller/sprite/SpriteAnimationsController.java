package pl.shockah.mallard.ui.controller.sprite;

import javax.annotation.Nonnull;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
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
	public final SpriteProject project;

	public SpriteAnimationsController(@Nonnull SpriteController spriteController, @Nonnull SpriteProject project) {
		this.spriteController = spriteController;
		this.project = project;

		Box<ListView<SpriteProject.Animation.Entry>> listView = new Box<>();
		Box<Button> removeButton = new Box<>();

		setView(new VBox(4) {{
			setMaxHeight(Double.MAX_VALUE);
			getChildren().add(new HBox(4) {{
				getChildren().add(new Button("Add") {{
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
				}});
				getChildren().add(new Button("Remove") {{
					removeButton.value = this;
					setMaxWidth(Double.MAX_VALUE);
					HBox.setHgrow(this, Priority.ALWAYS);
					setDisable(true);
					setOnAction(event -> {
						project.animations.remove(listView.value.getSelectionModel().selectedItemProperty().get());
					});
				}});
			}});
			getChildren().add(new ListView<SpriteProject.Animation.Entry>() {{
				listView.value = this;
				setMaxHeight(Double.MAX_VALUE);
				setCellFactory(self2 -> new Cell());
				setItems(project.animations);

				getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
					removeButton.value.setDisable(newValue == null);
				});
			}});
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