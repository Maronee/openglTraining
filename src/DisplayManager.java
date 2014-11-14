import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;


public class DisplayManager {
	
	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;

	public static void createDisplay(){
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
			Display.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Display.setTitle("3D Plotter");
		GL11.glClearColor(0f, 0f, 0f, 0f);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective( 45.0f, (float) WIDTH / (float) HEIGHT, 0.001f, 100.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	public static void updateDisplay(){
		Display.sync(60);
		Display.update();
	}
	
	public static void destroyDisplay(){
		Display.destroy();
	}
	
}
