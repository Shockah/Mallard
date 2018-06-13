package pl.shockah.mallard.project;

import javax.annotation.Nonnull;

import pl.shockah.jay.JSONObject;

public abstract class ProjectSerializer<T extends Project> {
	@Nonnull
	public final String type;

	public ProjectSerializer(@Nonnull String type) {
		this.type = type;
	}

	@Nonnull
	public abstract JSONObject serialize(@Nonnull T project);

	@Nonnull
	public abstract T deserialize(@Nonnull JSONObject json);
}