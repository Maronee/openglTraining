import java.io.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;



public class Plotter {

	private final int WIDTH = 1280;
	private final int HEIGHT = 1024;
	
	private int triangle_x = 20;
	private int triangle_y = 20;
	private float[] verticesArray;
	private FloatBuffer vertices;
	private int vertexCount = 0;
	
	private int shaderProgram;
	private int vertexShader;
	private int fragmentShader;
	
	private int vao;
	private int vbo;
	
	private float verticesZ = -1f;
	
	/*
	 * Triangle A				Triangle B
	 *  1*						3*			*2
	 * 
	 *  2*			*3						*1
	 * 
	 * So werden die Punkte gezählt/gesehen
	 * berechnungen der Knoten wären wie folgt:
	 * Benennung: fromPositive
	 * yA1 = yB2 = yB3 = xA3 = xB1 = xB2 = 0,5 -(1 - i/n), wobei i der Index und n die Anzahl der Dreicke ist
	 * Benennung: fromNegative
	 * yA2 = yA3 = yB1 = xA1 = xA2 = xB3 = -0,5 - ( ((n+1)- i)/n - 1), i index, n Anzahl der Dreicke
	 */

	public float fromPositive(float i, float n){
		return (0.5f - (1f - i/n ));
	}
	public float fromNegative(float i, float n){
		return (-0.5f -( ( ((n + 1f) - i) / n) - 1f ));
	}
		
	public static void main(String[] args){
		Plotter mainProg = new Plotter();
		/*
		DisplayManager.createDisplay();  //Gehört zur Alternativen Methode
		mainProg.run();
		*/
		while(!Display.isCloseRequested()){
			mainProg.run();
			Display.update();
			Display.sync(60);
		}
		mainProg.close();
	}
	
	public Plotter(){
		//auskommentieren für alternative Methode
		try {
			this.setup();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.calcVertices();
	}
	
	public void setup() throws LWJGLException{
		Display.setDisplayMode(new DisplayMode(this.WIDTH,this.HEIGHT));
		Display.setTitle("3D Plotter");
		Display.create();
		
		//GL11.glClearDepth(1);
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glClearColor(0f, 0f, 0f, 0f);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective( 45.0f, (float) this.WIDTH / (float) this.HEIGHT, 0.001f, 100.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glTranslatef(0, 0, 0f);
		//
	}
	
	public void calcVertices()
	{
		List<Float> vertexList = new ArrayList<Float>();
        for(int i= 1; i < this.triangle_x + 1; i++){
        	for(int j = 1; j < this.triangle_y + 1; j++){
        		// Dreieck A
        		vertexList.add(this.fromNegative(i, this.triangle_x));// xA1
        		vertexList.add(this.fromPositive(j, this.triangle_y));// yA1
        		vertexList.add(this.verticesZ);									 // Z koordinate
        		
        		vertexList.add(this.fromNegative(i, this.triangle_x));	
        		vertexList.add(this.fromNegative(j, this.triangle_y));
        		vertexList.add(this.verticesZ);
        		
        		vertexList.add(this.fromPositive(i, this.triangle_x));
        		vertexList.add(this.fromNegative(j, this.triangle_y));
        		vertexList.add(this.verticesZ);
        		
        		// Dreieck B
        		vertexList.add(this.fromPositive(i, this.triangle_x));
        		vertexList.add(this.fromNegative(j, this.triangle_y));
        		vertexList.add(this.verticesZ);									 
        		
        		vertexList.add(this.fromPositive(i, this.triangle_x));
        		vertexList.add(this.fromPositive(j, this.triangle_y));
        		vertexList.add(this.verticesZ);
        		
        		vertexList.add(this.fromNegative(i, this.triangle_x));
        		vertexList.add(this.fromPositive(j, this.triangle_y));
        		vertexList.add(this.verticesZ);
        		
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
        this.vertices.flip();//damit das programm versteht dass gelesen wird
        
        // Dieser Teil müste für den Alternativen ansatz Auskommentiert werden
        // vbo wird erstellt
        this.vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.vertices, GL15.GL_STATIC_DRAW);
        
	}

	public void run(){
		/**
		 * Ein Versuch über mehrer Klassen das rendering 
		 * laufen zu lassen und die Shader so zu laden,
		 * jedoch habe ich keine akzeptable Ausgabe geschafft.
		 * Deswergen wird nur das Mesh gezeigt.
		 */
		
		/*
		 * Die Alternativen Methoden bei denen aber es nicht gefruchtet hat,
		 * Shader Dateien schienen nicht in Ordnung
		 * 
		 *
		StaticShader shader = new StaticShader();
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		RawModel model = loader.loadToVao(this.verticesArray);
		
		while(!Display.isCloseRequested())
		{
			renderer.prepare();
			shader.start();
			renderer.render(model);
			shader.stop();
			//GL11.glLoadIdentity();
			DisplayManager.updateDisplay();
			
		}
		*/
		
		
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0, 0, -0.5f);  //setzt das Koordinatensystem auf Projektionsmatrix
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.vertexCount);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		
	}
	
	public void close(){
		GL15.glDeleteBuffers(this.vbo);
		Display.destroy();
		System.exit(0);
	}
}
