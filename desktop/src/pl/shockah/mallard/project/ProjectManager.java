package pl.shockah.mallard.project;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.jay.JSONObject;

public class ProjectManager {
	@Nonnull
	public final Map<Class<? extends Project>, ProjectSerializer<? extends Project>> classToSerializerMap = new HashMap<>();

	@Nonnull
	public final Map<String, ProjectSerializer<? extends Project>> typeToSerializerMap = new HashMap<>();

	public <T extends Project> void register(@Nonnull Class<T> clazz, @Nonnull ProjectSerializer<T> serializer) {
		classToSerializerMap.put(clazz, serializer);
		typeToSerializerMap.put(serializer.type, serializer);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T extends Project> JSONObject serialize(@Nonnull T project) {
		ProjectSerializer<? extends Project> serializer = classToSerializerMap.get(project.getClass());
		if (serializer == null)
			throw new IllegalArgumentException(String.format("No registered serializer for %s.", project.getClass()));

		ProjectSerializer<T> typedSerializer = (ProjectSerializer<T>)serializer;
		return JSONObject.of(
				"type", serializer.type,
				"data", typedSerializer.serialize(project)
		);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T extends Project> T deserialize(@Nonnull JSONObject json) {
		String type = json.getString("type");
		ProjectSerializer<? extends Project> serializer = typeToSerializerMap.get(type);
		if (serializer == null)
			throw new IllegalArgumentException(String.format("No registered serializer for %s.", type));

		ProjectSerializer<T> typedSerializer = (ProjectSerializer<T>)serializer;
		return typedSerializer.deserialize(json.getObject("data"));
	}
}