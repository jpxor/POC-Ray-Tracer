package jpx.rtracer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import org.joml.Vector3f;
import org.joml.Vector3fc;

//reference for later: 
//https://www.scratchapixel.com
//https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel
//http://raytracey.blogspot.ca/2016/11/opencl-path-tracing-tutorial-2-path.html
//gpu realtime path tracing tutorials:https://github.com/straaljager/



public class NaiveRayTracer {

	public final int MaxDepth = 2;
	public final int SampleCount = 32;
	
	Random random = new Random();
	public float[] normalDistributed;
	public float[] uniformDistributed;
	public int salt = 0;
	
	public final static float PI = (float)Math.PI; 
	public final static float INVPI = 1f/PI;
	public final static float SQRT2OVERPI = (float)Math.sqrt(2f/PI);
	
	private float ambiantLight = 0f;
	
	public List<Obj> lights = new ArrayList<>();
	public List<Obj> objects = new ArrayList<>();
	public Camera camera;
	
	//compute
	public BiFunction<Ray,Intersection,Intersection> computeSurfaceRadiance = this::NaiveTrace;
	
	/**
	 * 
	 */
	public NaiveRayTracer() {		
		int m = 3;
		normalDistributed = new float[m*SampleCount];
		uniformDistributed = new float[m*SampleCount];
				
		for(int i=0; i<m*SampleCount; ++i) {
			normalDistributed[i] = (float) random.nextGaussian();
			uniformDistributed[i] = random.nextFloat();
		}
	}
	
	/**
	 * 
	 */
	public Intersection rayTrace(Ray ray) {
		SynchedTracker<Intersection,Float> tracker = new SynchedTracker<>( (val,min)->val<min, Float.MAX_VALUE );
		objects.parallelStream().forEach( (obj)->
		{
			Intersection intersection = obj.intersects(ray);
			if( intersection != null ) {
				float distanceSqr = ray.origin.distanceSquared(intersection.point);
				tracker.offer(intersection, distanceSqr); 
			}
		});
		Intersection surface = tracker.get();
		if(surface == null) {
			return new Intersection();
		}
		
		if(surface.object.material.emissiveness > 0) {
			return surface;
		}
		
		if(ray.depth == MaxDepth) {
			surface.reflectedlight = new Vector3f(ambiantLight);
			return surface;
		}
		return computeSurfaceRadiance.apply(ray, surface);
	}
		
	/**
	 * 
	 */
	private Intersection NaiveTrace(Ray ray, Intersection surface) {
		int numLights = this.lights.size();
		
		//steps requiring samples: each light, diffuse from normal, specular from reflection
		int sampleSets = 3; 
		int samplesPerSet = SampleCount / sampleSets;
		int unusedSamples = SampleCount % sampleSets;
		
		//viewer
		Vector3fc fromViewer = new Vector3f(ray.segment).normalize();
		Vector3fc toViewer = new Vector3f(fromViewer).negate();
		
		//surface properties
		Vector3fc normal = surface.normal;
		Vector3fc smoothReflection = new Vector3f(fromViewer).reflect(normal);
		float viewIncidence = saturate(toViewer.dot(normal) );
		
		//place holders
		Vector3f toSource = new Vector3f();
		
		//sum incident radiance here
		Vector3f radiance = new Vector3f();
		
		//sample the lights directly
		for(Obj light : lights) {
			
			toSource.set( light.position() ).sub(surface.point);
			float sourceDistance = toSource.length();			
			
			float boundingRadius = light.geom.boundingRadius();
			float sampleAngle = boundingRadius/sourceDistance;
			
			float mean = 0;
			float stdDeviation = sampleAngle/4;
			int numSamples = samplesPerSet/numLights;
			
			Vector3f sampleRadiance = takeSamples(numSamples, viewIncidence, toViewer, smoothReflection, toSource.normalize(), mean, stdDeviation, ray, surface); 
			radiance.add(sampleRadiance);
		}
		
		//sample the normal direction for better diffuse and global lighting
		toSource.set( normal );
//		{
//			float mean = 0;
//			float stdDeviation = PI/4;
//			int numSamples = samplesPerSet;
//		
//			Vector3f sampleRadiance = takeSamples(numSamples, viewIncidence, toViewer, smoothReflection, toSource, mean, stdDeviation, ray, surface); 
//			radiance.add(sampleRadiance);
//		}
		
		//sample the reflection direction for better specular and mirror
		toSource.set( smoothReflection );
		{
			float r = surface.object.material.roughness;
			
			float mean = 0;
			float stdDeviation = (0.75f+r/4)*PI/4; //lower roughness makes for sharper reflections
			int numSamples = samplesPerSet;
		
			Vector3f sampleRadiance = takeSamples(numSamples, viewIncidence, toViewer, smoothReflection, toSource, mean, stdDeviation, ray, surface); 
			radiance.add(sampleRadiance);
		}
				
		float invN = 1f/SampleCount;
		surface.reflectedlight.set( radiance.mul(invN) );
		return surface;
	}
	
