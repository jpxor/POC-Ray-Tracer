package jpx.rtracer.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

import org.joml.Vector2i;
import org.joml.Vector3f;

import jpx.rtracer.Ray;
import jpx.rtracer.SynchedTracker;
import jpx.rtracer.NaiveRayTracer;

@SuppressWarnings("serial")
public class RayTracerPanel extends JPanel {

	private float superSampling = 1f;
	
	private NaiveRayTracer world;
	private int[] cbuffer = null;
	private List<Vector2i> pxList = new ArrayList<>();

	public RayTracerPanel(NaiveRayTracer world, int i, int j) {  
		this.setPreferredSize(new Dimension(i,j));
		this.cbuffer = new int[i*j];
		this.world = world;
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				new Thread(()->doRenderToBuffer()).start();
			}
		});
		
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				
				int width = getWidth();
				int height = getHeight();
				
				float sx = (float)x / width;
				float sy = (float)y / height;
				
				Ray ray = world.camera.ray(sx, sy);
				Vector3f c = world.rayTrace(ray).radiantLght();
				
				System.out.println("[xy]: " + c);
			}
		});
	}
	
	public void paintComponent(Graphics g) {			
		int width = (int) (superSampling*getWidth());
		int height = (int) (superSampling*getHeight());
		
		try {
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			image.setRGB(0, 0, width, height, cbuffer, 0, width); 
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
		catch(Exception any) {repaint();}
	}

	synchronized void doRenderToBuffer() {		
		int width = (int) (superSampling*getWidth());
		int height = (int) (superSampling*getHeight());
		
		float invWidth = 1f/width;
		float invHeight = 1f/height;
		
		int size = width*height;
		if( pxList.size() != size ) {
			
			pxList.clear();
			for(int x=0; x<width; ++x) {
				for(int y=0; y<height; ++y) {
					pxList.add(new Vector2i(x,y));
				}
			}
			this.cbuffer = new int[size];
		}
		
		pxList.parallelStream().forEach( (pixel) ->
		{			
			float sx = (float)pixel.x * invWidth;
			float sy = (float)pixel.y * invHeight;
			
			Ray ray = world.camera.ray(sx, sy);
			Vector3f c = world.rayTrace(ray).radiantLght();
			saturate(c);
			
			int index = pixel.x + pixel.y * width;
			if(cbuffer[index] == 0) {
				cbuffer[index] = new Color(c.x, c.y, c.z).getRGB();
			}
			else {
				Color oldC = new Color(cbuffer[index]);
				float red = ((float)oldC.getRed())/256;
				float green = ((float)oldC.getGreen())/256;
				float blue = ((float)oldC.getBlue())/256;
				cbuffer[index] = new Color((red+c.x)/2, (green+c.y)/2,(blue+c.z)/2).getRGB();
			}
			
			repaint();
		});
		
	}

	private void saturate(Vector3f c) {
		c.x = Math.min(1, Math.max(0, c.x));
		c.y = Math.min(1, Math.max(0, c.y));
		c.z = Math.min(1, Math.max(0, c.z));
	}

}
