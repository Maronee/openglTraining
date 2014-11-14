import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;


public abstract class ShaderProgramm {

	private int programId;
	private int vertexShaderId;
	private int fragmentShaderId;
	/**
	 * @param args
	 */
	
	public ShaderProgramm(String vertexFile, String fragmentFile){
		vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShaderId);
		GL20.glAttachShader(programId, fragmentShaderId);
		GL20.glLinkProgram(programId);
		bindAttributes();
		GL20.glValidateProgram(programId);
	}
	
	public void start(){
		GL20.glUseProgram(programId);
	}
	
	public void stop(){
		GL20.glUseProgram(0);
	}
	
	public void cleanUp(){
		stop();
		GL20.glDetachShader(programId, vertexShaderId);
		GL20.glDetachShader(programId, fragmentShaderId);
		GL20.glDeleteShader(vertexShaderId);
		GL20.glDeleteShader(fragmentShaderId);
		GL20.glDeleteProgram(programId);
	}
	
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String varName){
		GL20.glBindAttribLocation(programId, attribute, varName);//varname ist der name im shadercode, attribute die pos des attributes
	}
	
	private static int loadShader(String file,int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null){
				shaderSource.append(line);
				shaderSource.append("\n");
			}
			reader.close();
		}catch(IOException e){
			System.err.println("File not found or other complications");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderId = GL20.glCreateShader(type);
		
		GL20.glShaderSource(shaderId, shaderSource);
		GL20.glCompileShader(shaderId);
		//gl get shader scheint deprecated zu sein,nur zur statusabfrage
		if(GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderId, 500));
			System.err.println("Compiling error with Shader");
		}
		
		return shaderId;
	}

}
