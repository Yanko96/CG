package ykong7_CS551;


import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;


public class JOGL1_4_3_TrianglePattern extends JOGL1_4_3_Triangle {
	// generate a random triangle and display
	int v[][] = new int[3][3]; // each vertex is v[i]

	
	public  JOGL1_4_3_TrianglePattern() {
		// generate three random vertices
		for (int i = 0; i < 3; i++) { 
			v[i][0] = (int) (WIDTH/5 + WIDTH * Math.random()/1.5);
			v[i][1] = (int) (HEIGHT/5 + HEIGHT * Math.random()/1.5);
			v[i][2] = 0;
		}
		delta = 2; // initialize translation distance
	}
	
	public void display(GLAutoDrawable drawable) {		

		// clear the display every frame
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame

		
		// update the vertices: triangle is moving
		for (int i = 0; i < 3; i++) { 
			v[i][0] = v[i][0] + (int) delta;
			v[i][1] = v[i][1] + (int) delta;
			v[i][2] = 0;
		}
		
		if (v[0][0]>WIDTH || v[0][1]>HEIGHT) {// change direction of moving 
			delta = -delta;  
			for (int i = 0; i < 3; i++) v[i][0] = v[i][0] + (int) delta;
		}
		
		else if (v[0][0]< 0 || v[0][1]<0) { // change the direction of moving
			delta = -delta;  
			for (int i = 0; i < 3; i++) v[i][0] = v[i][0] + (int) delta;
		}
			
		
		//Connect JOGL variable with shader variable by name
		int posLoc = gl.glGetUniformLocation(vfPrograms,  "sPos"); 
		gl.glProgramUniform1i(vfPrograms,  posLoc,  (v[0][0]+v[1][0]+v[2][0])/3);

		// scan-convert the triangle
		drawtriangle(v);
		
		// DRAW A LINE: using Bresenham's algorithm
	    //bresenhamLine(v[0][0], v[0][1], v[1][0], v[1][1]); 
	    //bresenhamLine(v[1][0], v[1][1], v[2][0], v[2][1]); 
	    //bresenhamLine(v[2][0], v[2][1], v[0][0], v[0][1]); 
	    

	}
	
	public void init(GLAutoDrawable drawable) {
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
			
			System.out.println("a) init: prepare shaders, VAO/VBO"); 
						
			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL1_4_3_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/"+path+"/JOGL1_4_3_TrianglePattern_F.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color		
	
			// 1. draw into the back buffers
		    gl.glDrawBuffer(GL_BACK);
	}
		
	
	public static void main(String[] args) {
		 new JOGL1_4_3_TrianglePattern();

	}
}


