package src.Models;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import src.Displayable;

public class Fish implements Displayable, KeyListener
{
	public static float x = 0;
	public static float y = 0;
	
	private float speed = 0.05f;
	private int numToFill = 20;
	protected static boolean full = false;
	private int counter = 0;
	
	//COLOURS
	protected float[] fishColor = {1f, 0.6f, 0.2f};
	protected float[] black = {0, 0, 0};
	protected float[] darkOrange = {0.757f, 0.404f, 0f};
	private float[] yellow = {1, 0.933f, 0};
	private float[] red = {1, 0, 0};
	
	//RADIUS'SSS....S
	protected static float initialBodyRadius = 0.2f;
	protected static float bodyRadius = initialBodyRadius;
	
	private float outerMouthRadiusX = 0.035f;
	private float outerMouthRadiusY = 0.05f;
	
	private float innerMouthRadiusX = 0.017f;
	private float innerMouthRadiusY = 0.025f;
	
	//SAVING POINTS FOR LATER USE
	private Point[] spikes = new Point[16];
	private float[] spikeTheta = new float[16];
	
	private int[] spikesMarks = {15, 30, 45, 60, 90, 105, 120, 135, 165, 180, 195, 210, 240, 255, 270, 285};
	private int[] innerSpikeMarks = {0, 25, 100, 125, 175, 200, 250, 275};
	
	//BUBBLES COLLECTED
	private HashSet<Bubble> collectedBubbles = new HashSet<>();
	
	/**
	 * Displays the fish with OpenGl.
	 * Also has some game logic:
	 * Determines the size of the fish based of how many bubbles collected.
	 * Checks if the number of bubbles it can collect has reached max.
	 * Displays a small explosion if the fish is full of bubbles,
	 * then returns to normal size.
	 * EDIT: The actual display code has been moved into other methods
	 * and this now deals more with the logic.
	 */
	@Override
	public void display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		
		
		if(collectedBubbles.size() > 0)
		{
			float sizeIncrease = (float)collectedBubbles.size() / 100;
			bodyRadius =  initialBodyRadius + sizeIncrease;
		}
		
