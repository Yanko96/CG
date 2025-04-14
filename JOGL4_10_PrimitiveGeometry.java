






import com.jogamp.opengl.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

public class JOGL4_10_PrimitiveGeometry extends JOGL2_7_Sphere {
 
	public void display(GLAutoDrawable drawable) {

		cnt++; 
		//Connect JOGL variable with shader variable by name
		int cntLoc = gl.glGetUniformLocation(vfPrograms,  "Cnt"); 
		gl.glProgramUniform1f(vfPrograms,  cntLoc,  (float) cnt);


		super.display(drawable);
	}
		


		public void init(GLAutoDrawable drawable) {
			
			gl = (GL4) drawable.getGL(); 
			String vShaderSource[], fShaderSource[], gShaderSource[] ;
						
			System.out.println("a) init: prepare shaders, geometry shader, VAO/VBO"); 
			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL4_10_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/"+path+"/JOGL4_10_F.shader"); // read fragment shader
			gShaderSource = readShaderSource("src/"+path+"/JOGL4_10_G.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource, gShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			
			// if you don't use it, you should not enable it
			//gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
						
			//4. specify drawing into only the back_buffer
			gl.glDrawBuffer(GL.GL_BACK); 
			
			// 5. Enable zbuffer and clear framebuffer and zbuffer
			gl.glEnable(GL.GL_DEPTH_TEST); 

 		}
	  
		public int initShaders(String vShaderSource[], String fShaderSource[], String gShaderSource[]) {

			// 1. create, load, and compile vertex shader
			int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
			gl.glShaderSource(vShader, vShaderSource.length, vShaderSource, null, 0);
			gl.glCompileShader(vShader);

			// 2. create, load, and compile fragment shader
			int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
			gl.glShaderSource(fShader, fShaderSource.length, fShaderSource, null, 0);
			gl.glCompileShader(fShader);

			// 2. create, load, and compile geometry shader
			int gShader = gl.glCreateShader(GL_GEOMETRY_SHADER);
			gl.glShaderSource(gShader, gShaderSource.length, gShaderSource, null, 0);
			gl.glCompileShader(gShader);

			// 3. attach the shader programs
			int vfProgram = gl.glCreateProgram(); // for attaching v & f shaders
			gl.glAttachShader(vfProgram, vShader);
			gl.glAttachShader(vfProgram, fShader);
			gl.glAttachShader(vfProgram, gShader);

			// 4. link the program
			gl.glLinkProgram(vfProgram); // successful linking --ready for using

			gl.glDeleteShader(vShader); // attached shader object will be flagged for deletion until 
										// it is no longer attached
			gl.glDeleteShader(fShader);
			gl.glDeleteShader(gShader);

			// 5. Use the program
			gl.glUseProgram(vfProgram); // loads them onto the GPU hardware
			gl.glDeleteProgram(vfProgram); // in-use program object will be flagged for deletion until 
											// it is no longer in-use

			return vfProgram;
		}
	  
  public static void main(String[] args) {
    new JOGL4_10_PrimitiveGeometry();
  }

}
