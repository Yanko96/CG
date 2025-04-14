




import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_COMPARE_REF_TO_TEXTURE;
import static com.jogamp.opengl.GL2ES2.GL_DEPTH_COMPONENT;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_COMPARE_FUNC;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_COMPARE_MODE;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

import java.io.File;
import java.nio.FloatBuffer;


public class JOGL4_11_ShadowMapping extends JOGL4_9_Bumpmapping {
	
	static int vfProgram1; // handle to shader programs
	static int vfProgram2; // handle to shader programs
	private int [] shadowTex = new int[1];
	private int [] shadowBuffer = new int[1];

	
	public void display(GLAutoDrawable glDrawable) {
	  // First pass: 
		  // 1. at the end of init, build a framebuffer called shadowBuffer, build a texture to hold the shadowBuffer values
		  // 2. attach the texture to the shadowBuffer, so it corresponds to the Depth buffer 
			gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
			gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[0], 0);
			
			// make sure the attachment is complete
			if(gl.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
				 System.out.println("The depth frame buffer is not ready!"); // false;
			
			// we don't draw into color buffers
			gl.glDrawBuffer(GL_NONE);
			gl.glEnable(GL_DEPTH_TEST);
			gl.glDepthFunc(GL_LEQUAL);
			gl.glClear(GL_DEPTH_BUFFER_BIT);
			gl.glEnable(GL_POLYGON_OFFSET_FILL);	// for reducing
			gl.glPolygonOffset(1.0f, 1.0f);			//  shadow artifacts
					  		  
		  // 3. use shader1: view from light source's point of view
			vfPrograms = vfProgram1; 
			gl.glUseProgram(vfPrograms);

			// need to reset projection, because we use need to use the corresponding shader program here
			gl.glViewport(0, 0, WIDTH, HEIGHT);
			myLoadIdentity();		    
		    myOrtho(-2*WIDTH,2*WIDTH,-2*HEIGHT, 2*HEIGHT, -2*WIDTH, 2*WIDTH);
		    
		    //light source position at the top of the robot arm origin
		    //so to show the earth's shadow on the disk and robot arm
		    L_position[0] = 0;
		    L_position[1] = WIDTH*1.5f;
		    L_position[2] = 0;
		    
		    // lights source's point of view: lightmatrix includes projection matrix: transform the camera
		    mygluLookAt(L_position[0], L_position[1], L_position[2], earthC[0], earthC[1], earthC[2], 
		    		0, 0, 1); 
			float PROJ_Lookat[] = new float [16];			  
			getMatrix(PROJ_Lookat); // get the projectoin (and lookat) matrix from the matrix stack
			int pLoc = gl.glGetUniformLocation(vfPrograms,  "light_matrix"); 
			gl.glProgramUniformMatrix4fv(vfPrograms, pLoc,  1,  false,  PROJ_Lookat, 0);

			// no shadow when depthbuffer is read-only, which is set for some in drawing cone
			myLoadIdentity();
			uploadMV(); 
			myDisplay();
		    
			gl.glDisable(GL_POLYGON_OFFSET_FILL);	// artifact reduction, continued			
			
		  // Second pass: 
		  // 1. deal with vertex then fragment from light's point of view
		  // 2. decide whether the pixel is in the shadow		  
			
				gl.glUseProgram(vfProgram2); // loads them onto the GPU hardware
				vfPrograms = vfProgram2; 

				// this is to restore to default system buffer
				gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
				gl.glDrawBuffer(GL_BACK);

				// this is the precious depth image from the light's point of view 
				gl.glActiveTexture(GL_TEXTURE4);
				gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
							  				
				// Send the same light's point of view matrix to the new vertex shader2
				pLoc = gl.glGetUniformLocation(vfPrograms,  "light_matrix"); 
				gl.glProgramUniformMatrix4fv(vfPrograms, pLoc,  1,  false,  PROJ_Lookat, 0);
				
				// this is the Projectoin matrix without lookat for the second path: uploaded in myortho
				myLoadIdentity();		    
			    myOrtho(-2*WIDTH,2*WIDTH,-2*HEIGHT, 2*HEIGHT, -2*WIDTH, 2*WIDTH);

			    // at this point, the proj_lookat is just a light mapping transformation
			    gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			    cnt++;
				depth = (cnt/100)%6;

			    if (cnt%65==0) {
			      dalpha = -dalpha;
			      dbeta = -dbeta;
			      dgama = -dgama;
			    }
			    alpha += dalpha;
			    beta += dbeta;
			    gama += dgama;

			    myLoadIdentity();
			    
			    myPushMatrix(); 
			    myTranslatef(0, 0, -WIDTH/10); 
			    uploadMV(); 
			    drawBackground(); 
			    gl.glClear(GL_DEPTH_BUFFER_BIT);			    
			    drawLightSource(); 
			    myPopMatrix(); 
			    myDisplay();
	  }

	  public void myDisplay() { // same display without clearing the buffer 
		    
		  // send the material property to the vertex shader
	 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission);

