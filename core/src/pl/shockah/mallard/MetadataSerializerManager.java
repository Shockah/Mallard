package pl.shockah.mallard;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.jay.JSONList;
import pl.shockah.jay.JSONObject;

public class MetadataSerializerManager {
	@Nonnull
	private final Map<String, MetadataSerializer<?, ?>> keyToSerializerMap = new HashMap<>();

	@Nonnull
	private final Map<Class<?>, MetadataSerializer<?, ?>> classToSerializerMap = new HashMap<>();

	public <T> void register(@Nonnull Class<T> clazz, @Nonnull MetadataSerializer<T, ?> serializer) {
		keyToSerializerMap.put(serializer.key, serializer);
		classToSerializerMap.put(clazz, serializer);
	}

	public void unregister(@Nonnull Class<?> clazz) {
		MetadataSerializer<?, ?> serializer = classToSerializerMap.get(clazz);
		if (serializer == null)
			return;
		keyToSerializerMap.remove(serializer.key);
		classToSerializerMap.remove(clazz);
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public JSONObject serialize(@Nonnull Object object) {
		MetadataSerializer<?, ?> serializer = classToSerializerMap.get(object.getClass());
		if (serializer == null)
			throw new UnsupportedOperationException(String.format("No registered serializer for class %s.", object.getClass()));

		MetadataSerializer<Object, Object> rawSerializer = (MetadataSerializer<Object, Object>)serializer;
		JSONObject json = new JSONObject();
		json.put("serializer", rawSerializer.key);
		json.put("value", rawSerializer.serialize(object));
		return json;
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public Object deserialize(@Nonnull JSONObject json) {
		String key = json.getString("serializer");
		MetadataSerializer<?, ?> serializer = keyToSerializerMap.get(key);
		if (serializer == null)
			throw new UnsupportedOperationException(String.format("No registered serializer for key %s.", key));

		MetadataSerializer<Object, Object> rawSerializer = (MetadataSerializer<Object, Object>)serializer;
		return rawSerializer.deserialize(json.get("value"));
	}

	public final void registerDefaultSerializers() {
		register(Integer.class, new MetadataSerializer<Integer, Number>("int") {
			@Nonnull
			@Override
			public Number serialize(@Nonnull Integer value) {
				return value;
			}

			@Nonnull
			@Override
			public Integer deserialize(@Nonnull Number input) {
				return input.intValue();
			}
		});

		register(Float.class, new MetadataSerializer<Float, Number>("float") {
			@Nonnull
			@Override
			public Number serialize(@Nonnull Float value) {
				return value;
			}

			@Nonnull
			@Override
			public Float deserialize(@Nonnull Number input) {
				return input.floatValue();
			}
		});

		register(Rectangle.class, new MetadataSerializer<Rectangle, JSONList<Integer>>("float") {
			@Nonnull
			@Override
			public JSONList<Integer> serialize(@Nonnull Rectangle value) {
				JSONList<Integer> result = new JSONList<>();
				result.add((int)value.position.x);
				result.add((int)value.position.y);
				result.add((int)value.size.x);
				result.add((int)value.size.y);
				return result;
			}

			@Nonnull
			@Override
			public Rectangle deserialize(@Nonnull JSONList<Integer> input) {
				if (input.size() != 4)
					throw new IllegalArgumentException();
				return new Rectangle(input.get(0), input.get(1), input.get(2), input.get(3));
			}
		});
	}
}