



import com.jogamp.opengl.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

public class JOGL2_6_Cylinder extends JOGL2_5_Cone {
  
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
	    	myScalef(cRadius, cRadius, 2*cRadius);

			//drawCone();
			drawCylinder();
	    myPopMatrix();

	  }

	  void subdivideCylinder(float vPoints[],float v1[],
			  float v2[], int depth) {
		  float v0[] = {0, 0, 0};
		  float v12[] = new float[3];

	    if (depth==0) {
	      // gl.glColor3d(v1[0]*v1[0], v1[1]*v1[1], 0);
	      // color is specified in the Fragment shader
	    	
	      //drawtriangle(v2, v1, v0);
	      // bottom cover of the cone
			// load vPoints with the triangle vertex values
			for (int i = 0; i < 3; i++)  vPoints[count++] = v2[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v1[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v0[i] ;     

			  float v11[] = new float[3];
			  float v22[] = new float[3];
		      for (int i = 0; i<2; i++) {
			        v22[i] = v2[i];
			        v11[i] = v1[i];
			  }
		      v11[2] = v22[2] = v0[2] = 1.0f;

		      
			for (int i = 0; i < 3; i++)  vPoints[count++] = v1[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v2[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22[i] ;     

			for (int i = 0; i < 3; i++)  vPoints[count++] = v1[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v11[i] ;     
				
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v11[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v0[i] ;     

			
			/*
		      gl.glBegin(GL.GL_POLYGON);
		      // draw the side rectangles of the cylinder
		      gl.glVertex3fv(v1,0);
		      gl.glVertex3fv(v2,0);
		      gl.glVertex3fv(v22,0);
		      gl.glVertex3fv(v11,0);
		      gl.glEnd();
	    	*/
		      
		      
	      //drawtriangle(v1, v2, v0); // side cover of the cone
			
            return;

	    
	    }

	    for (int i = 0; i<3; i++) {
	      v12[i] = v1[i]+v2[i];
	    }
	    normalize(v12);

	    subdivideCylinder(vPoints, v1, v12, depth-1);
	    subdivideCylinder(vPoints, v12, v2, depth-1);
	    
	  }


	  public void drawCylinder() {
	    int numofTriangle= 2*3*4*(int)Math.pow(2,depth); // number of triangles after subdivision
	    float vPoints[] = new float[3*3*numofTriangle]; // 3 vertices each triangle, and 3 values each vertex

	    //System.out.println("vPoints[] is used to save all triangle vertex values, pervertex values to be sent to the vertex shader");	

	    count = 0; // start filling triangle array to be sent to vertex shader

	    subdivideCylinder(vPoints, cVdata[0], cVdata[1], depth);
	    subdivideCylinder(vPoints, cVdata[1], cVdata[2], depth);
	    subdivideCylinder(vPoints, cVdata[2], cVdata[3], depth);
	    subdivideCylinder(vPoints, cVdata[3], cVdata[0], depth);
	    
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
				
		gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3); 
	  }


	  
  public static void main(String[] args) {
    new JOGL2_6_Cylinder();
  }

}
