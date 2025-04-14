package ykong7_CS551;



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;



public class JOGL3_5_Specular extends JOGL3_4_Diffuse {
  
	  float L_specular[] = {1, 1, 1, 1}; // Light source property: specular 
	  float M_specular[] = {1, 1, 1, 1}; // Material property: specular 
	  float M_shininess = 50; // Material property: shininess 
	  float V_position[] = {0, 0, 10*WIDTH, 1}; // View position

	  

	  public void display(GLAutoDrawable glDrawable) {
			  // send the material property to the vertex shader
				
		 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(L_specular);

				//Connect JOGL variable with shader variable by name
				int colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ls"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

		 		cBuf = Buffers.newDirectFloatBuffer(M_specular);

				//Connect JOGL variable with shader variable by name
				colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ms"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
				
				//Connect JOGL variable with shader variable by name
				colorLoc = gl.glGetUniformLocation(vfPrograms,  "Msh"); 
				gl.glProgramUniform1f(vfPrograms,  colorLoc,  M_shininess);

				cBuf = Buffers.newDirectFloatBuffer(V_position);

				//Connect JOGL variable with shader variable by name
				colorLoc = gl.glGetUniformLocation(vfPrograms,  "Vsp"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

				// draw the models
			    super.display(glDrawable);
		  }
  
	  
	  
	  public void init(GLAutoDrawable drawable) {
			
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			System.out.println("a) init: prepare shaders, VAO/VBO"); 
			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL3_5_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/"+path+"/JOGL3_2_F.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			
			// if you don't use it, you should not enable it
			gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
						
			//4. specify drawing into only the back_buffer
			gl.glDrawBuffer(GL_BACK); 
			
			// 5. Enable zbuffer and clear framebuffer and zbuffer
			gl.glEnable(GL_DEPTH_TEST); 

		}

	  
	  public static void main(String[] args) {
	    new JOGL3_5_Specular();
	  }

}
