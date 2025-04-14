package ykong7_CS551;



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.gl2.GLUT;

import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;



public class JOGL3_2_Emission extends JOGL2_15_LookAt {
  
	  float M_emission[] = {0.1f, 0.1f, 0.1f, 1}; // Material property: emission 
	  
	  public void drawColorCoord(float xlen, float ylen, float zlen) {

		  float vColor[] = {1, 0, 0}; 
		  float vPoint[] = {0, 0, 0, 0, 0, 0}; 

		  float M_emission1[] = {0.9f, 0.0f, 0.0f, 1}; // Material property: emission 

		  // send the material property to the vertex shader
	 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission1);
		  int colorLoc = gl.glGetUniformLocation(vfPrograms,  "Me"); 
		  gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

			vPoint[3] = xlen; 
		  drawLineJOGL(vPoint, vColor); 

		  float M_emission2[] = {0.0f, 0.9f, 0.0f, 1}; // Material property: emission 

		  // send the material property to the vertex shader
	 	  cBuf = Buffers.newDirectFloatBuffer(M_emission2);
		  colorLoc = gl.glGetUniformLocation(vfPrograms,  "Me"); 
		  gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

		  vPoint[3] = 0; 
		  vPoint[4] = ylen; 
		  drawLineJOGL(vPoint, vColor); 

		  float M_emission3[] = {0.0f, 0.0f, 0.9f, 1}; // Material property: emission 

		  // send the material property to the vertex shader
	 	  cBuf = Buffers.newDirectFloatBuffer(M_emission3);
		  colorLoc = gl.glGetUniformLocation(vfPrograms,  "Me"); 
		  gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);


		  vPoint[4] = 0; 
		  vPoint[5] = zlen; 
		  drawLineJOGL(vPoint, vColor); 

		  // restore emission color 
	 	  cBuf = Buffers.newDirectFloatBuffer(M_emission);
		  colorLoc = gl.glGetUniformLocation(vfPrograms,  "Me"); 
		  gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

	  }

	  
		  
		  public void display(GLAutoDrawable glDrawable) {
			  // send the material property to the vertex shader
		 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission);

				//Connect JOGL variable with shader variable by name
				int colorLoc = gl.glGetUniformLocation(vfPrograms,  "Me"); 
				gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

			  // draw the models
			  super.display(glDrawable);
		  }
  
	  
	  
	  public void init(GLAutoDrawable drawable) {
			
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			System.out.println("a) init: prepare shaders, VAO/VBO"); 

			// 7/26/2022: this is added for accommodating packages
			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL3_2_V.shader"); // read vertex shader
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
	    new JOGL3_2_Emission();
	  }

}
