package pl.shockah.mallard.ui;

import javax.annotation.Nonnull;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import lombok.Getter;

public abstract class Controller {
	@Getter
	private Region view;

	protected final void setView(@Nonnull Region view) {
		this.view = view;
		view.sceneProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue == null && newValue != null)
				onAddedToScene(newValue);
			else if (oldValue != null && newValue == null)
				onRemovedFromScene(oldValue);
		});
	}

	protected void onAddedToScene(@Nonnull Scene scene) {
	}

	protected void onRemovedFromScene(@Nonnull Scene scene) {
	}
}