			//Connect JOGL variable with shader variable by name
			int colorLoc = gl.glGetUniformLocation(vfPrograms,  "Me"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);


	 		cBuf = Buffers.newDirectFloatBuffer(L_ambient);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "La"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

	 		cBuf = Buffers.newDirectFloatBuffer(M_ambient);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ma"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
			
	 		cBuf = Buffers.newDirectFloatBuffer(L_diffuse);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ld"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

	 		cBuf = Buffers.newDirectFloatBuffer(M_diffuse);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Md"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
			
	 		cBuf = Buffers.newDirectFloatBuffer(L_position);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Lsp"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

			
	 		cBuf = Buffers.newDirectFloatBuffer(L_specular);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ls"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

	 		cBuf = Buffers.newDirectFloatBuffer(M_specular);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ms"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
			
			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Msh"); 
			gl.glProgramUniform1f(vfPrograms,  colorLoc,  M_shininess);

			cBuf = Buffers.newDirectFloatBuffer(V_position);

			//Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "Vsp"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

			
	 		cBuf = Buffers.newDirectFloatBuffer(L1_diffuse);
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "L1d"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
			
	 		cBuf = Buffers.newDirectFloatBuffer(L2_diffuse);
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "L2d"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
			
	 		cBuf = Buffers.newDirectFloatBuffer(L3_diffuse);
			colorLoc = gl.glGetUniformLocation(vfPrograms,  "L3d"); 
			gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
	
