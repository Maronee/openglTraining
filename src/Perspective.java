/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */


import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * Shows a space-like particle simulation.
 *
 * @author Oskar
 */
public class Perspective {

    public static void main(String[] args) {
    	Perspective p = new Perspective();
    	p.logic();
    }
    public void logic(){
        // Initialization code Display
        try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.setTitle("Three Dee Demo");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }
        //

        // Initialization code OpenGL
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // Create a new perspective with 30 degree angle (field of view), 640 / 480 aspect ratio, 0.001f zNear, 100 zFar
        // Note: 	+x is to the right
        //     		+y is to the top
        //			+z is to the camera
        gluPerspective((float) 30, 640f / 480f, 0.001f, 100);
        glMatrixMode(GL_MODELVIEW);
        //

        // To make sure the points closest to the camera are shown in front of the points that are farther away.
        glEnable(GL_DEPTH_TEST);
        this.calcVertices();

        // Initialization code random points
        Point[] points = new Point[1000];
        Random random = new Random();
        // Iterate of every array index
        for (int i = 0; i < points.length; i++) {
            // Set the point at the array index to 
            // x = random between -50 and +50
            // y = random between -50 and +50
            // z = random between  0  and -200
            points[i] = new Point((random.nextFloat() - 0.5f) * 100f, (random.nextFloat() - 0.5f) * 100f,
                    random.nextInt(200) - 200);
        }
        // The speed in which the "camera" travels
        float speed = 0.0f;
        //

        while (!Display.isCloseRequested()) {
            // Render

            // Clear the screen of its filthy contents
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Push the screen inwards at the amount of speed
            glTranslatef(0, 0, speed);

            // Begin drawing points
            glBegin(GL_POINTS);
            // Iterate of every point
            for (Point p : points) {
                // Draw the point at its coordinates
                glVertex3f(p.x, p.y, p.z);
            }
            // Stop drawing points
            glEnd();
            
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    		
    		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
    		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
    		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
    		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.vertexCount);
    		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            // If we're pressing the "up" key increase our speed
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                speed += 0.01f;
            }
            // If we're pressing the "down" key decrease our speed
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                speed -= 0.01f;
            }
            // Iterate over keyboard input events
            while (Keyboard.next()) {
                // If we're pressing the "space-bar" key reset our speed to zero
                if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                    speed = 0f;
                }
                // If we're pressing the "c" key reset our speed to zero and reset our position
                if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                    speed = 0;
                    glLoadIdentity();
                }
            }

            // Update the display
            Display.update();
            // Wait until the frame-rate is 60fps
            Display.sync(60);
        }

        // Dispose of the display
        Display.destroy();
        // Exit the JVM (for some reason this lingers on Macintosh)
        System.exit(0);
    }
    
    private int triangle_x;
	private int triangle_y;
	private float[] verticesArray;
	private int vertexCount;
	private FloatBuffer vertices;
	private int vbo;

	public float fromPositive(float i, float n){
		return (0.5f - (1f - i/n ));
	}
	public float fromNegative(float i, float n){
		return (-0.5f -( ( ((n + 1f) - i) / n) - 1f ));
	}
	
    public void calcVertices()
	{
		List<Float> vertexList = new ArrayList<Float>();
        for(int i= 1; i < this.triangle_x + 1; i++){
        	for(int j = 1; j < this.triangle_y + 1; j++){
        		// Dreieck A
        		vertexList.add(this.fromNegative(i, this.triangle_x));// xA1
        		vertexList.add(this.fromPositive(j, this.triangle_y));// yA1
        		vertexList.add(1f);									 // Z koordinate
        		
        		vertexList.add(this.fromNegative(i, this.triangle_x));	
        		vertexList.add(this.fromNegative(j, this.triangle_y));
        		vertexList.add(1f);
        		
        		vertexList.add(this.fromPositive(i, this.triangle_x));
        		vertexList.add(this.fromNegative(j, this.triangle_y));
        		vertexList.add(1f);
        		
        		// Dreieck B
        		vertexList.add(this.fromPositive(i, this.triangle_x));
        		vertexList.add(this.fromNegative(j, this.triangle_y));
        		vertexList.add(1f);									 
        		
        		vertexList.add(this.fromPositive(i, this.triangle_x));
        		vertexList.add(this.fromPositive(j, this.triangle_y));
        		vertexList.add(1f);
        		
        		vertexList.add(this.fromNegative(i, this.triangle_x));
        		vertexList.add(this.fromPositive(j, this.triangle_y));
        		vertexList.add(1f);
        		
        	}
        }
        //list muss man von Float in float casten
        
        this.verticesArray = new float[vertexList.size()];
        this.vertexCount = vertexList.size();
        int k = 0;
        
        for(Float f : vertexList){
        	this.verticesArray[k++] = (f != null ? f : Float.NaN);
        }
        
        
        this.vertices = BufferUtils.createFloatBuffer(this.vertexCount);
        this.vertices.put(this.verticesArray);
        this.vertices.flip();
        
        this.vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.vertices, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        
        //vertexList.clear();
	}
    
    private static class Point {

        final float x;
        final float y;
        final float z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
