


import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;

import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;


public class JOGL1_4_2_Line extends JOGL1_4_1_Point {
	
	public void display(GLAutoDrawable drawable) {		
		float vPoint[] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}; 
		 float color[] = {1.0f, 0.0f, 0.0f}; 

		// 1. draw into both buffers
	    gl.glDrawBuffer(GL_FRONT_AND_BACK);

		// 2. generate 2 random end points: logical coordinates: -1 to 1	
		vPoint[0] = (float) (2*Math.random() - 1);
		vPoint[1] = (float) (2*Math.random() - 1);
		vPoint[3] = (float) (2*Math.random() - 1);
		vPoint[4] = (float) (2*Math.random() - 1);

		// wait for the previous display to stay for a while
		try { Thread.sleep(550); }  catch (Exception ignore) {}
				
		// 3. draw the line with the color using JOGL's line function
		color[0] = 1; color [1] = 1; color[2] = 0; 

		// indicator using uniform color 
		int colorLoc = gl.glGetUniformLocation(vfPrograms,  "uColorLine"); 
		gl.glProgramUniform1i(vfPrograms,  colorLoc, 1); // 1 means using uniform in vertex shader

		gl.glDisableVertexAttribArray(1); // Disable the 1th per-vertex attribute: color		
		drawLineJOGL(vPoint, color);
		drawable.swapBuffers(); // for drawing into both buffers
	    
	    // wait for the current line to stay for a while
		try { Thread.sleep(550); } catch (Exception ignore) {}
		
		// DRAW A LINE: using the basic incremental algorithm -- a list of points
		
	    int x0, y0, xn, yn, dx, dy;
	    //1. generate a random line with x0<xn && -1<m<1; horizontal lines
	    do {
	      // line algorithm is according to physical coordinates
	      x0 = (int) (WIDTH*Math.random());
	      y0 = (int) (HEIGHT*Math.random());
	      xn = (int) (WIDTH*Math.random());
	      yn = (int) (HEIGHT*Math.random());
	      dx = xn-x0;
	      dy = yn-y0;

	      if (y0>yn) {
	        dy = -dy;
	      }
	    } while (dy>dx || x0>xn);

	   //2. draw the line: per-vertex color
 		//Indicate per-vertex color
 	    colorLoc = gl.glGetUniformLocation(vfPrograms,  "uColorLine"); 
		gl.glProgramUniform1i(vfPrograms,  colorLoc, 0);

		gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
	    line(x0, y0, xn, yn); // per-vertex color line		
	}
	
	  void uploadColor (float[] color) { //called iColor in the shader
			// send color data to vertex shader through uniform (array): color here is not per-vertex
			FloatBuffer cBuf = Buffers.newDirectFloatBuffer(color);

			//Connect JOGL variable with shader variable by name
			int colorLoc = gl.glGetUniformLocation(vfPrograms,  "uColor"); 
			gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
			  
	  }

	
	
	  // scan-convert an integer line with slope -1<m<1
	  void line(int x0, int y0, int xn, int yn) {
			float vPoint[] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}; 
			//float vColor[] = {1.0f, 1.0f, 0.0f}; //yellow
		    int x;
		    float m, y;

	    int nPixels = xn - x0 + 1; // number of pixels on the line 	    
	    float[] vPoints = new float[3*nPixels]; // predefined number of pixels on the line
	    float[] vColors = new float[3*nPixels]; // predefined number of pixels on the line

	    m = (float)(yn-y0)/(xn-x0); // slope of the line 

	    x = x0;
	    y = y0;

	    while (x<xn+1) {
	      //3. write a pixel into the framebuffer, here we write into an array
		  vPoints[(x-x0)*3] = (float) (2*x) / (float) WIDTH - 1.0f; // normalize -1 to 1
		  vPoints[(x-x0)*3 + 1] = (float) (2*y) / (float) HEIGHT - 1.0f; // normalize -1 to 1
		  vPoints[(x-x0)*3 + 2] = 0.0f;
		  vColors[(x-x0)*3] = (float) Math.random(); // random 0-1
		  vColors[(x-x0)*3 + 1] = (float) Math.random(); 
		  vColors[(x-x0)*3 + 2] = (float) Math.random();
	      x++;
	      y += m; /* next pixel's position */
	    }
 
	    // 4. load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
						
		// 5. send color data to vertex shader through uniform: one color for a line
	    // 4. load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1 		
		 vBuf = Buffers.newDirectFloatBuffer(vColors);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
									
		// 6. draw points: VAO has 1 array of corresponding vertices 
		gl.glDrawArrays(GL_POINTS, 0, (nPixels)); 
	}
	
	  
	  
	 // specify to draw a line using OpenGL
	  public void drawLineJOGL(float[] vPoint, float[] vColor) {

		uploadColor(vColor); // uniform iColor
		 
		// 1. load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoint);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
			
		// 3. draw a line: VAO has one array of corresponding two vertices
		gl.glDrawArrays(GL_LINES, 0, vPoint.length/3); 
	}
	  
	  
	
		public void init(GLAutoDrawable drawable) {
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
			
			System.out.println("\na) init: load the shader programs; prepare VAO and VBO; "); 	

			// 7/26/2022: this is added for accommodating packages
			String path = this.getClass().getPackageName().replace(".", "/"); 
					
			vShaderSource = readShaderSource("src/"+path+"/JOGL1_4_2_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/"+path+"/JOGL1_4_2_F.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			//gl.glGenBuffers(1, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position

			// remember to turn it on if you use it (when you use uniform color) 
			//gl.glEnableVertexAttribArray(1); // enable the 0th vertex attribute: position
			
			// draw into both buffers
			gl.glDrawBuffer(GL_FRONT_AND_BACK); 
	    
		}
		
	
	public static void main(String[] args) {
		 new JOGL1_4_2_Line();
		 
			System.out.println("send an indicator to allow mixing (using) either uniform or per-vertex colors.");

	}
}
