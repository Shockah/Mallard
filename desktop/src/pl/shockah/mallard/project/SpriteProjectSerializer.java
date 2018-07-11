package pl.shockah.mallard.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.jay.JSONList;
import pl.shockah.jay.JSONObject;
import pl.shockah.mallard.ShapeManager;
import pl.shockah.unicorn.UnexpectedException;

public class SpriteProjectSerializer extends ProjectSerializer<SpriteProject> {
	private static final int VERSION = 1;

	@Nonnull
	protected final ShapeManager shapeManager;

	public SpriteProjectSerializer(@Nonnull ShapeManager shapeManager) {
		super("sprite");
		this.shapeManager = shapeManager;
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
				ImageIO.write(SwingFXUtils.fromFXImage(frame.image.getValue(), null), "png", baos);
				jSubsprite.put("data", Base64.getEncoder().encodeToString(baos.toByteArray()));

				if (!frame.origin.equals(Vec2.zero)) {
					jSubsprite.put("origin", JSONObject.of(
							"x", frame.origin.getValue().x,
							"y", frame.origin.getValue().y
					));
				}

				if (!frame.shapes.isEmpty()) {
					JSONObject jSubspriteShapes = jSubsprite.putNewObject("shapes");
					for (SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry : frame.shapes) {
						JSONObject jShapeEntry = shapeManager.jsonSerializationManager.serialize(shapeEntry.shape.getValue());
						if (!shapeEntry.visible.get())
							jShapeEntry.put("visible", false);
						jShapeEntry.put("color", JSONObject.of(
								"r", shapeEntry.color.getValue().getRed(),
								"g", shapeEntry.color.getValue().getGreen(),
								"b", shapeEntry.color.getValue().getBlue()
						));
						jSubspriteShapes.put(shapeEntry.name, jShapeEntry);
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

						if (frame.relativeDuration.get() != 1f)
							jAnimationFrame.put("relativeDuration", frame.relativeDuration.get());

						if (!frame.offset.getValue().equals(Vec2.zero)) {
							jAnimationFrame.put("offset", JSONObject.of(
									"x", frame.offset.getValue().x,
									"y", frame.offset.getValue().y
							));
						}
					}

					jAnimation.put("duration", animation.duration.get());
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
					frame.origin.setValue(new Vec2(
							jSubspriteOrigin.getFloat("x"),
							jSubspriteOrigin.getFloat("y")
					));
				}

				if (jSubsprite.containsKey("shapes")) {
					for (Map.Entry<String, Object> jSubspriteShapeEntry : jSubsprite.getObject("shapes").entrySet()) {
						JSONObject jSubspriteShape = (JSONObject) jSubspriteShapeEntry.getValue();
						String type = jSubspriteShape.getString("type");
						String name = jSubspriteShapeEntry.getKey();

						SpriteProject.Frame.ShapeEntry<? extends Shape.Filled> shapeEntry = new SpriteProject.Frame.ShapeEntry<>(frame, shapeManager.getEntry(type), name, shapeManager.jsonSerializationManager.deserialize(jSubspriteShape));
						shapeEntry.visible.set(jSubspriteShape.getBool("visible", true));
						jSubspriteShape.onObject("color", jColor -> {
							Color color = new Color(jColor.getFloat("r"), jColor.getFloat("g"), jColor.getFloat("b"), 1.0);
							shapeEntry.color.setValue(color);
						});
						frame.shapes.add(shapeEntry);
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

						jAnimationFrame.onFloat("relativeDuration", animationFrame.relativeDuration::set);
						jAnimationFrame.onObject("offset", jFrameOffset -> {
							animationFrame.offset.setValue(new Vec2(
									jFrameOffset.getFloat("x"),
									jFrameOffset.getFloat("y")
							));
						});

						animation.frames.add(animationFrame);
					}

					animation.duration.set(jAnimation.getFloat("duration"));

					project.animations.add(new SpriteProject.Animation.Entry(jAnimationEntry.getKey(), animation));
				}
			}

			return project;
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}
}