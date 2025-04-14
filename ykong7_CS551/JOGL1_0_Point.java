



package ykong7_CS551;


/*************************************************
 * Created on August 1, 2017, @author: Jim X. Chen
 *
 * Draw a point 
 * with simple vertex shader and fragment shader
 *
 *
 */

import static com.jogamp.opengl.GL4.*; // OpenGL constants
import com.jogamp.opengl.*; // OepnGL functions

public class JOGL1_0_Point extends JOGL1_0_Frame {

	public JOGL1_0_Point() { // 0. it calls super's constructor first
		//System.out.println("-Dsun.java2d.d3d=false (turn off direct3d)");
		//System.out.println("-Djogl.debug.DebugGL (only when debugging JOGL)");
		//System.out.println("-Djogl.debug.TraceGL (only when tracing JOGL)");
	}


	public void init(GLAutoDrawable drawable) {
		System.out.println("a) init, which overwrite super's init: prepare vertex shader and fragment shader");

		super.init(drawable);

		// the vertex shader runs for all vertices in parallel 
		// gl_Position is a predefined output variable: vertex position for drawing 
		String vShaderSource[] = { 	"#version 410\n",
									"void main(void) {", 
										"gl_Position = vec4(0.0, 0.0, 0.0, 1.0);", 
									"}", 
		};
		
		// the fragment shader is to set the RGB color of the pixel to be displayed. 
		// it runs for all pixels in parallel 
		String fShaderSource[] = { 	"#version 410\n",
									"out vec4 color; ", 
									"void main(void) {", 
										"color	= vec4(1.0, 0.0, 0.0, 1.0); ",
									"}", 
		};

		initShaders(vShaderSource, fShaderSource); // make shader programs ready

	}

	public int initShaders(String vShaderSource[], String fShaderSource[]) {

		// 1. create, load, and compile vertex shader
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vShaderSource.length, vShaderSource, null, 0);
		gl.glCompileShader(vShader);

		// 2. create, load, and compile fragment shader
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fShaderSource.length, fShaderSource, null, 0);
		gl.glCompileShader(fShader);

		// 3. attach the shader programs: these two shaders attached, so vShader "out" goes to fShader "in"
		int vfProgram = gl.glCreateProgram(); // for attaching v & f shaders
		gl.glAttachShader(vfProgram, vShader);
		gl.glAttachShader(vfProgram, fShader);

		// 4. link the program
		gl.glLinkProgram(vfProgram); // successful linking --ready for using

		gl.glDeleteShader(vShader); // attached shader object will be flagged for deletion until 
									// it is no longer attached. 
		gl.glDeleteShader(fShader); // It should not be deleted if you want to use it again after using another shader.  

		// 5. Use the program
		gl.glUseProgram(vfProgram); // loads the program onto the GPU hardware; can switch to another attached shader program.
		gl.glDeleteProgram(vfProgram); // in-use program object will be flagged for deletion until 
										// it is no longer in-use. If you have multiple programs that you switch back and forth,
										// they should not be deleted. 
		return vfProgram;
	}

	
	public void display(GLAutoDrawable drawable) { // overwrite super's display
		System.out.println("c) display is called, which overwrite super's display, to draw a point.");		
		 
		// 8. clear the back-buffer into the background color
		gl.glClear(GL_COLOR_BUFFER_BIT);

		// Supposed to send a VAO with vertex positions to the vertex shader for rendering.

		// 6. specify to draw a primitive: one point
		gl.glPointSize(10.0f);
		gl.glDrawArrays(GL_POINTS, 0, 1); // first index 0, count 1
		
		drawable.swapBuffers();  // my solution to drawing into both buffers
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glDrawArrays(GL_POINTS, 0, 1); // first index 0, count 1
		
		// swapBuffers automatically
	}

	public static void main(String[] args) {
		JOGL1_0_Point f = new JOGL1_0_Point();
		
		f.setTitle("JOGL1_0_Point: drawing a point");
		f.setSize(WIDTH*4/3, HEIGHT); // set the size of the frame 
		f.setLocation(20, 20); // frame's upper-left corner
		// event loop
	}
}
