package jpx.rtracer.shapes;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import jpx.rtracer.Intersection;
import jpx.rtracer.Ray;

public class Plane implements Geometry {

	public Vector3fc axisH;
	public Vector3fc axisV;
	
	public Vector3fc normal;
	public float radius;
	
	public Plane(Vector3fc axisH, Vector3fc axisV) {
		super();
		this.axisH = axisH;
		this.axisV = axisV;
		
		this.normal = axisH.cross(axisV, new Vector3f()).normalize(); 
		this.radius = (float)Math.sqrt( axisH.lengthSquared() + axisV.lengthSquared() );
	}

	@Override
	public Intersection intersects(Vector3fc geomOrigin, Ray ray) {
		float nDotRaySegment = normal.dot(ray.segment);
		if( nDotRaySegment == 0) {
			return null;
		}
		
		Vector3f origin = new Vector3f(axisH).add(axisV).div(2).negate().add(geomOrigin); 
		
		float d = normal.dot(origin); 
		float nDotRayOrigin = normal.dot(ray.origin);
		float t = (d - nDotRayOrigin)/nDotRaySegment;
		
		if( t<0 ) {
			return null;
		}
		
		Vector3f ending = new Vector3f(origin).add(axisH).add(axisV);
		Vector3f point = new Vector3f(ray.segment).mul(t).add(ray.origin);
		
		if(        point.x < origin.x()-0.000001f
				|| point.y < origin.y()-0.000001f
				|| point.z < origin.z()-0.000001f
				|| point.x > ending.x()+0.000001f
				|| point.y > ending.y()+0.000001f
				|| point.z > ending.z()+0.000001f) {
			return null;
		}
		
		Intersection intersection = new Intersection();
		intersection.point = point;
		intersection.normal = new Vector3f(normal);
		return intersection;
	}

	@Override
	public float boundingRadius() {
		return radius;
	}

}
