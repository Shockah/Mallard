package pl.shockah.mallard.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.jay.JSONList;
import pl.shockah.jay.JSONObject;
import pl.shockah.mallard.JSONSerializationManager;
import pl.shockah.unicorn.UnexpectedException;

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
		try {
			JSONObject json = new JSONObject();
			json.put("version", VERSION);

			JSONList<JSONObject> jSubsprites = (JSONList<JSONObject>) json.putNewList("frames");
			for (SpriteProject.Frame frame : project.frames) {
				JSONObject jSubsprite = jSubsprites.addNewObject();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(SwingFXUtils.fromFXImage(frame.image, null), "png", baos);
				jSubsprite.put("data", Base64.getEncoder().encodeToString(baos.toByteArray()));

				if (!frame.origin.equals(Vec2.zero)) {
					jSubsprite.put("origin", JSONObject.of(
							"x", frame.origin.x,
							"y", frame.origin.y
					));
				}

				if (!frame.shapes.isEmpty()) {
					JSONObject jSubspriteShapes = jSubsprite.putNewObject("shapes");
					for (Map.Entry<String, Shape.Filled> shapeEntry : frame.shapes.entrySet()) {
						jSubspriteShapes.put(shapeEntry.getKey(), shapeSerializationManager.serialize(shapeEntry.getValue()));
					}
				}
			}

			if (!project.animations.isEmpty()) {
				JSONObject jAnimations = json.putNewObject("animations");
				for (SpriteProject.Animation.Entry animationEntry : project.animations) {
					SpriteProject.Animation animation = animationEntry.animation;
					JSONObject jAnimation = jAnimations.putNewObject(animationEntry.name);

					JSONList<JSONObject> jAnimationFrames = (JSONList<JSONObject>) jAnimation.putNewList("frames");
					for (SpriteProject.Animation.Frame frame : animation.frames) {
						JSONObject jAnimationFrame = jAnimationFrames.addNewObject();
						jAnimationFrame.put("index", project.frames.indexOf(frame.frame));
						if (frame.relativeDuration != 1f)
							jAnimationFrame.put("relativeDuration", frame.relativeDuration);
					}

					jAnimation.put("duration", animation.duration);
				}
			}

			return json;
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	@Nonnull
	@Override
	public SpriteProject deserialize(@Nonnull JSONObject json) {
		try {
			int version = json.getInt("version");
			if (version > VERSION)
				throw new IllegalArgumentException("Cannot handle a newer version of a project.");

			SpriteProject project = new SpriteProject();

			for (JSONObject jSubsprite : json.getList("frames").ofObjects()) {
				byte[] data = Base64.getDecoder().decode(jSubsprite.getString("data"));
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				SpriteProject.Frame frame = new SpriteProject.Frame(SwingFXUtils.toFXImage(ImageIO.read(bais), null));

				if (jSubsprite.containsKey("origin")) {
					JSONObject jSubspriteOrigin = jSubsprite.getObject("origin");
					frame.origin = new Vec2(
							jSubspriteOrigin.getFloat("x"),
							jSubspriteOrigin.getFloat("y")
					);
				}

				if (jSubsprite.containsKey("shapes")) {
					for (Map.Entry<String, Object> jSubspriteShapeEntry : jSubsprite.getObject("shapes").entrySet()) {
						JSONObject jSubspriteShape = (JSONObject) jSubspriteShapeEntry.getValue();
						frame.shapes.put(jSubspriteShapeEntry.getKey(), shapeSerializationManager.deserialize(jSubspriteShape));
					}
				}

				project.frames.add(frame);
			}

			if (json.containsKey("animations")) {
				for (Map.Entry<String, Object> jAnimationEntry : json.getObject("animations").entrySet()) {
					JSONObject jAnimation = (JSONObject) jAnimationEntry.getValue();
					SpriteProject.Animation animation = new SpriteProject.Animation();

					for (JSONObject jAnimationFrame : jAnimation.getList("frames").ofObjects()) {
						SpriteProject.Frame frame = project.frames.get(jAnimationFrame.getInt("index"));
						SpriteProject.Animation.Frame animationFrame = new SpriteProject.Animation.Frame(frame);

						if (jAnimationFrame.containsKey("relativeDuration"))
							animationFrame.relativeDuration = jAnimationFrame.getFloat("relativeDuration");

						animation.frames.add(animationFrame);
					}

					animation.duration = jAnimation.getFloat("duration");

					project.animations.add(new SpriteProject.Animation.Entry(jAnimationEntry.getKey(), animation));
				}
			}

			return project;
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}
}