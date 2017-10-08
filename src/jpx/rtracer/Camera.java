package jpx.rtracer;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Camera {
	
	Quaternionf orientation = new Quaternionf(); 
	Vector3f position = new Vector3f(0);
	Vector2f camFov = new Vector2f(90,60);
	
	Vector3f viewPlaneOrigin = new Vector3f(-0.5f, -0.5f, 0.0f);
	Vector3f viewPlaneAxisH = new Vector3f(1,0,0);
	Vector3f viewPlaneAxisV = new Vector3f(0,1,0);
	
	public Camera(Vector3fc camPos, Vector2fc camFov) { 		
		this.position.set(camPos);
		this.camFov.set(camFov).mul((float)Math.PI/180f); 
	}
	


	public Ray ray(float x, float y) {
		Ray ray = new Ray();
		
//		viewPlaneOrigin.set(0,0,0).sub(viewPlaneAxisH).sub(viewPlaneAxisV).mul(0.5f);
//		viewPlaneAxisH.set(1,0,0).rotate(orientation);
//		viewPlaneAxisV.set(0,1,0).rotate(orientation);
		
		Vector3f offset = new Vector3f( viewPlaneAxisH ).mul(x).add(new Vector3f( viewPlaneAxisV ).mul(y)); 
		Vector3f pixelPoint = new Vector3f( viewPlaneOrigin ).add( offset );
		
		float d = 0.5f / (float)(Math.tan(0.5*camFov.x()));
		Vector3f toOrigin = new Vector3f(0,0,-1).rotate(orientation).mul(d);
		
		ray.origin = new Vector3f(position).add(toOrigin);
		ray.segment = pixelPoint.sub(ray.origin).normalize().mul(100);
		
		
//		Vector3f farOffset = new Vector3f( farPlane.axisH ).mul(x).add(new Vector3f(farPlane.axisV).mul(y)); 
//		Vector3f destination = new Vector3f( farPlane.origin ).add( farOffset );
//		ray.segment = destination.sub(ray.origin);
		
		return ray;
	}
}
