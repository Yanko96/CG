





import com.jogamp.opengl.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

public class JOGL2_7_Sphere extends JOGL2_6_Cylinder {
  
	  static float sVdata[][] = { {1.0f, 0.0f, 0.0f}
							      , {0.0f, 1.0f, 0.0f}
							      , {0.0f, 0.0f, 1.0f}
							      , {-1.0f, 0.0f, 0.0f}
							      , {0.0f, -1.0f, 0.0f}
							      , {0.0f, 0.0f, -1.0f}
	  };

	  public void display(GLAutoDrawable glDrawable) {

			cnt++;    
			cRadius += flip;
			if ((cRadius>(WIDTH/2))|| (cRadius<=1)) {
				depth++;
				depth = depth%7;
		        flip = -flip;
		     }
		   
			//3. clear both framebuffer and zbuffer
		    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	   
	        	    
	    // rotate 1 degree alone vector (1, 1, 1)
	    myRotatef(0.01f, 0.5f, 0.5f, 0.5f); // numerical drift if not rotating around the primary axis
	    myRotatef(0.01f, 1f, 0f, 0f);    
	    myPushMatrix();
	    	myScalef(cRadius, cRadius, cRadius);

			//drawCone();
			drawSphere();
	    myPopMatrix();

	  }

	  private void subdivideSphere(float vPoints[],float v1[],
			  float v2[], float v3[], int depth) {
		  float v12[] = new float[3];
		  float v23[] = new float[3];
		  float v31[] = new float[3];

	    if (depth==0) {
			// load vPoints with the triangle vertex values
			for (int i = 0; i < 3; i++)  vPoints[count++] = v1[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v2[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v3[i] ;     
			
			return; 
	    }
	    
	    for (int i = 0; i<3; i++) {
	        v12[i] = v1[i]+v2[i];
	        v23[i] = v2[i]+v3[i];
	        v31[i] = v3[i]+v1[i];
	      }
	      normalize(v12);
	      normalize(v23);
	      normalize(v31);
	      subdivideSphere(vPoints, v1, v12, v31, depth-1);
	      subdivideSphere(vPoints, v2, v23, v12, depth-1);
	      subdivideSphere(vPoints, v3, v31, v23, depth-1);
	      subdivideSphere(vPoints, v12, v23, v31, depth-1);
	    
	  }


	  public void drawSphere() {
	    int numofTriangle= 8*(int)Math.pow(4,depth); // number of triangles after subdivision
	    float vPoints[] = new float[3*3*numofTriangle]; // 3 vertices each triangle, and 3 values each vertex

	    uploadMV(); // get the modelview matrix to the shaders

	    count = 0; // start filling triangle array to be sent to vertex shader

	    subdivideSphere(vPoints, sVdata[0], sVdata[1], sVdata[2], depth);
	    subdivideSphere(vPoints, sVdata[0], sVdata[2], sVdata[4], depth);
	    subdivideSphere(vPoints, sVdata[0], sVdata[4], sVdata[5], depth);
	    subdivideSphere(vPoints, sVdata[0], sVdata[5], sVdata[1], depth);

	    subdivideSphere(vPoints, sVdata[3], sVdata[1], sVdata[5], depth);
	    subdivideSphere(vPoints, sVdata[3], sVdata[5], sVdata[4], depth);
	    subdivideSphere(vPoints, sVdata[3], sVdata[4], sVdata[2], depth);
	    subdivideSphere(vPoints, sVdata[3], sVdata[2], sVdata[1], depth);

	    
	    // send the current MODELVIEW matrix and the vertices to the vertex shader
	    // color is generated according to the logical coordinates   


		// load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW); 
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
				
		gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3); 
	  }

	  
  public static void main(String[] args) {
    new JOGL2_7_Sphere();
  }

}
