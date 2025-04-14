


import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;

import java.io.File;
import java.nio.FloatBuffer;

public class JOGL4_6_Texture extends JOGL4_5_Image {

	// used for texture objects
	int gmuTexture;
	int vseTexture;
	int count1;

	void subdivideCone(float vPoints[], float vNormals[], float vTexCoord[], float v1[], float v2[], int depth) { // added
																													// texture
																													// coordinates
		float v0[] = { 0, 0, 0 };
		float v12[] = new float[3];

		if (depth == 0) {
			// drawtriangle(v2, v1, v0);
			// bottom cover of the cone

			v0[2] = 1; // height of the cone, the tip on z axis
			// load vPoints with the triangle vertex values
			for (int i = 0; i < 3; i++) {
				vNormals[count] = v1[i] + v0[i];
				vPoints[count++] = v1[i];
			}
			for (int i = 0; i < 3; i++) {
				vNormals[count] = v2[i] + v0[i];
				vPoints[count++] = v2[i];
			}
			for (int i = 0; i < 3; i++) {
				vNormals[count] = v2[i] + v1[i] + v0[i];
				vPoints[count++] = v0[i];
			}

			// texture coordinates for the triangle
			for (int i = 0; i < 2; i++)
				vTexCoord[count1++] = (v1[i] + 1) / 2;
			for (int i = 0; i < 2; i++)
				vTexCoord[count1++] = (v2[i] + 1) / 2;
			for (int i = 0; i < 2; i++)
				vTexCoord[count1++] = (v0[i] + 1) / 2;

			return;
		}

		for (int i = 0; i < 3; i++) {
			v12[i] = v1[i] + v2[i];
		}
		normalize(v12);

		subdivideCone(vPoints, vNormals, vTexCoord, v1, v12, depth - 1);
		subdivideCone(vPoints, vNormals, vTexCoord, v12, v2, depth - 1);
	}

