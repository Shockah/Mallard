package pl.shockah.mallard.project;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.util.Base64;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.godwit.MemoryFileHandle;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.jay.JSONList;
import pl.shockah.jay.JSONObject;
import pl.shockah.mallard.JSONSerializationManager;

public class SpriteProjectSerializer extends ProjectSerializer<SpriteProject> {
	private static final int VERSION = 1;

	@Nonnull
	protected final JSONSerializationManager<Shape.Filled> shapeSerializationManager;

	public SpriteProjectSerializer(@Nonnull JSONSerializationManager<Shape.Filled> shapeSerializationManager) {
		super("sprite");
		this.shapeSerializationManager = shapeSerializationManager;
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public JSONObject serialize(@Nonnull SpriteProject project) {
		JSONObject json = new JSONObject();
		json.put("version", VERSION);

		JSONList<JSONObject> jSubsprites = (JSONList<JSONObject>)json.putNewList("subsprites");
		for (SpriteProject.Subsprite subsprite : project.subsprites) {
			JSONObject jSubsprite = jSubsprites.addNewObject();

			MemoryFileHandle memory = new MemoryFileHandle();
			PixmapIO.writePNG(memory, subsprite.pixmap);
			jSubsprite.put("data", Base64.getEncoder().encodeToString(memory.getBytes()));

			if (!subsprite.origin.equals(Vec2.zero)) {
				jSubsprite.put("origin", JSONObject.of(
						"x", subsprite.origin.x,
						"y", subsprite.origin.y
				));
			}

			if (!subsprite.shapes.isEmpty()) {
				JSONObject jSubspriteShapes = jSubsprite.putNewObject("shapes");
				for (Map.Entry<String, Shape.Filled> shapeEntry : subsprite.shapes.entrySet()) {
					jSubspriteShapes.put(shapeEntry.getKey(), shapeSerializationManager.serialize(shapeEntry.getValue()));
				}
			}
		}

		if (!project.animations.isEmpty()) {
			JSONObject jAnimations = json.putNewObject("animations");
			for (Map.Entry<String, SpriteProject.Animation> animationEntry : project.animations.entrySet()) {
				SpriteProject.Animation animation = animationEntry.getValue();
				JSONObject jAnimation = jAnimations.putNewObject(animationEntry.getKey());

				JSONList<JSONObject> jAnimationFrames = (JSONList<JSONObject>)jAnimation.putNewList("frames");
				for (SpriteProject.Animation.Frame frame : animation.frames) {
					JSONObject jAnimationFrame = jAnimationFrames.addNewObject();
					jAnimationFrame.put("index", project.subsprites.indexOf(frame.subsprite));
					if (frame.relativeDuration != 1f)
						jAnimationFrame.put("relativeDuration", frame.relativeDuration);
				}

				jAnimation.put("duration", animation.duration);
			}
		}

		return json;
	}

	@Nonnull
	@Override
	public SpriteProject deserialize(@Nonnull JSONObject json) {
		int version = json.getInt("version");
		if (version > VERSION)
			throw new IllegalArgumentException("Cannot handle a newer version of a project.");

		SpriteProject project = new SpriteProject();

		for (JSONObject jSubsprite : json.getList("subsprites").ofObjects()) {
			byte[] data = Base64.getDecoder().decode(jSubsprite.getString("data"));
			SpriteProject.Subsprite subsprite = new SpriteProject.Subsprite(new Pixmap(data, 0, data.length));

			if (jSubsprite.containsKey("origin")) {
				JSONObject jSubspriteOrigin = jSubsprite.getObject("origin");
				subsprite.origin = new Vec2(
						jSubspriteOrigin.getFloat("x"),
						jSubspriteOrigin.getFloat("y")
				);
			}

			if (jSubsprite.containsKey("shapes")) {
				for (Map.Entry<String, Object> jSubspriteShapeEntry : jSubsprite.getObject("shapes").entrySet()) {
					JSONObject jSubspriteShape = (JSONObject)jSubspriteShapeEntry.getValue();
					subsprite.shapes.put(jSubspriteShapeEntry.getKey(), shapeSerializationManager.deserialize(jSubspriteShape));
				}
			}

			project.subsprites.add(subsprite);
		}

		if (json.containsKey("animations")) {
			for (Map.Entry<String, Object> jAnimationEntry : json.getObject("animations").entrySet()) {
				JSONObject jAnimation = (JSONObject)jAnimationEntry.getValue();
				SpriteProject.Animation animation = new SpriteProject.Animation();

				for (JSONObject jAnimationFrame : jAnimation.getList("frames").ofObjects()) {
					SpriteProject.Subsprite subsprite = project.subsprites.get(jAnimationFrame.getInt("index"));
					SpriteProject.Animation.Frame animationFrame = new SpriteProject.Animation.Frame(subsprite);

					if (jAnimationFrame.containsKey("relativeDuration"))
						animationFrame.relativeDuration = jAnimationFrame.getFloat("relativeDuration");

					animation.frames.add(animationFrame);
				}

				animation.duration = jAnimation.getFloat("duration");

				project.animations.put(jAnimationEntry.getKey(), animation);
			}
		}

		return project;
	}
}