package pl.shockah.mallard.ui.controller;

import org.fxmisc.easybind.EasyBind;

import javax.annotation.Nonnull;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Getter;
import pl.shockah.mallard.Layouts;
import pl.shockah.mallard.Mallard;
import pl.shockah.mallard.ui.BindUtilities;
import pl.shockah.unicorn.javafx.Controller;

public class WelcomeController extends Controller {
	@Nonnull
	private final AppController appController;

	@FXML
	private TitledPane titledPane;

	@FXML
	private Hyperlink newProjectLink;

	@FXML
	private Hyperlink existingProjectLink;

	public WelcomeController(@Nonnull AppController appController) {
		this.appController = appController;
		Layouts.welcome.loadIntoController(this);
	}

	@Override
	protected void onLoaded() {
		super.onLoaded();

		titledPane.minWidthProperty().bind(EasyBind.map(Mallard.getStage().widthProperty(), width -> width.doubleValue() * 0.4));

		newProjectLink.visitedProperty().bind(new ReadOnlyBooleanWrapper(false));
		existingProjectLink.visitedProperty().bind(new ReadOnlyBooleanWrapper(false));
	}

	@FXML
	private void onNewProjectAction(ActionEvent event) {
		appController.newAction();
	}

	@FXML
	private void onExistingProjectAction(ActionEvent event) {
		appController.openAction();
	}
}