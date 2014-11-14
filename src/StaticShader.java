
public class StaticShader extends ShaderProgramm {

	private static final String VERTEX_FILE = "shader/shader.vert";
	private static final String FRAGMENT_FILE = "shader/shader.frag";
	
	
	
	public StaticShader(){
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");// wo unsere pos in vao gespeichert seind
	}
}
