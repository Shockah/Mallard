package pl.shockah.mallard.ui;

import javafx.scene.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class Controller {
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Node view;
}