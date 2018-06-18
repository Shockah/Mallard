package pl.shockah.mallard.project;

import javax.annotation.Nonnull;

import pl.shockah.mallard.JSONSerializer;

public abstract class ProjectSerializer<T extends Project> extends JSONSerializer<T> {
	public ProjectSerializer(@Nonnull String type) {
		super(type);
	}
}