	public void drawCone() {
		int numofTriangle = 4 * (int) Math.pow(2, depth); // number of triangles after subdivision
		float vPoints[] = new float[3 * 3 * numofTriangle]; // 3 vertices each triangle, and 3 values each vertex
		float vNormals[] = new float[3 * 3 * numofTriangle]; // 3 vertices each triangle, and 3 values each vertex
		// texture coordinates corresponding to the vertices
		float vTexCoord[] = new float[2 * 3 * numofTriangle]; // 3 vertices each triangle, and 2 values each vertex

		int cLoc = gl.glGetUniformLocation(vfPrograms, "dCone");
		gl.glProgramUniform1i(vfPrograms, cLoc, 1);

		// turn on and off transparency
		if (cnt % WIDTH > WIDTH / 10) {
			gl.glDisable(GL_BLEND);
			gl.glDepthMask(true);
		} else {
			gl.glEnable(GL_BLEND);
			gl.glDepthMask(false);

		}

		count = 0; // start filling triangle array to be sent to vertex shader
		count1 = 0; // start filling triangle array to be sent to vertex shader

		subdivideCone(vPoints, vNormals, vTexCoord, cVdata[0], cVdata[1], depth);
		subdivideCone(vPoints, vNormals, vTexCoord, cVdata[1], cVdata[2], depth);
		subdivideCone(vPoints, vNormals, vTexCoord, cVdata[2], cVdata[3], depth);
		subdivideCone(vPoints, vNormals, vTexCoord, cVdata[3], cVdata[0], depth);

		// send the current MODELVIEW matrix and the vertices to the vertex shader
		// color is generated according to the logical coordinates
		uploadMV(); // get the modelview matrix from the matrix stack

		// load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer

		// load vbo[1] with normal data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1
		vBuf = Buffers.newDirectFloatBuffer(vNormals);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active VAO buffer

		// load vbo[2] with texture coord data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]); // use handle 2
		vBuf = Buffers.newDirectFloatBuffer(vTexCoord);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW);
		gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0); // associate vbo[2] with active VAO buffer

		// load vbo[2] with texture coord data
		gl.glEnableVertexAttribArray(3); // enable the 2th vertex attribute: TextureCoord
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]); // use handle 2
		vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW);
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0); // associate vbo[2] with active VAO buffer

		gl.glEnable(GL_CULL_FACE);
		gl.glCullFace(GL_BACK);
		gl.glDrawArrays(GL_TRIANGLES, 0, numofTriangle * 3);

		// reversing the normals and redraw the polygon
		for (int i = 0; i < 3 * 3 * numofTriangle; i++)
			vNormals[i] = -vNormals[i]; // 3 vertices each triangle, and 3 values each vertex

		// load vbo[1] with normal data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 0
		vBuf = Buffers.newDirectFloatBuffer(vNormals);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, // # of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active VAO buffer

		gl.glCullFace(GL_FRONT);
		gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit() / 3);

		gl.glDisable(GL_CULL_FACE);

		cLoc = gl.glGetUniformLocation(vfPrograms, "dCone");
		gl.glProgramUniform1i(vfPrograms, cLoc, 0);

		// cnt++; // it is used in the shader to animate the texture
		// Connect JOGL variable with shader variable by name
		int cntLoc = gl.glGetUniformLocation(vfPrograms, "Cnt");
		gl.glProgramUniform1f(vfPrograms, cntLoc, (float) cnt);

	}

	public void drawCylinder() {

		// inform fragment shader to draw cylinder with texture
		int cyLoc = gl.glGetUniformLocation(vfPrograms, "dCylinder");
		//gl.glProgramUniform1i(vfPrograms, cyLoc, 1);
		
		// removed for homework consideration 

		super.drawCylinder();
		 
		cyLoc = gl.glGetUniformLocation(vfPrograms, "dCylinder");
		gl.glProgramUniform1i(vfPrograms, cyLoc, 0);

	}

	public void init(GLAutoDrawable drawable) {

		gl = (GL4) drawable.getGL();
		String vShaderSource[], fShaderSource[];

		System.out.println("a) init: prepare shaders, VAO/VBO, read images as textures"); 
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource = readShaderSource("src/"+path+"/JOGL4_6_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/"+path+"/JOGL4_6_F.shader"); // read fragment shader
		vfPrograms = initShaders(vShaderSource, fShaderSource);

		// 1. generate vertex arrays indexed by vao
		gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
		gl.glBindVertexArray(vao[0]); // use handle 0

		// 2. generate vertex buffers indexed by vbo: here vertices and colors
		gl.glGenBuffers(vbo.length, vbo, 0);

		// 3. enable VAO with loaded VBO data
		gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position

		// if you don't use it, you should not enable it
		gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: Normal

		// if you don't use it, you should not enable it
		gl.glEnableVertexAttribArray(2); // enable the 2th vertex attribute: TextureCoord

		// 4. specify drawing into only the back_buffer
		gl.glDrawBuffer(GL_BACK);

		// 5. Enable zbuffer and clear framebuffer and zbuffer
		gl.glEnable(GL_DEPTH_TEST);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// read an image as texture
		Texture joglTexture = loadTexture("src/"+path+"/gmu.jpg");
		gmuTexture = joglTexture.getTextureObject();
		Texture joglTexture1 = loadTexture("src/"+path+"/VSE.jpg");
		vseTexture = joglTexture1.getTextureObject();

		// activate texture #0 and bind it to the texture object
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, gmuTexture);
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, vseTexture);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		// gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);

		//Width and Height are used to retrieve corresponding texel in the shader
		//Connect JOGL variable with shader variable by name
		int whLoc = gl.glGetUniformLocation(vfPrograms,  "Height"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  HEIGHT);

		//Connect JOGL variable with shader variable by name
		whLoc = gl.glGetUniformLocation(vfPrograms,  "Width"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  WIDTH);

	}

	public static void main(String[] args) {
		new JOGL4_6_Texture();
	}

}
