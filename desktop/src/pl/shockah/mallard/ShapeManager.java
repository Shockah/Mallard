package pl.shockah.mallard;

import java.util.function.Function;

import javax.annotation.Nonnull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.mallard.ui.controller.sprite.ShapeEditorController;

public class ShapeManager {
	@Nonnull
	public final ObservableList<Entry<? extends Shape.Filled>> types = FXCollections.observableArrayList();

	@Nonnull
	public final JSONSerializationManager<Shape.Filled> jsonSerializationManager = new JSONSerializationManager<>();

	public <S extends Shape.Filled, Serializer extends JSONSerializer<S>> void register(@Nonnull String name, @Nonnull Class<S> clazz, @Nonnull Serializer serializer, @Nonnull Function<S, ShapeEditorController<S>> controllerFactory) {
		types.add(new Entry<>(name, clazz, controllerFactory));
		jsonSerializationManager.register(clazz, serializer);
	}

	public class Entry<S extends Shape.Filled> {
		@Nonnull
		public final String name;

		@Nonnull
		public final Class<S> clazz;

		@Nonnull
		public final Function<S, ShapeEditorController<S>> controllerFactory;

		public Entry(@Nonnull String name, @Nonnull Class<S> clazz, @Nonnull Function<S, ShapeEditorController<S>> controllerFactory) {
			this.name = name;
			this.clazz = clazz;
			this.controllerFactory = controllerFactory;
		}
	}
}