package jpx.rtracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import jpx.rtracer.shapes.Geometry;

public class Obj {

	public Material material; 
	public Vector3f position;
	public Geometry geom;
	public String name = "";  
	public float id = (float)Math.random();

	public Obj(Material material, Vector3f position, Geometry geom) {
		super();
		this.material = material;
		this.position = position;
		this.geom = geom;
	}

	public Intersection intersects(Ray ray) { 
		Intersection intersection = geom.intersects(position, ray);
		if(intersection != null) {
			intersection.object = this;
		}
		return intersection;
	}

	public Vector3fc position() { 
		return position;
	}

	public Vector3fc id() {
		return new Vector3f(id);
	}
	
}
