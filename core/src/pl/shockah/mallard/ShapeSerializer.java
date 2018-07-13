package pl.shockah.mallard;

import javax.annotation.Nonnull;

import pl.shockah.godwit.geom.Circle;
import pl.shockah.godwit.geom.IVec2;
import pl.shockah.godwit.geom.Rectangle;
import pl.shockah.godwit.geom.Shape;
import pl.shockah.godwit.geom.Vec2;
import pl.shockah.godwit.geom.polygon.Polygon;
import pl.shockah.jay.JSONList;
import pl.shockah.jay.JSONObject;

public abstract class ShapeSerializer<S extends Shape.Filled> extends JSONSerializer<S> {
	public ShapeSerializer(@Nonnull String type) {
		super(type);
	}

	public static class RectangleSerializer extends ShapeSerializer<Rectangle> {
		public RectangleSerializer() {
			super("rectangle");
		}

		@Nonnull
		@Override
		public JSONObject serialize(@Nonnull Rectangle object) {
			return JSONObject.of(
					"x", object.position.x,
					"y", object.position.y,
					"w", object.size.x,
					"h", object.size.y
			);
		}

		@Nonnull
		@Override
		public Rectangle deserialize(@Nonnull JSONObject json) {
			return new Rectangle(
					json.getFloat("x"),
					json.getFloat("y"),
					json.getFloat("w"),
					json.getFloat("h")
			);
		}
	}

	public static class CircleSerializer extends ShapeSerializer<Circle> {
		public CircleSerializer() {
			super("circle");
		}

		@Nonnull
		@Override
		public JSONObject serialize(@Nonnull Circle object) {
			return JSONObject.of(
					"x", object.position.x,
					"y", object.position.y,
					"r", object.radius
			);
		}

		@Nonnull
		@Override
		public Circle deserialize(@Nonnull JSONObject json) {
			return new Circle(
					json.getFloat("x"),
					json.getFloat("y"),
					json.getFloat("r")
			);
		}
	}

	public static class PolygonSerializer extends ShapeSerializer<Polygon> {
		public PolygonSerializer() {
			super("polygon");
		}

		@Nonnull
		@Override
		@SuppressWarnings("unchecked")
		public JSONObject serialize(@Nonnull Polygon object) {
			JSONObject json = new JSONObject();

			JSONList<JSONObject> jPoints = (JSONList<JSONObject>)json.putNewList("points");
			for (int i = 0; i < object.getPointCount(); i++) {
				IVec2 point = object.get(i);
				jPoints.add(JSONObject.of(
						"x", point.x(),
						"y", point.y()
				));
			}

			return json;
		}

		@Nonnull
		@Override
		public Polygon deserialize(@Nonnull JSONObject json) {
			Polygon polygon = new Polygon();
			polygon.closed = true;

			JSONList<JSONObject> jPoints = json.getList("points").ofObjects();
			for (JSONObject jPoint : jPoints) {
				polygon.addPoint(new Vec2(
						jPoint.getFloat("x"),
						jPoint.getFloat("y")
				));
			}

			return polygon;
		}
	}
}