package pl.shockah.mallard;

import javax.annotation.Nonnull;

import lombok.experimental.UtilityClass;
import pl.shockah.mallard.ui.controller.AppController;
import pl.shockah.mallard.ui.controller.WelcomeController;
import pl.shockah.unicorn.javafx.Layout;
import pl.shockah.unicorn.javafx.LayoutManager;

@UtilityClass
public final class Layouts {
	@Nonnull
	private static final LayoutManager manager = new LayoutManager.Classpath("layouts/%s.fxml");

	@Nonnull
	public static final Layout<AppController> app = manager.getLayout("app");

	@Nonnull
	public static final Layout<WelcomeController> welcome = manager.getLayout("welcome");
}