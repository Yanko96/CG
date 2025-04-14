




import com.jogamp.opengl.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

public class JOGL2_6_ConeInstancing extends JOGL2_5_Cone {
	    
	    public void display(GLAutoDrawable glDrawable) {

   	
	    	int curTimeLoc = gl.glGetUniformLocation(vfPrograms, "Cnt"); // curTime used to transform instances
			gl.glUniform1f(curTimeLoc, cnt);    	
	    	
	    	cnt++;    
						
			cRadius += flip;
			if ((cRadius>(WIDTH/2))|| (cRadius<=1)) {
				depth++;
				depth = depth%7;
		        flip = -flip;
		     }
		   
			//3. clear both framebuffer and zbuffer
		    gl.glClear(GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	   
	    //4. GL_DEPTH_TEST for hidden-surface removal
	    	gl.glEnable(GL_DEPTH_TEST);
	    	//gl.glDisable(GL_DEPTH_TEST);
	    
	 
	   
	    //5. Test glPolygonMode 
	         //gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	    	 //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL_LINE);
	    	 //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL_LINE);
	 
	    //6. Test glCullFace
	    	//gl.glEnable(GL_CULL_FACE); 
	    	//gl.glDisable(GL_CULL_FACE); 
	        //gl.glCullFace(GL_BACK); 
	        //gl.glCullFace(GL_FRONT);

	        
	    //6. draw a triangle for showing hidden surface removal
	    float 	v0[] = {-WIDTH/4, -WIDTH/4, -WIDTH/4}, 
	    		v1[] = {WIDTH/4, 0, WIDTH/4}, 
	    		v2[] = {WIDTH/4, HEIGHT/4, 0}; 
	    myPushMatrix();
	        myLoadIdentity();
	        myRotatef((float) cnt/50f, 0, 1, 0); 
//		    gl.glColor3f(0.5f, 0.5f, 0.5f);
		    transDrawTriangle(v0, v1, v2); 
	    myPopMatrix();
	    
	    // rotate 1 degree alone vector (1, 1, 1)
	    myRotatef(0.01f, 0.5f, 0.5f, 0.5f); // numerical drift if not rotating around the primary axis
	    myRotatef(0.01f, 1f, 0f, 0f);    
	    myPushMatrix();
	    myScalef(cRadius, cRadius, cRadius);
	    //myScalef(50, 50, 50);

		    drawCone(10); // draw 10 copies of the cone 
	    myPopMatrix();

	  }

	  public void drawCone(int instances) {
	    int numofTriangle= 8*(int)Math.pow(2,depth); // number of triangles after subdivision
	    float vPoints[] = new float[3*3*numofTriangle]; // 3 vertices each triangle, and 3 values each vertex

	    //System.out.println("vPoints[] is used to save all triangle vertex values, pervertex values to be sent to the vertex shader");	

	    count = 0; // start filling triangle array to be sent to vertex shader

	    subdivideCone(vPoints, cVdata[0], cVdata[1], depth);
	    subdivideCone(vPoints, cVdata[1], cVdata[2], depth);
	    subdivideCone(vPoints, cVdata[2], cVdata[3], depth);
	    subdivideCone(vPoints, cVdata[3], cVdata[0], depth);
	    
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
				
		gl.glDrawArraysInstanced(GL_TRIANGLES, 0, vBuf.limit()/3, instances); 
	  }


		public void init(GLAutoDrawable drawable) {
			
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			System.out.println("a) init: prepare shaders, VAO/VBO"); 
			// 7/26/2022: this is added for accommodating packages
			String path = this.getClass().getPackageName().replace(".", "/"); 

			vShaderSource = readShaderSource("src/"+path+"/JOGL2_6_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/"+path+"/JOGL2_5_F.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			
			// if you don't use it, you should not enable it
			//gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
						
			//4. specify drawing into only the back_buffer
			gl.glDrawBuffer(GL.GL_BACK); 
			
			// 5. Enable zbuffer and clear framebuffer and zbuffer
			gl.glEnable(GL.GL_DEPTH_TEST); 

 		}
	  
	  
  public static void main(String[] args) {
    new JOGL2_6_ConeInstancing();
  }

}
