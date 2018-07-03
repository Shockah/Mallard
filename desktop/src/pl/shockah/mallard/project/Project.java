package pl.shockah.mallard.project;

import java.io.File;

import javax.annotation.Nonnull;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public abstract class Project {
	@Nonnull
	public final Property<File> file = new SimpleObjectProperty<>(this, "file");
}