package pl.shockah.mallard;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.jay.JSONObject;

public class JSONSerializationManager<Type> {
	@Nonnull
	public final Map<Class<? extends Type>, JSONSerializer<? extends Type>> classToSerializerMap = new HashMap<>();

	@Nonnull
	public final Map<String, JSONSerializer<? extends Type>> typeToSerializerMap = new HashMap<>();

	public <ArgType extends Type, ArgSerializer extends JSONSerializer<ArgType>> void register(@Nonnull Class<ArgType> clazz, @Nonnull ArgSerializer serializer) {
		classToSerializerMap.put(clazz, serializer);
		typeToSerializerMap.put(serializer.type, serializer);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <ArgType extends Type> JSONObject serialize(@Nonnull ArgType object) {
		JSONSerializer<? extends Type> serializer = classToSerializerMap.get(object.getClass());
		if (serializer == null)
			throw new IllegalArgumentException(String.format("No registered serializer for %s.", object.getClass()));

		JSONSerializer<ArgType> typedSerializer = (JSONSerializer<ArgType>)serializer;
		return JSONObject.of(
				"type", serializer.type,
				"data", typedSerializer.serialize(object)
		);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <ArgType extends Type> ArgType deserialize(@Nonnull JSONObject json) {
		String type = json.getString("type");
		JSONSerializer<? extends Type> serializer = typeToSerializerMap.get(type);
		if (serializer == null)
			throw new IllegalArgumentException(String.format("No registered serializer for %s.", type));

		JSONSerializer<ArgType> typedSerializer = (JSONSerializer<ArgType>)serializer;
		return typedSerializer.deserialize(json.getObject("data"));
	}
}