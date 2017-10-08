package jpx.rtracer;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Material {
	
	public float emissiveness = 0;
	public float roughness = 0.50f;
	public Vector3f diffuseColour = new Vector3f( 
			0.5f + (float)Math.random()/2,
			0.5f + (float)Math.random()/2,
			0.5f + (float)Math.random()/2);
	
	public Material(float roughness) {
		this.roughness = roughness;
	}
	
	public Vector3f diffuseColour() { 
		return diffuseColour;
	}

	public float roughnessSq() {
		return roughness*roughness;
	}
	
	public float roughness() {
		return roughness;
	}

	public float indexOfRefraction() {
		return 1.6f;
	}

	public Vector3fc emissiveLight() { 
		return new Vector3f(diffuseColour).mul(emissiveness); 
	}

}
