package jpx.rtracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Ray {
	
	public Vector3f origin;
	public Vector3f segment;
	public int depth = 0;
	
	public Ray(Vector3fc start, Vector3fc end) {
		this.origin = new Vector3f(start);
		this.segment = end.sub(start, new Vector3f());
	}

	public Ray() {}

	public void addBias(float bias) { 
		origin.add( new Vector3f(segment).normalize().mul(bias) );
	}

	public void addBias(Vector3f dir, float bias) { 
		origin.add( new Vector3f(dir).normalize().mul(bias) );
	}
}
