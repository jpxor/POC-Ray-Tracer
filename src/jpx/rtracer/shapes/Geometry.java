package jpx.rtracer.shapes;

import org.joml.Vector3fc;

import jpx.rtracer.Intersection;
import jpx.rtracer.Ray;

public interface Geometry {

	Intersection intersects(Vector3fc geomOrigin, Ray ray);
	float boundingRadius();

}
