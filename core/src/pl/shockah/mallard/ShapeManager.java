package pl.shockah.mallard;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.shockah.godwit.geom.Shape;

public class ShapeManager {
	@Nonnull
	protected final Map<String, Entry<? extends Shape.Filled>> typeToEntryMap = new HashMap<>();

	@Nonnull
	public final ObservableList<Entry<? extends Shape.Filled>> types = FXCollections.observableArrayList();

	@Nonnull
	public final JSONSerializationManager<Shape.Filled> jsonSerializationManager = new JSONSerializationManager<>();

	public <S extends Shape.Filled, Serializer extends JSONSerializer<S>> void register(@Nonnull String name, @Nonnull Class<S> clazz, @Nonnull Serializer serializer) {
		Entry<S> entry = new Entry<>(name, clazz);
		typeToEntryMap.put(serializer.type, entry);
		types.add(entry);
		jsonSerializationManager.register(clazz, serializer);
	}

	@SuppressWarnings("unchecked")
	public <S extends Shape.Filled> Entry<S> getEntry(@Nonnull String name) {
		return (Entry<S>)typeToEntryMap.get(name);
	}

	public class Entry<S extends Shape.Filled> {
		@Nonnull
		public final String name;

		@Nonnull
		public final Class<S> clazz;

		public Entry(@Nonnull String name, @Nonnull Class<S> clazz) {
			this.name = name;
			this.clazz = clazz;
		}
	}
}