


import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.TextRenderer;

import static com.jogamp.opengl.GL4.*;


public class JOGL1_4_3_Triangle extends JOGL1_4_3_Line {

	
	public void display(GLAutoDrawable drawable) {		
		// 1. draw into the back buffers
	    //gl.glDrawBuffer(GL_BACK);

		// clear the display every frame
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame

		
		// generate a random triangle and display
		int v[][] = new int[3][3]; // each vertex is v[i]

		// generate three random vertices
		for (int i = 0; i < 3; i++) { 
			v[i][0] = (int) (WIDTH * Math.random());
			v[i][1] = (int) (HEIGHT * Math.random());
			v[i][2] = 0;
		}

		// scan-convert the triangle
		drawtriangle(v);
			    
		try {
			Thread.sleep(550);
		} catch (Exception ignore) {}

	}

	
	public void drawtriangle(int[][] v) {
		int ymin, ymid, ymax; // 3 vertices' y location
		
		// 1. sort the vertices along y
		//    find v[ymin], v[ymid], v[ymax]		
		if (v[0][1] < v[1][1]) { // 201; 021; 012
			if (v[0][1] < v[2][1]) { // 021; 012
				ymin = 0;
				if (v[1][1] < v[2][1]) { // 012
					ymid = 1;
					ymax = 2;
				} else { // 021
					ymid = 2;
					ymax = 1;
				}
			} else {// 201
				ymin = 2;
				ymid = 0;
				ymax = 1;
			}
		} else { // 210; 120; 102
			if (v[1][1] < v[2][1]) { // 120; 102
				ymin = 1;
				if (v[0][1] < v[2][1]) { // 102
					ymid = 0;
					ymax = 2;
				} else { // 120
					ymid = 2;
					ymax = 0;
				}
			} else {// 210
				ymin = 2;
				ymid = 1;
				ymax = 0;
			}
		}

		// 2. Calculate 1/m for each edges
		// Given y, when y = y + 1, x = x + 1/m on an edge
		float m_nd = 0, m_nx = 0, m_dx = 0; // 1/m of min-mid; min-max; mid-max
											// edges
		float x1 = v[ymin][0], x2 = v[ymin][0]; // min points of the edges
		int y = v[ymin][1], dy;

		// calculate 1/m for min-max edge
		if ((dy = v[ymax][1] - v[ymin][1]) > 0) // pixels walking along y
			m_nx = (float) (v[ymax][0] - v[ymin][0]) / dy;
		else
			return; // trivial; triangle has no size

		// calculate 1/m for min-mid edge
		if ((dy = v[ymid][1] - v[ymin][1]) > 0) {
			m_nd = (float) (v[ymid][0] - v[ymin][0]) / dy;

		} else { // flat bottom
			x1 = v[ymid][0];
		}

		// calculate 1/m for mid-max edge
		if ((dy = v[ymax][1] - v[ymid][1]) > 0) {
			m_dx = (float) (v[ymax][0] - v[ymid][0]) / dy;

		} else { // flat top
		}

		//3. For each y, draw a horizontal line between x1 and x2 on the two edges
		for (y = v[ymin][1]; y < v[ymid][1]; y++) {
			// for each scan-line

			span((int) x2, (int) x1, y);
			x1 = x1 + m_nd;
			x2 = x2 + m_nx;
		}

		for (y = v[ymid][1]; y < v[ymax][1]; y++) {
			// for each scan-line

			span((int) x2, (int) x1, y);
			x1 = x1 + m_dx;
			x2 = x2 + m_nx;
		}
	}

	
	// draw a horizontal line
	void span(int x2, int x1, int y1) {
		int x0, y0, xn, yn; 
		int x, y;	    

		x0 = x1; y0 = y1; xn = x2; yn = y1; 
	    
	    if (xn<x0) {
	      //swapd(&x0,&xn);
	      int temp = x0;
	      x0 = xn;
	      xn = temp;
	    }
	    
	    x = x0;
	    y = y0;

	    int nPixels = xn - x0 + 1; // number of pixels on the line 	    
	    float[] vPoints = new float[3*nPixels]; // predefined number of pixels on the line
	    float[] vColors = new float[3*nPixels]; // predefined number of pixel colors on the line
	    
	    while (x<xn+1) {
	       // taking care of different slopes (mirror vertical, horizontal, and diagonal lines) 
		   
	      // write a pixel into the framebuffer, here we write into an array
		  vPoints[(x-x0)*3] = 2.0f*x / (float) WIDTH - 1.0f; // normalize -1 to 1
		  vPoints[(x-x0)*3 + 1] = 2.0f*y / (float) HEIGHT - 1.0f; // normalize -1 to 1
		  vPoints[(x-x0)*3 + 2] = 0.0f;
		  
		  // generate corresponding pixel colors
		  vColors[(x-x0)*3] = (float) Math.random(); 
		  vColors[(x-x0)*3 + 1] = (float) Math.random(); 
		  vColors[(x-x0)*3 + 2] = (float) Math.random(); 
		  
	      x++; /* consider next pixel */
	    }
	       
	    
	    // load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
						
	    // load vbo[1] with color data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 0 		
		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(vColors);
		gl.glBufferData(GL_ARRAY_BUFFER, cBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					cBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer

 		
		//  draw points: VAO has is an array of corresponding vertices and colors
		gl.glDrawArrays(GL_POINTS, 0, (vBuf.limit()/3)); 	    
	  }



	
	public void init(GLAutoDrawable drawable) {
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			// 7/26/2022: this is added for accommodating packages
			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL1_4_3_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/"+path+"/JOGL1_4_3_F.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
			
 		}
		
	
	public static void main(String[] args) {
		 new JOGL1_4_3_Triangle();

	}
}