			myPushMatrix(); 
		      if (cnt%750<311) 
 				 myCamera(WIDTH/4, 2f*cnt*dg, WIDTH/6, spherem+sphereD); 			    		      
		      drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
		    myPopMatrix(); 	
		      

	  }
	  
	  void drawLightSource() {
	      // draw the fixed light source as a reference for shadow
		  // send the material property to the vertex shader
		  float M_emissionl[] = {1f, 1f, 1f, 1f}; // Material property: Emission 
	 	  FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emissionl);

		  int colorLoc = gl.glGetUniformLocation(vfPrograms,  "Me"); 
		  gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);

		  float M_ambientl[] = {1f, 1f, 1f, 1f}; // Material property: ambient 
	 	  cBuf = Buffers.newDirectFloatBuffer(M_ambientl);

		  colorLoc = gl.glGetUniformLocation(vfPrograms,  "Ma"); 
		  gl.glProgramUniform4fv(vfPrograms,  colorLoc, 1, cBuf);
	      myPushMatrix();
	      myTranslatef(L_position[0], L_position[1], L_position[2]); 
	      myScalef(WIDTH/30, WIDTH/30, WIDTH/30); 	      
	      drawSphere(); 
	      myPopMatrix(); 

	  }
	  
	  public void drawBackground() { // draw a rectangle to cover the display: pick up each texel
		    float tPoints[] = {
		    		-WIDTH*2, -HEIGHT*2, 0, 
		            WIDTH*2, -HEIGHT*2, 0, 
		            WIDTH*2,  HEIGHT*2, 0,  
		            
		            -WIDTH*2, -HEIGHT*2, 0,  
		             WIDTH*2,  HEIGHT*2, 0, 
		            -WIDTH*2,  HEIGHT*2, 0};  
			float tNormals[] = {0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f}; 
					            
			
			// draw a rectangle of the whole area: for sweeping out the background
			// the two triangles will be transformed into the viewing volume from the z=0
			uploadMV();
			
			// load vbo[0] with vertex data
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
			FloatBuffer vBuf = Buffers.newDirectFloatBuffer(tPoints);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
			
			// load vbo[1] with Normal data
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 0 		
			vBuf = Buffers.newDirectFloatBuffer(tNormals);
			gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
			gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active VAO buffer
					
			gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3); 	
	  }


	  	  
	public void init(GLAutoDrawable drawable) {
		
		
		gl = (GL4) drawable.getGL();
		String vShaderSource1[], vShaderSource2[], fShaderSource1[], fShaderSource2[] ;
					
		System.out.println("a) init: prepare shaders, VAO/VBO, read images as textures, mipmapping, anisotropic"); 
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource1 = readShaderSource("src/"+path+"/JOGL4_11_V1.shader"); // read vertex shader
		vShaderSource2 = readShaderSource("src/"+path+"/JOGL4_11_V2.shader"); // read fragment shader
		fShaderSource1 = readShaderSource("src/"+path+"/JOGL4_11_F1.shader"); // read fragment shader
		fShaderSource2 = readShaderSource("src/"+path+"/JOGL4_11_F2.shader"); // read fragment shader

		vfProgram1 = initShader1(vShaderSource1);		
		vfProgram2 = initShaders(vShaderSource2, fShaderSource2);		

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
					
		//4. specify drawing into only the back_buffer
		gl.glDrawBuffer(GL_BACK); 
		
		// 5. Enable zbuffer and clear framebuffer and zbuffer
		gl.glEnable(GL_DEPTH_TEST); 

		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// read an image as texture
		Texture	joglTexture	=	loadTexture("src/"+path+"/gmu.jpg");						
		gmuTexture	=	joglTexture.getTextureObject(); 
		joglTexture	=	loadTexture("src/"+path+"/VSE.jpg");						
		vseTexture	=	joglTexture.getTextureObject(); 
		joglTexture	=	loadTexture("src/"+path+"/earthCube.jpg");						
		cubeTexture	=	joglTexture.getTextureObject(); 
		joglTexture	=	loadTexture("src/"+path+"/natureCube.jpg");						
		cube1Texture	=	joglTexture.getTextureObject(); 
		joglTexture	=	loadTexture("src/"+path+"/flowerBump.jpg");						
		bumpTexture	=	joglTexture.getTextureObject(); 
		joglTexture	=	loadTexture("src/"+path+"/randomBump.jpg");						
		bump1Texture	=	joglTexture.getTextureObject(); 

		// activate texture #0 and bind it to the texture object
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D,	gmuTexture);
		gl.glActiveTexture(GL_TEXTURE1);
		texParameters(); 
		gl.glBindTexture(GL_TEXTURE_2D,	vseTexture);
		gl.glActiveTexture(GL_TEXTURE2);
		texParameters(); 
		gl.glBindTexture(GL_TEXTURE_2D,	cubeTexture);
		texParameters(); 
		
		gl.glActiveTexture(GL_TEXTURE3);
		gl.glBindTexture(GL_TEXTURE_2D,	bumpTexture);
		texParameters(); 
				

		// 1. this is to say we are creating our own buffer: for shadow
	    gl.glGenFramebuffers(1, shadowBuffer, 0);
		gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
		
		// 2. We create an empty texture holder, to be associated with shadowBuffer
		// later, instead of writing the depth value into the system default, it will write into this texture
		gl.glGenTextures(1, shadowTex, 0);
		gl.glActiveTexture(GL_TEXTURE4);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]); 
		// the empty texture corresponds to the viewport area, and we are saving depth components (32 bits)
		// as I understand it, it should save RGB the same depth value
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32,
						WIDTH, HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		// For textureProj so the system will return between (0, 1), in the shadow or not 
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL); 
		// (r<=Dt? 0 : 1), r is tc, and Dt is depth texture value

		System.out.println("No shadow when depthbuffer is read-only, "); 
		System.out.println("which happens sometimes when drawing cone for transparency.");
		
		
		//Width and Height are used to retrieve corresponding texel in the shader
		//Connect JOGL variable with shader variable by name
		int whLoc = gl.glGetUniformLocation(vfPrograms,  "Height"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  HEIGHT);

		//Connect JOGL variable with shader variable by name
		whLoc = gl.glGetUniformLocation(vfPrograms,  "Width"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  WIDTH);

		
	}
	
	void texParameters() {
	      // This portion is about filters, and anisotropy
			gl.glHint(GL_GENERATE_MIPMAP_HINT, GL_NICEST); 
			gl.glGenerateMipmap(GL_TEXTURE_2D);			
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			
			if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))
			{ float max[ ] = new float[1];
				gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0); // maximum number of anisotropic sizes/images
				gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0]);
				//System.out.println("anisotropic filtering not used!"); 
			}
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

		// 3. attach the shader programs
		int vfProgram = gl.glCreateProgram(); // for attaching v & f shaders
		gl.glAttachShader(vfProgram, vShader);
		gl.glAttachShader(vfProgram, fShader);

		// 4. link the program
		gl.glLinkProgram(vfProgram); // successful linking --ready for using

		//#### because we switch between multiple shaders, we cannot mark them for deletion
		
		//gl.glDeleteShader(vShader); // attached shader object will be flagged for deletion until 
									// it is no longer attached
		//gl.glDeleteShader(fShader);

		// 5. Use the program
		gl.glUseProgram(vfProgram); // loads them onto the GPU hardware
		//gl.glDeleteProgram(vfProgram); // in-use program object will be flagged for deletion until 
										// it is no longer in-use

		return vfProgram;
	}

	public int initShader1(String vShaderSource[]) {

		// 1. create, load, and compile vertex shader
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vShaderSource.length, vShaderSource, null, 0);
		gl.glCompileShader(vShader);


		// 3. attach the shader programs
		int vfProgram = gl.glCreateProgram(); // for attaching v & f shaders
		gl.glAttachShader(vfProgram, vShader);

		// 4. link the program
		gl.glLinkProgram(vfProgram); // successful linking --ready for using

		//#### because we switch between multiple shaders, we cannot mark them for deletion
		
		//gl.glDeleteShader(vShader); // attached shader object will be flagged for deletion until 
									// it is no longer attached
		//gl.glDeleteShader(fShader);

		// 5. Use the program
		gl.glUseProgram(vfProgram); // loads them onto the GPU hardware
		//gl.glDeleteProgram(vfProgram); // in-use program object will be flagged for deletion until 
										// it is no longer in-use

		return vfProgram;
	}

	  public static void main(String[] args) {
		  JOGL4_11_ShadowMapping f= new JOGL4_11_ShadowMapping();
		  f.setTitle("JOGL4_11_ShadowMapping: add a plane for lighting effect.");
	  }

}
