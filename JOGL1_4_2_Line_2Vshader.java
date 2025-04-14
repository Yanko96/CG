


import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;

import com.jogamp.opengl.*;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL4.*;


public class JOGL1_4_2_Line_2Vshader extends JOGL1_4_1_Point {
	static int vfPrograms0, vfPrograms1; // handle to shader programs
	
	public void display(GLAutoDrawable drawable) {		
		float vPoint[] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}; 
		float color[] = {0.0f, 0.0f, 0.0f}; 

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

		// vertex program only use uniform color
		gl.glDisableVertexAttribArray(1); // not using the 1th vertex attribute: color
		gl.glUseProgram(vfPrograms0);
		
		// use uniform color 
		drawLineJOGL(vPoint, color);
		drawable.swapBuffers();
		drawLineJOGL(vPoint, color);
		
	    // wait for the current line to stay for a while
		try { Thread.sleep(550); } catch (Exception ignore) {}
		
		// DRAW A LINE: using the basic incremental algorithm -- a list of points and colors
		
	    int x0, y0, xn, yn, dx, dy;
	    //1. generate a random line with x0<xn && -1<m<1; horizontal lines
	    do {
	      // according to physical coordinates
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

	   //2. draw the line by using the basic incremental algorithm
 
	    // use per-vertex color shader
		gl.glEnableVertexAttribArray(1); // not using the 1th vertex attribute: color
		gl.glUseProgram(vfPrograms1);

 	   //uploadColor(color); // upload to the shader as uniform
	   line(x0, y0, xn, yn); 
		drawable.swapBuffers();
	   line(x0, y0, xn, yn); 
		
	   // color[0] = 1; // make it yellow color (restore it)
		
		//swap buffer is automatic 

	}
	
	  void uploadColor (float[] color) { //called iColor in the shader
			// send color data to vertex shader through uniform (array): color here is not per-vertex
			FloatBuffer cBuf = Buffers.newDirectFloatBuffer(color);

			//Connect JOGL variable with shader variable by name
			int colorLoc = gl.glGetUniformLocation(vfPrograms0,  "uColor"); 
			gl.glProgramUniform3fv(vfPrograms0,  colorLoc, 1, cBuf);
			  
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
		  // randomly generated colors
		  vColors[(x-x0)*3] = (float) Math.random(); // normalize -1 to 1
		  vColors[(x-x0)*3 + 1] = (float) Math.random(); // normalize -1 to 1
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
			String vShaderSource0[], vShaderSource1[], fShaderSource[] ;
			
			System.out.println("\na) init: ");			
			System.out.println("	load the shader programs; "); 	

						
			// 7/26/2022: this is added for accommodating packages
			String path = this.getClass().getPackageName().replace(".", "/"); 
			System.out.println("	include package directory: " + path); 

			// two different vertex programs to deal with unform color or per-vertex color
			vShaderSource0 = readShaderSource("src/"+path+"/JOGL1_4_2_V0.shader"); // uniform color	
			vShaderSource1 = readShaderSource("src/"+path+"/JOGL1_4_2_V1.shader"); // per-vertex color
			fShaderSource = readShaderSource("src/"+path+"/JOGL1_4_2_F.shader"); // read vertex shader			}
			vfPrograms0 = initShaders(vShaderSource0, fShaderSource);		
			vfPrograms1 = initShaders(vShaderSource1, fShaderSource);		
				
			System.out.println("	prepare VAO and VBO; "); 	
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			//gl.glGenBuffers(1, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			
			// enabled only when you use per-vertex color shader
			//gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
	    
		}
		
		// reused shader programs cannot be marked for deletion
		public int initShaders(String vShaderSource[], String fShaderSource[]) {

			// 1. create, load, and compile vertex shader
			int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
			gl.glShaderSource(vShader, vShaderSource.length, vShaderSource, null, 0);
			gl.glCompileShader(vShader);

			// 2. create, load, and compile fragment shader
			int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
			gl.glShaderSource(fShader, fShaderSource.length, fShaderSource, null, 0);
			gl.glCompileShader(fShader);

			// 3. attach the shader programs: these two shaders are related (attached) together
			int vfProgram = gl.glCreateProgram(); // for attaching v & f shaders
			gl.glAttachShader(vfProgram, vShader);
			gl.glAttachShader(vfProgram, fShader);

			// 4. link the program
			gl.glLinkProgram(vfProgram); // successful linking --ready for using

			gl.glDeleteShader(vShader); // attached shader object will be flagged for deletion until 
										// it is no longer attached. 
			gl.glDeleteShader(fShader); // It should not be deleted if you want to use it again after using another shader.  

			// 5. Use the program
			//gl.glUseProgram(vfProgram); // loads the program onto the GPU hardware; can switch to another attached shader program.
			//gl.glDeleteProgram(vfProgram); // in-use program object will be flagged for deletion until 
											// it is no longer in-use. If you have multiple programs that you switch back and forth,
											// they should not be deleted. 
			return vfProgram;
		}

		
		public void dispose(GLAutoDrawable drawable) {
			//gl.glDeleteProgram(vfPrograms0); // in-use program object will be flagged for deletion  
			//gl.glDeleteProgram(vfPrograms1); // in-use program object will be flagged for deletion  
			super.dispose(drawable);
			System.out.println("	shader programs are marked for deletion.");
		}

	
	public static void main(String[] args) {
		 new JOGL1_4_2_Line_2Vshader();
			System.out.println("demonstrate using two shaders: one for uniform color, one for per-vertex colors.");

	}
}
