package pl.shockah.mallard.ui.controller;

import javax.annotation.Nonnull;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.ui.BindUtilities;

public class WelcomeController extends Controller {
	@Nonnull
	public final AppController appController;

	public WelcomeController(@Nonnull AppController appController) {
		this.appController = appController;

		setView(new HBox() {{
			VBox.setVgrow(this, Priority.ALWAYS);
			setAlignment(Pos.CENTER);

			getChildren().add(new VBox() {{
				setAlignment(Pos.CENTER);

				getChildren().add(new TitledPane("Welcome", new VBox(8) {{
					BindUtilities.bind(Mallard.getStage().widthProperty(), width -> setMinWidth(width.doubleValue() * 0.4));
					setPadding(new Insets(24));

					getChildren().addAll(
							new Label("Mallard") {{
								setStyle("-fx-font-size: 2em;");
							}},
							new Region() {{
								setPrefHeight(16);
							}},
							new Hyperlink("Start a new project") {{
								setOnAction(event -> appController.newAction());
							}},
							new Hyperlink("Open an existing project") {{
								setOnAction(event -> appController.openAction());
							}}
					);
				}}) {{
					setCollapsible(false);
				}});
			}});
		}});


//		setView(new BorderPane() {{
//			Region center = new TitledPane("Welcome", new VBox() {{
//				getChildren().add(new Label("Mallard") {{
//					setStyle("-fx-font-size: 2em;");
//				}});
//			}}) {{
//				setCollapsible(false);
//			}};
//
//			setCenter(center);
//
//			setLeft(new Region() {{
//				prefWidthProperty().bind(center.widthProperty());
//				prefHeightProperty().bind(center.heightProperty());
//			}});
//			setRight(new Region() {{
//				prefWidthProperty().bind(center.widthProperty());
//				prefHeightProperty().bind(center.heightProperty());
//			}});
//			setTop(new Region() {{
//				prefWidthProperty().bind(center.widthProperty());
//				prefHeightProperty().bind(center.heightProperty());
//			}});
//			setBottom(new Region() {{
//				prefWidthProperty().bind(center.widthProperty());
//				prefHeightProperty().bind(center.heightProperty());
//			}});
//		}});



//		setView(new TitledPane("Welcome", new Pane() {{
//			setPrefWidth(0.0);
//			setPrefHeight(0.0);
//
//			getChildren().add(new Label("Mallard") {{
//				setStyle("-fx-font-size: 2em;");
//			}});
//		}}) {{
//
//			setCollapsible(false);
//		}});
	}
}