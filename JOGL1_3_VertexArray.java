

/*************************************************
 * Created on August 1, 2017, @author: Jim X. Chen
 *
 * Draw multiple points (in parallel)
 * 
 * a) Another method of sending values to the vertex shader(s) respectively
 * b) Efficient transfer of default per-vertex values: position, color, normal, texture coordinates, etc.
 * 
 * VBO: arrays to store vertex positions, colors, and other per-vertex information
 * VAO: an array that packs multiple VBO for transferring to the vertex shader
 *
 */

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;

import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;

public class JOGL1_3_VertexArray extends JOGL1_2_Animate {
	protected static int vao[] = new int[1]; // vertex array object (handle), for sending to the vertex shader
	protected static int vbo[] = new int[4]; // vertex buffers objects (handles) to stores position, color, normal, etc

	// array of vertices and colors corresponding to the vertices: a triangle
	static float vPoints[] = { -0.5f, 0.0f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f };

	public void display(GLAutoDrawable drawable) {
		float vColors[] = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f };

		// clear the display every frame
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame
		
		// pos goes from -1 to 1 back and forth
		pos += delta;
		if (pos <= -1.0f)
			delta = 0.005f;
		else if (pos >= 1.0f)
			delta = -0.005f;

		// Connect JOGL variable with shader variable by name
		int posLoc = gl.glGetUniformLocation(vfPrograms, "sPos");
		gl.glProgramUniform1f(vfPrograms, posLoc, pos);

		// demonstrate variable points coordinates: x position of vertex2 and vertex3
		vPoints[3] = pos;
		vPoints[6] = -pos;

		// 3. load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints); // vertices packed to upload to V shader
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex positions
				GL_STATIC_DRAW); // the data is static: modified once and used many times for GL drawing commands
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
		// layout (location = 0) in V shader; 3 values for each vertex position
		// layout (location = 0) in vec3 iPosition; // VBO: vbo[0]

		// 4. load vbo[1] with color data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1
		vBuf = Buffers.newDirectFloatBuffer(vColors);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex colors
				GL_STATIC_DRAW);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active vao buffer
		// layout (location = 1) in V shader; 3 values for each vertex color

		gl.glPointSize(6.0f);

		// 6. draw 3 points: VAO has two arrays of corresponding vertices and colors
		gl.glDrawArrays(GL_POINTS, 0, 3); // 3: size of the array (attributes)
		if (pos > 0.1f)
			gl.glDrawArrays(GL_LINE_LOOP, 0, 3);
		else if (pos < -0.1f)
			gl.glDrawArrays(GL_TRIANGLES, 0, 3);
	}


	
	public void init(GLAutoDrawable drawable) {

		String vShaderSource[], fShaderSource[];
		gl = (GL4) drawable.getGL();

		System.out.println("a) init: per-vertex values (vertex attributes: positions, colors, etc.) to the vertex shaders in parallel");

		// 7/26/2022: this is added for accommodating packages
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource = readShaderSource("src/"+path+"/JOGL1_3_V.shader"); // read vertex shader	
		fShaderSource = readShaderSource("src/"+path+"/JOGL1_3_F.shader"); // read vertex shader			}
			
		vfPrograms = initShaders(vShaderSource, fShaderSource);

		// 1. generate vertex arrays indexed by vao
		gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
		System.out.println("	Generate a VAO to hold VBO - arrays of vertex attributes (positions, colors)"); // we only use one vao
		gl.glBindVertexArray(vao[0]); // use handle 0

		// 2. generate vertex buffers indexed by vbo: here to store vertices and colors
		gl.glGenBuffers(vbo.length, vbo, 0);
		System.out.println("	Generate VBO (" + vbo.length + ") to hold arrays of vertex attributes."); // we use two: position
																									// and color
		// 5. enable VAO with loaded VBO data: accessible in the vertex shader
		gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
		gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
		System.out.print("	Enable corresponding vertex attributes, "); // we use two: position and color
		System.out.println("and then load the attributes in display()."); // we use two: position and color

	}

	public static void main(String[] args) {
		new JOGL1_3_VertexArray();
		System.out.println("per-vertex values to the shaders; drawing points, lines, and triangles with per-vertex colors.");

	}
}
