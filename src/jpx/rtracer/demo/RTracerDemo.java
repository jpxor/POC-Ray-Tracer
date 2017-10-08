package jpx.rtracer.demo;

import javax.swing.JFrame;

import org.joml.Vector2f;
import org.joml.Vector3f;

import jpx.rtracer.Camera;
import jpx.rtracer.Material;
import jpx.rtracer.Obj;
import jpx.rtracer.NaiveRayTracer;
import jpx.rtracer.shapes.Geometry;
import jpx.rtracer.shapes.Plane;
import jpx.rtracer.shapes.Sphere;

public class RTracerDemo {

	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		NaiveRayTracer world = createWorld();
		RayTracerPanel panel = new RayTracerPanel(world, 600,600);
		
		frame.add(panel);
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
//		while(true) {
//			Thread.sleep(16);
//			panel.doRenderToBuffer();
//		}
	}

	private static NaiveRayTracer createWorld() {
		NaiveRayTracer world = new NaiveRayTracer();
		
		Vector3f camPos = new Vector3f(0,-0.1f,-0.0f);
		Vector2f camFov = new Vector2f(60,0);
		world.camera = new Camera( camPos, camFov );
		
		Geometry shape;
		Material material;
		Vector3f position;
		
		float white = 0.6f;
		
		//floor
		position = new Vector3f(0.0f, 0.5f, 0.0f);
		shape = new Plane(new Vector3f(1,0,0), new Vector3f(0,0,2));
		material = new Material(1f);
		material.diffuseColour.set(white);
		Obj object = new Obj(material, position, shape);
		object.name = "floor";
		world.objects.add( object );

		//ceiling
		position = new Vector3f(0f,-0.5f,0);
		shape = new Plane(new Vector3f(0,0,2), new Vector3f(1,0,0));
		material = new Material(1f);
		material.diffuseColour.set(white);
		object = new Obj(material, position, shape);
		world.objects.add( object );

		//left wall
		position = new Vector3f(-0.5f,0f,0);
		shape = new Plane(new Vector3f(0,1,0), new Vector3f(0,0,2));
		material = new Material(1.0f);
		material.diffuseColour.set(0.6f,0,0);
		world.objects.add(new Obj(material, position, shape));

		//right wall
		position = new Vector3f(0.5f,0,0);
		shape = new Plane(new Vector3f(0,0,2), new Vector3f(0,1,0));
		material = new Material(1.0f);
		material.diffuseColour.set(0,0.6f,0);
		world.objects.add(new Obj(material, position, shape));

		//back wall
		position = new Vector3f(0,0,1);
		shape = new Plane(new Vector3f(0,4,0), new Vector3f(4,0,0));
		material = new Material(1.0f);
		material.diffuseColour.set(white);
		world.objects.add(new Obj(material, position, shape));

		//front wall
		position = new Vector3f(0,0,-1);
		shape = new Plane(new Vector3f(8,0,0), new Vector3f(0,8,0));
		material = new Material(1.0f);
		material.diffuseColour.set(white);
		world.objects.add(new Obj(material, position, shape));

		//sphere
		shape = new Sphere(0.3f);
		position = new Vector3f(0.1f,0.2f,0.65f);
		material = new Material(0.2f);
		material.diffuseColour.set(white);
		world.objects.add(new Obj(material, position, shape));

		//sphere
		shape = new Sphere(0.2f);
		position = new Vector3f(-0.23f,0.30f,0.27f);
		material = new Material(0.5f);
		material.diffuseColour.set(white);
		world.objects.add(new Obj(material, position, shape));

		//sphere
		shape = new Sphere(0.1f);
		position = new Vector3f(0.25f,0.40f,.25f);
		material = new Material(0.8f);
		material.diffuseColour.set(white);
		world.objects.add(new Obj(material, position, shape));
		
		//lights
//		shape = new Sphere(0.05f);
//		position = new Vector3f(-0.48f, -0.35f, 0.98f);
//		material = new Material(0.5f);
//		material.emissiveness = 3f;
//		material.diffuseColour.set(1f, 1f, 1f);
//		
//		Obj light = new Obj(material, position, shape);
//		world.objects.add(light);
		
//		material = new Material(0.5f);
//		material.emissiveness = 3f;
//		material.diffuseColour.set(1f, 1f, 1f);
//		position = new Vector3f(.48f, -0.35f, 0.98f);
//		Obj light2 = new Obj(material, position, shape);
//		world.objects.add(light2);
//		
//		material = new Material(0.5f);
//		material.emissiveness = 3f;
//		material.diffuseColour.set(1f, 1f, 1f);
//		position = new Vector3f(0f, -0.15f, 0.98f);
//		Obj light3 = new Obj(material, position, shape);
//		world.objects.add(light3);
//		
//		material = new Material(0.5f);
//		material.emissiveness = 3f;
//		material.diffuseColour.set(1f, 1f, 1f);
//		position = new Vector3f(0.40f, -0.35f, 0.10f);
//		Obj light4 = new Obj(material, position, shape);
//		world.objects.add(light4);
//		
//		material = new Material(0.5f);
//		material.emissiveness = 3f;
//		material.diffuseColour.set(1f, 1f, 1f);
//		position = new Vector3f(-0.48f, -0.35f, 0.10f);
//		Obj light5 = new Obj(material, position, shape);
//		world.objects.add(light5);
		
//		world.lights.add(light);
//		world.lights.add(light2);
//		world.lights.add(light3);
//		world.lights.add(light4);
//		world.lights.add(light5);
		
		shape = new Plane( new Vector3f(0,0,0.25f), new Vector3f(0.25f,0,0) );
		position = new Vector3f(0, -0.498f, 0.35f);
		material = new Material(1f);
		material.emissiveness = 6f;
		material.diffuseColour.set(1);
		Obj skylight = new Obj(material, position, shape);
		world.objects.add(skylight);
		world.lights.add(skylight);
		
//		shape = new Plane( new Vector3f(0,0,0.1f), new Vector3f(0,0.8f,0) );
//		position = new Vector3f(0.499f, 0, 0.2f);
//		material = new Material(1f);
//		material.emissiveness = 10f;
//		material.diffuseColour.set(1);
//		Obj windowlight = new Obj(material, position, shape);
//		world.objects.add(windowlight);
//		world.lights.add(windowlight);
		
		
		return world;
	}
	
	

}
