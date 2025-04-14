package ykong7_CS551;



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;



public class JOGL3_11_Phong extends JOGL3_7_MoveLight {
	
 

	// rewrite for two-sided lighting
	public void drawCone() {
		    int numofTriangle= 4*(int)Math.pow(2,depth); // number of triangles after subdivision
		    float vPoints[] = new float[3*3*numofTriangle]; // 3 vertices each triangle, and 3 values each vertex
		    float vNormals[] = new float[3*3*numofTriangle]; // 3 vertices each triangle, and 3 values each vertex

		    count = 0; // start filling triangle array to be sent to vertex shader

		    subdivideCone(vPoints, vNormals, cVdata[0], cVdata[1], depth);
		    subdivideCone(vPoints, vNormals, cVdata[1], cVdata[2], depth);
		    subdivideCone(vPoints, vNormals, cVdata[2], cVdata[3], depth);
		    subdivideCone(vPoints, vNormals, cVdata[3], cVdata[0], depth);
		    
		    // send the current MODELVIEW matrix and the vertices to the vertex shader
		    // color is generated according to the logical coordinates   
		    uploadMV(); // get the modelview matrix from the matrix stack
			

			// load vbo[0] with vertex data
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer

			// load vbo[1] with normal data
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 0 		
			 vBuf = Buffers.newDirectFloatBuffer(vNormals);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
			gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active VAO buffer
			gl.glEnable(GL_CULL_FACE);
			gl.glCullFace(GL_BACK);
			gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3); 

			
			
			//reversing the normals and redraw the polygon
			for (int i=0; i<3*3*numofTriangle; i++)
				vNormals[i] = -vNormals[i]; // 3 vertices each triangle, and 3 values each vertex
			
			// load vbo[1] with normal data
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 0 		
			 vBuf = Buffers.newDirectFloatBuffer(vNormals);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
			gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active VAO buffer
					
			gl.glCullFace(GL_FRONT);
			gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3); 
			
			gl.glDisable(GL_CULL_FACE);
		
	  
	  }


	
	
	public void init(GLAutoDrawable drawable) {
			
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			System.out.println("a) init: prepare shaders, VAO/VBO"); 
			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL3_11_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/"+path+"/JOGL3_11_F.shader"); // read fragment shader
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
	    new JOGL3_11_Phong();
	  }

}
