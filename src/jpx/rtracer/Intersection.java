package jpx.rtracer;

import org.joml.Vector3f;

public class Intersection {

	public Vector3f reflectedlight = new Vector3f(0);  
	public Vector3f point = new Vector3f(0); 
	public Vector3f normal = new Vector3f(0,1,0); 
	public Obj object;
	
	public Vector3f radiantLght() {
		if(object == null) {
			return new Vector3f(0,0,0);
		}
		else return new Vector3f(reflectedlight).add(object.material.emissiveLight()); 
	} 

}
