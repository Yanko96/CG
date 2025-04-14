package ykong7_CS551;



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;



public class JOGL3_3_Ambient extends JOGL3_2_Emission {
  
	  float L_ambient[] = {0.8f, 0.8f, 0.8f, 1f}; // Light source property: ambient 
	  float M_ambient[] = {0.2f, 0.2f, 0.2f, 1f}; // Material property: ambient 


		  public void display(GLAutoDrawable glDrawable) {
			  
			  // change the light source intensity 
			 for (int i=0; i<3; i++) L_ambient[i] = (10+cnt % 150)/150f; // Light source property: ambient 

				FloatBuffer cBuf = Buffers.newDirectFloatBuffer(L_ambient);

				//Connect JOGL variable with shader variable by name
				int colorLoc = gl.glGetUniformLocation(vfPrograms,  "La"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
				

		 		cBuf = Buffers.newDirectFloatBuffer(M_ambient);

				//Connect JOGL variable with shader variable by name
				colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ma"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
				
				
				// draw the models: remember here emission is uploaded to the vertex shader
				// if you want to upload it again, you cannot call super's display
			    super.display(glDrawable);
				
		  }
		  
		  public void drawSphere() {
			  float M_ambient1[] = {0.8f, 0.3f, 0.8f, 1}; // Material property: ambient 
			  
			  FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_ambient1);

				//Connect JOGL variable with shader variable by name
				int colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ma"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
			 
				super.drawSphere(); 
				
				// restore ambient 
				cBuf = Buffers.newDirectFloatBuffer(M_ambient);

				//Connect JOGL variable with shader variable by name
				colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ma"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
		  }
 
	  
	  
	  public void init(GLAutoDrawable drawable) {
			
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			System.out.println("a) init: prepare shaders, VAO/VBO"); 

			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL3_3_V.shader"); // read vertex shader
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
			//gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
						
			//4. specify drawing into only the back_buffer
			gl.glDrawBuffer(GL_BACK); 
			
			// 5. Enable zbuffer and clear framebuffer and zbuffer
			gl.glEnable(GL_DEPTH_TEST); 

		}

	  
	  public static void main(String[] args) {
	    new JOGL3_3_Ambient();
	  }

}
