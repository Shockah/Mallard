package pl.shockah.mallard.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.unicorn.collection.MutableBooleanArray2D;

public class SpriteProjectAtlasPacker {
	@Nonnull
	public AtlasData pack(@Nonnull SpriteProject project, int gap) {
		Map<SpriteProject.Frame, Double> sortValues = new HashMap<>();
		for (SpriteProject.Frame frame : project.frames) {
			double dw = frame.image.getValue().getWidth();
			double dh = frame.image.getValue().getHeight();
			double value = Math.max(dw, dh) / Math.min(dw, dh) * dw * dh;
			sortValues.put(frame, value);
		}

		List<SpriteProject.Frame> sorted = new ArrayList<>(project.frames);
		sorted.sort(Comparator.comparingDouble(sortValues::get).reversed());

		int width = 64;
		int height = 64;
		while (true) {
			try {
				Map<SpriteProject.Frame, Rectangle> atlas = pack(sorted, width, height, gap);
				int atlasWidth = atlas.values().stream()
						.mapToInt(rectangle -> (int)(rectangle.position.x + rectangle.size.x))
						.max().orElse(0) + gap;
				int atlasHeight = atlas.values().stream()
						.mapToInt(rectangle -> (int)(rectangle.position.y + rectangle.size.y))
						.max().orElse(0) + gap;
				return new AtlasData(project, atlas, atlasWidth, atlasHeight);
			} catch (CannotFitException e) {
				if (width == height)
					height *= 2;
				else
					width *= 2;
			}
		}
	}

	@Nonnull
	private Map<SpriteProject.Frame, Rectangle> pack(@Nonnull List<SpriteProject.Frame> sortedFrames, int width, int height, int gap) throws CannotFitException {
		Map<SpriteProject.Frame, Rectangle> atlas = new LinkedHashMap<>();
		MutableBooleanArray2D taken = new MutableBooleanArray2D(width, height);

		Outer:
		for (SpriteProject.Frame frame : sortedFrames) {
			int w = (int)frame.image.getValue().getWidth();
			int h = (int)frame.image.getValue().getHeight();

			for (int y = gap; y <= height - h - gap; y++) {
				OuterX:
				for (int x = gap; x <= width - w - gap; x++) {
					for (int y2 = 0; y2 < h + gap; y2++) {
						for (int x2 = 0; x2 < w + gap; x2++) {
							if (taken.get(x + x2, y + y2))
								continue OuterX;
						}
					}

					atlas.put(frame, new Rectangle(x, y, w, h));
					for (int y2 = 0; y2 < h + gap; y2++) {
						for (int x2 = 0; x2 < w + gap; x2++) {
							taken.set(x + x2, y + y2, true);
						}
					}

					continue Outer;
				}
			}

			throw new CannotFitException();
		}

		return atlas;
	}

	private static class CannotFitException extends Exception {
	}

	public static class AtlasData {
		@Nonnull
		public final SpriteProject project;

		@Nonnull
		public final Map<SpriteProject.Frame, Rectangle> atlas;

		public final int width;

		public final int height;

		public AtlasData(@Nonnull SpriteProject project, @Nonnull Map<SpriteProject.Frame, Rectangle> atlas, int width, int height) {
			this.project = project;
			this.atlas = Collections.unmodifiableMap(new HashMap<>(atlas));
			this.width = width;
			this.height = height;
		}
	}
}