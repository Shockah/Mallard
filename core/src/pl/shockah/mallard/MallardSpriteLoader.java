package pl.shockah.mallard;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import pl.shockah.godwit.Godwit;
import pl.shockah.godwit.geom.Circle;
import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.geom.polygon.Polygon;
import pl.shockah.jay.JSONObject;
import pl.shockah.unicorn.collection.Box;

public class MallardSpriteLoader extends AsynchronousAssetLoader<MallardSprite, MallardSpriteLoader.MallardSpriteParameters> {
	@Nonnull
	private final ShapeManager shapeManager = new ShapeManager();

	@Nonnull
	private final Map<FileHandle, MallardSprite> asyncLoaded = new HashMap<>();

	public MallardSpriteLoader(FileHandleResolver resolver) {
		super(resolver);

		shapeManager.register("Rectangle", Rectangle.class, new ShapeSerializer.RectangleSerializer());
		shapeManager.register("Circle", Circle.class, new ShapeSerializer.CircleSerializer());
		shapeManager.register("Polygon", Polygon.class, new ShapeSerializer.PolygonSerializer());
	}

	public static void register() {
		AssetManager assetManager = Godwit.getInstance().getAssetManager();
		assetManager.setLoader(MallardSprite.class, new MallardSpriteLoader(assetManager.getFileHandleResolver()));
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MallardSpriteParameters parameter) {
		ArchiveFileHandleResolver archiveResolver = new ArchiveFileHandleResolver(file);
		Array<AssetDescriptor> dependencies = new Array<>();
		dependencies.add(new AssetDescriptor<>(archiveResolver.resolve("texture.png"), Texture.class));
		dependencies.add(new AssetDescriptor<>(archiveResolver.resolve("data.json"), JSONObject.class));
		return dependencies;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, MallardSpriteParameters parameter) {
		ArchiveFileHandleResolver archiveResolver = new ArchiveFileHandleResolver(file);
		Texture texture = manager.get(new AssetDescriptor<>(archiveResolver.resolve("texture.png"), Texture.class));
		JSONObject json = manager.get(new AssetDescriptor<>(archiveResolver.resolve("data.json"), JSONObject.class));
		asyncLoaded.put(file, deserialize(texture, json));
	}

	@Nonnull
	private MallardSprite deserialize(@Nonnull Texture texture, @Nonnull JSONObject json) {
		int version = json.getInt("version");
		if (version > MallardSprite.VERSION)
			throw new IllegalArgumentException("Cannot handle a newer version of a project.");

		List<MallardSprite.Subsprite> subsprites = new ArrayList<>();
		Map<String, MallardSprite.Animation> animations = new LinkedHashMap<>();

		for (JSONObject jSubsprite : json.getList("frames").ofObjects()) {
			JSONObject jRegion = jSubsprite.getObject("region");
			TextureRegion region = new TextureRegion(
					texture,
					jRegion.getInt("x"), jRegion.getInt("y"),
					jRegion.getInt("w"), jRegion.getInt("h")
			);

			Box<Vec2> origin = new Box<>(Vec2.zero);
			jSubsprite.onObject("origin", jOrigin -> {
				origin.value = new Vec2(jOrigin.getFloat("x"), jOrigin.getFloat("y"));
			});

			Map<String, Shape.Filled> shapes = new LinkedHashMap<>();

			if (jSubsprite.containsKey("shapes")) {
				for (Map.Entry<String, Object> jSubspriteShapeEntry : jSubsprite.getObject("shapes").entrySet()) {
					JSONObject jSubspriteShape = (JSONObject) jSubspriteShapeEntry.getValue();
					String type = jSubspriteShape.getString("type");
					String name = jSubspriteShapeEntry.getKey();

					Shape.Filled shape = shapeManager.jsonSerializationManager.deserialize(jSubspriteShape);
					shapes.put(name, shape);
				}
			}

			subsprites.add(new MallardSprite.Subsprite(region, origin.value, shapes));
		}

		if (json.containsKey("animations")) {
			for (Map.Entry<String, Object> jAnimationEntry : json.getObject("animations").entrySet()) {
				JSONObject jAnimation = (JSONObject) jAnimationEntry.getValue();

				List<MallardSprite.Animation.Frame> frames = new ArrayList<>();

				for (JSONObject jAnimationFrame : jAnimation.getList("frames").ofObjects()) {
					MallardSprite.Subsprite subsprite = subsprites.get(jAnimationFrame.getInt("index"));
					Box<Float> relativeDuration = new Box<>(1f);
					Box<Vec2> offset = new Box<>(Vec2.zero);

					jAnimationFrame.onFloat("relativeDuration", f -> {
						relativeDuration.value = f;
					});

					jAnimationFrame.onObject("offset", jFrameOffset -> {
						offset.value = new Vec2(
								jFrameOffset.getFloat("x"),
								jFrameOffset.getFloat("y")
						);
					});

					frames.add(new MallardSprite.Animation.Frame(subsprite, relativeDuration.value, offset.value));
				}

				float duration = jAnimation.getFloat("duration");
				animations.put(jAnimationEntry.getKey(), new MallardSprite.Animation(frames.toArray(new MallardSprite.Animation.Frame[frames.size()]), duration));
			}
		}

		return new MallardSprite(texture, subsprites.toArray(new MallardSprite.Subsprite[subsprites.size()]), animations);
	}

	@Override
	public MallardSprite loadSync(AssetManager manager, String fileName, FileHandle file, MallardSpriteParameters parameter) {
		MallardSprite result = asyncLoaded.get(file);
		asyncLoaded.remove(file);
		return result;
	}

	public static class MallardSpriteParameters extends AssetLoaderParameters<MallardSprite> {
	}
}