		if(collectedBubbles.size() >= numToFill)
		{
			full = true;
		}
		
		
		if (!full) 
		{
			displayBody(gl);
			displaySpikes(gl);
			displayMouth(gl);
		}
		else
		{
			displayExplosion(gl);
		}
	
	}
	
	/**
	 * Displays the body of the fish
	 * @param gl the paintbrush for OpenGl
	 */
	private void displayBody(GL2 gl)
	{
		int counter = 0;
		//BODY
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glColor3f(1f, 0.6f, 0.2f);
			gl.glVertex2f(x, y);
			
			for(int i = 0; i <= 300; i++)
		 	{
		 		double theta = (2.0f * Math.PI * i / 300);
		 		gl.glVertex2d(x + Math.sin(theta) * (bodyRadius), y + Math.cos(theta) * (bodyRadius));
		 		
		 		for (int j = 0; j < spikesMarks.length; j++)
		 		{
		 			if(spikesMarks[j] == i)
		 			{
		 				spikes[counter] = new Point((float)(x + Math.sin(theta) * (bodyRadius)),(float) (y + Math.cos(theta) * (bodyRadius)));
			 			spikeTheta[counter] = (float) theta;
			 			counter++;
		 			}
		 		}
		 	}
		gl.glEnd();
		
		//BODY OUTLINE
		gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glColor3fv(darkOrange, 0);
			gl.glLineWidth(3);
			for(int i = 0; i <= 300; i++)
		 	{
				double theta = (2.0f * Math.PI * i / 300);
		 		gl.glVertex2d(x + Math.sin(theta) * (bodyRadius), y + Math.cos(theta) * (bodyRadius));
		 	}
		gl.glEnd();
	}
	
	/**
	 * Displays the spikes of the fish
	 * @param gl the paintbrush for OpenGl
	 */
	private void displaySpikes(GL2 gl)
	{
		//OUTERSPIKES
	 	gl.glBegin(GL2.GL_TRIANGLES);
	 		gl.glColor3fv(darkOrange, 0);
	 		
	 		for(int i = 0; i < spikes.length; i += 2)
	 		{
	 			gl.glVertex2f(spikes[i].getX(), spikes[i].getY());
	 			gl.glVertex2f(spikes[i+1].getX(), spikes[i+1].getY());
	 			
	 			float averageTheta = (spikeTheta[i] + spikeTheta[i+1])/2;
	 			
	 			gl.glVertex2d(x + Math.sin(averageTheta) * (bodyRadius + 0.06), y + Math.cos(averageTheta) * (bodyRadius + 0.06f));
	 		}
	 	gl.glEnd();

		//INNER SPIKES
		gl.glBegin(GL2.GL_TRIANGLES);
			for(int i = 0; i < innerSpikeMarks.length; i += 2)
			{
				double theta = 2.0f * Math.PI * innerSpikeMarks[i] / 300;
				double theta2 = 2.0f * Math.PI * innerSpikeMarks[i + 1] / 300;
				double middleTheta = (theta + theta2) / 2;
				double radius = bodyRadius / 2 + 0.03f;
				gl.glVertex2d(x + Math.sin(theta) * (radius), y + Math.cos(theta) * (radius));
				gl.glVertex2d(x + Math.sin(middleTheta ) * (radius + 0.06f), y + Math.cos(middleTheta) * (radius + 0.06f));
				gl.glVertex2d(x + Math.sin(theta2) * (radius), y + Math.cos(theta2) * (radius));
			}
		gl.glEnd();

	}
	
	/**
	 * Displays the mouth of the fish
	 * @param gl the paintbrush for OpenGl
	 */
	private void displayMouth(GL2 gl)
	{
		//MOUTH
	 	gl.glBegin(GL2.GL_TRIANGLE_FAN);
	 		gl.glColor3f(0.8f, 0.5f, 0.2f);
	 		gl.glVertex2f(x + bodyRadius, y);
			for(int i = 0; i <= 300; i++)
		 	{
		 		double theta = (2.0f * Math.PI * i / 300);
		 		gl.glVertex2d(x + bodyRadius + Math.sin(theta) * (outerMouthRadiusX), y + Math.cos(theta) * (outerMouthRadiusY));
		 	}
	 	gl.glEnd();
	 	gl.glBegin(GL2.GL_TRIANGLE_FAN);
	 		gl.glColor3f(0, 0, 0);
	 		gl.glVertex2f(x + bodyRadius, y);
			for(int i = 0; i <= 300; i++)
		 	{
		 		double theta = (2.0f * Math.PI * i / 300);
		 		gl.glVertex2d(x + bodyRadius + Math.sin(theta) * (innerMouthRadiusX), y + Math.cos(theta) * (innerMouthRadiusY));
		 	}
	 	gl.glEnd();

	}
	
	/**
	 * Displays a small explosion. The length of time that
	 * the explosion is displayed is handled here by a counter.
	 * Also resets the body radius of the fish to initial size.
	 * @param gl the paintbrush for OpenGl
	 */
	private void displayExplosion(GL2 gl)
	{
		gl.glBegin(GL2.GL_LINES);
			
			int numLines = 32;
			for(int i = 0; i <= numLines; i++)
		 	{
				gl.glColor3fv(yellow, 0);
				gl.glVertex2f(x, y);
				gl.glColor3fv(red, 0);
		 		double theta = (2.0f * Math.PI * i / numLines);
		 		gl.glVertex2d(x + Math.sin(theta) * (bodyRadius), y + Math.cos(theta) * (bodyRadius));
		 	}
		gl.glEnd();
		
		bodyRadius = initialBodyRadius;
		collectedBubbles.clear();
		
		//use this to show the explosion for a period of time
		counter++;
		if(counter > 10)
		{
			counter = 0;
			full = false;
		}
	}
	
	/**
	 * Adds a collected bubble to the list of collected bubbles.
	 * @param b a bubble
	 */
	public void addBubble(Bubble b)
	{
		collectedBubbles.add(b);
	}
	
	
	//GETTERS
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getBodyRadius()
	{
		return bodyRadius;
	}
	
	
	//MOVEMENT
	
	public void moveLeft()
	{
		if (x > -1 + bodyRadius)
		{
			x -= speed;
		}
	}
	
	public void moveRight()
	{
		if (x < 1 - bodyRadius)
		{
			x += speed;
		}
	}
	
	public void moveUp()
	{
		if (y < 0.8f - bodyRadius)
		{
			y += speed;
		}
	}
	
	public void moveDown()
	{
		if (y > -1 + bodyRadius)
		{
			y -= speed;
		}
	}

	
	//KEYLISTENER STUFF
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_LEFT)
		{
			moveLeft();
		}
		if (key == KeyEvent.VK_RIGHT)
		{
			moveRight();
		}
		if (key == KeyEvent.VK_UP)
		{
			moveUp();
		}
		if (key == KeyEvent.VK_DOWN)
		{
			moveDown();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	
}
