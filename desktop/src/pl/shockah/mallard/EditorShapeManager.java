package pl.shockah.mallard;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Shape;
import pl.shockah.mallard.project.SpriteProject;
import pl.shockah.mallard.ui.controller.sprite.editor.ShapeEditor;
import pl.shockah.unicorn.func.Func2;

public class EditorShapeManager extends ShapeManager {
	@Override
	public <S extends Shape.Filled, Serializer extends JSONSerializer<S>> void register(@Nonnull String name, @Nonnull Class<S> clazz, @Nonnull Serializer serializer) {
		throw new UnsupportedOperationException();
	}

	public <S extends Shape.Filled, Serializer extends JSONSerializer<S>> void register(@Nonnull String name, @Nonnull Class<S> clazz, @Nonnull Serializer serializer, @Nonnull Func2<SpriteProject.Frame, SpriteProject.Frame.ShapeEntry<S>, ShapeEditor<S>> editorFactory) {
		Entry<S> entry = new Entry<>(name, clazz, editorFactory);
		typeToEntryMap.put(serializer.type, entry);
		types.add(entry);
		jsonSerializationManager.register(clazz, serializer);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends Shape.Filled> Entry<S> getEntry(@Nonnull String name) {
		return (Entry<S>)typeToEntryMap.get(name);
	}

	public class Entry<S extends Shape.Filled> extends ShapeManager.Entry<S> {
		@Nonnull
		public final Func2<SpriteProject.Frame, SpriteProject.Frame.ShapeEntry<S>, ShapeEditor<S>> editorFactory;

		public Entry(@Nonnull String name, @Nonnull Class<S> clazz, @Nonnull Func2<SpriteProject.Frame, SpriteProject.Frame.ShapeEntry<S>, ShapeEditor<S>> editorFactory) {
			super(name, clazz);
			this.editorFactory = editorFactory;
		}
	}
}