	/**
	 * 
	 * @param numSamples
	 * @param viewIncidence
	 * @param toViewer
	 * @param smoothReflection
	 * @param toSource
	 * @param mean
	 * @param stdDeviation
	 * @param ray
	 * @param surface
	 * @param rand
	 * @return
	 */
	private Vector3f takeSamples(int numSamples, float viewIncidence, Vector3fc toViewer, Vector3fc smoothReflection, Vector3fc toSource, float mean, float stdDeviation, Ray ray, Intersection surface) { 
		Vector3f microPerpendicular = findPerpendicular(toSource, new Vector3f());
		Vector3f microReflection = new Vector3f();
		Vector3f rayend = new Vector3f();
		
		Vector3f retRadiance = new Vector3f();
		Vector3f diffuse = new Vector3f();
		Vector3f specular = new Vector3f();
		Vector3f half = new Vector3f();
		
		for(int i=0; i<numSamples; ++i) {
			
			//generate sample direction
			float rotation = uniformDistribution(0, 2*PI);
			float microAngle = normalDistribution(mean, stdDeviation);
			microReflection.set(toSource);
			microReflection.rotateAxis(microAngle, microPerpendicular.x, microPerpendicular.y, microPerpendicular.z);
			microReflection.rotateAxis(rotation, toSource.x(), toSource.y(), toSource.z() );
			
			//cast ray
			Ray sourceRay = new Ray( surface.point, rayend.set(surface.point).add(microReflection) );   
			sourceRay.depth = 1 + ray.depth;
			sourceRay.addBias(surface.normal, 0.0001f);
			Intersection source = rayTrace(sourceRay); 
			
			//diffuse
			float sourceIncidence = Math.max(0, microReflection.dot(surface.normal) ); 
			diffuse.set( source.radiantLght() ).mul(sourceIncidence).mul(surface.object.material.diffuseColour());
			
			//specular
			toViewer.half(microReflection, half);
			float reflectIncidence = Math.max(0, microReflection.dot(smoothReflection) ); 
			specular.set( source.radiantLght() ).mul(reflectIncidence);
			
			//combining with specular power
			float ior = surface.object.material.indexOfRefraction();
			float r = surface.object.material.roughness();
			
			float cos0 = saturate( half.dot(toViewer) );
			
			float D = Distribution(surface.normal, half, r);
			float G = Geometry(viewIncidence, r)*Geometry(sourceIncidence, r); 
			float F = Fresnel( cos0, ior); 
			
			if( viewIncidence != 0 && sourceIncidence != 0 ) {
				float ks = F;
				float kd = 1-ks;
				
				retRadiance.add( specular.mul(ks*viewIncidence*G*D/( 4*viewIncidence*sourceIncidence )) );
				retRadiance.add( diffuse.mul(kd) );
			}
		}
		return retRadiance;
	}
	
	/**
	 * 
	 * @param rand
	 * @param min
	 * @param max
	 * @return
	 */
	private float uniformDistribution(float min, float max) { 
//		if(salt < 0) salt = 0; //this is why we need unsigned values!
//		float rand = uniformDistributed[salt++ % uniformDistributed.length ];

		float rand = random.nextFloat();
		return min + (max-min)*rand;
	}


	/**
	 * 
	 * @param rand
	 * @param mean
	 * @param stdDeviation
	 * @return
	 */
	private float normalDistribution(float mean, float stdDeviation) {
//		if(salt < 0) salt = 0; //this is why we need unsigned values!
//		float rand = normalDistributed[salt++ % normalDistributed.length ];
		
		float rand = (float) random.nextGaussian();
		return mean + stdDeviation*rand;
	}

	
	/** 
	 * @param src
	 * @param dest
	 * @return
	 */
	private Vector3f findPerpendicular(Vector3fc src, Vector3f dest) {
		if(src.z() != 0) {
			float pz = -src.x()/src.z();
			dest.set(1, 0, pz).normalize();
		}
		else if(src.y() != 0) {
			float py = -src.x()/src.y();
			dest.set(1, py, 0).normalize();
		}
		else {
			dest.set(0, 1, 0);
		}
		return dest;
	}
			
	/**
	 * 
	 * @param v
	 * @return
	 */
	private float saturate(float v) {
		return Math.min(1, Math.max(0, v)); 
	}
	
	//https://en.wikipedia.org/wiki/Indicator_function
	private float indX(float v) {
		return v>0? 1:0;
	}
	
	//http://graphicrants.blogspot.ca/2013/08/specular-brdf-reference.html
	//http://www.codinglabs.net/article_physically_based_rendering_cook_torrance.aspx
	private float Distribution( Vector3fc normal, Vector3fc half, float r ){
		float VH = normal.dot(half);
		float VH2 = VH*VH;
		float a2 = r * r * r * r;

		float fsub = 1 + VH2*(a2-1);
		return (indX(VH) * a2) / ( PI * fsub * fsub );
	}
	
	//https://en.wikipedia.org/wiki/Fresnel_equations
	//https://en.wikipedia.org/wiki/Schlick%27s_approximation
	private float Fresnel(float cos0, float ior) {
		float F0 = (1.0f - ior) / (1.0f + ior);
		F0 = F0 * F0;
		return F0 + (1-F0) * (float)Math.pow( 1-cos0, 5);
	}
	
	//http://graphicrants.blogspot.ca/2013/08/specular-brdf-reference.html
	float Geometry(float incidence, float r)
	{
		float a = r*r;
		float k  = a*SQRT2OVERPI;
		return incidence/(k + incidence*(1-k)); //UE4
	}

}
