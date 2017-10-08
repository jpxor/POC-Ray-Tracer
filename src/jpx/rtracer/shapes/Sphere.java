package jpx.rtracer.shapes;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import jpx.rtracer.Intersection;
import jpx.rtracer.Ray;

public class Sphere implements Geometry {

	public float radius;
	public float radiusSqr;
	
	public Sphere(float radius) {
		super();
		this.radius = radius;
		this.radiusSqr = radius*radius;
	}

	@Override
	public Intersection intersects(Vector3fc origin, Ray ray) {
		float t0;
		float t1;
		
		Vector3f L = new Vector3f(ray.origin).sub(origin); 
        float a = ray.segment.dot(ray.segment);
        float b = 2 * ray.segment.dot(L); 
        float c = L.dot(L) - radiusSqr; 
        
        
        float discr = b * b - 4 * a * c; 
        if (discr < 0) return null; 
        
        if (discr == 0) {
        	t0 = t1 = - 0.5f * b / a; 
        }
        else { 
            float q = (b > 0) ? 
                -0.5f * (b + (float)Math.sqrt(discr)) : 
                -0.5f * (b - (float)Math.sqrt(discr)); 
            t0 = q / a; 
            t1 = c / q; 
        } 

        if (t0 > t1) {
        	float tmp = t0;
        	t0 = t1;
        	t1 = tmp;
        }
 
        if (t0 < 0) { 
            t0 = t1; // if t0 is negative, let's use t1 instead 
            if (t0 < 0) {
            	// both t0 and t1 are negative 
            	return null; 
            }
        } 
 
        float t = t0; 
        Vector3f point = new Vector3f(ray.segment).mul(t).add(ray.origin);
        Vector3f normal = new Vector3f( point ).sub(origin).normalize();
		
		Intersection intersection = new Intersection();
		intersection.point = point;
		intersection.normal = normal;
		return intersection;
	}

	@Override
	public float boundingRadius() {
		return radius;
	}
	
	

}





