

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;

public class JOGL4_1_Blending extends JOGL3_11_Phong {
 
	  float M_emission[] = {0.1f, 0.1f, 0.1f, 1f}; // Material property: emission 
	  float L_ambient[] = {0.1f, 0.1f, 0.1f, 1f}; // Light source property: ambient 
	  float M_ambient[] = {0.1f, 0.1f, 0.1f, 0.5f}; // Material property: ambient 
	  float L_diffuse[] = {1f, 1f, 1f, 1f}; // Light source property: diffuse 
	  float M_diffuse[] = {.7f, 0.7f, 0.7f, 0.5f}; // Material property: diffuse 
	  float L_position[] = {4*WIDTH, 4*WIDTH, 4*WIDTH, 1}; // Light source property: position

	  float L_specular[] = {1f, 1f, 1f, 1f}; // Light source property: specular 
	  float M_specular[] = {1f, 1f, 1f, 1f}; // Material property: specular 
	  float M_shininess = 100f; // Material property: shininess 
	  float V_position[] = {0, 0, 4*WIDTH, 1}; // View position

	  float L1_diffuse[] = {1, 0.1f, 0.1f, 0.5f}; // Light source property: diffuse 
	  float L1_position[] = {4*WIDTH, 4*WIDTH, 4*WIDTH, 1}; // Light source property: position
	  float L2_diffuse[] = {0.1f, 1, 0.1f, 0.5f}; // Light source property: diffuse 
	  float L2_position[] = {4*WIDTH, 4*WIDTH, 4*WIDTH, 1}; // Light source property: position
	  float L3_diffuse[] = {0.1f, 0.1f, 1, 0.5f}; // Light source property: diffuse 
	  float L3_position[] = {4*WIDTH, 4*WIDTH, 4*WIDTH, 1}; // Light source property: position	  
			    
		 // for transparency: blending, and depth-buffer read-only
	  protected void drawSolar(float E, float e, float M, float m) {		    
			float tiltAngle = 45 * dg;
			float[] tmp = { 0, 0, 0, 1 };
			
			// enable blending for solar system
		    gl.glEnable(GL_BLEND);
		    gl.glBlendFunc(GL_SRC_ALPHA,  GL_ONE_MINUS_SRC_ALPHA);		

			myPushMatrix();
				// coordOff = false; // cjx
				drawSphere(); // for loading matrix purpose: sun
				gl.glLineWidth(2.5f);
				drawColorCoord(WIDTH/5, WIDTH/5, WIDTH/5);
				myTransHomoVertex(tmp, sunC);
		
			    myRotatef(e, 0.0f, 1.0f, 0.0f);
				// rotating around the "sun"; proceed angle
				myRotatef(tiltAngle, 0.0f, 0.0f, 1.0f); // tilt angle
				myTranslatef(0.0f, E, 0.0f);
					

				// draw 3 moons around the earth, draw moons first for retrieving light sources
				myPushMatrix();
				cylinderm = cylinderm + cylinderD;
				myRotatef(cylinderm, 0.0f, 1.0f, 0.0f);
				// rotating around the "earth"
				myTranslatef(M, 0.0f, 0.0f);
				myScalef(E / 8, E / 8, E / 8);
				
				// send the material property to the vertex shader: red cylinder
				M_emission[0] = 1; // Material property: emission
				FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission);
				int colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
				gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
				M_emission[0] = 0.1f;
		
				myTransHomoVertex(tmp, cylinderC);
				// retrieve the center of the cylinder: light source position
				// send the light source positions to the vertex shader
				cBuf = Buffers.newDirectFloatBuffer(cylinderC);
				colorLoc = gl.glGetUniformLocation(vfPrograms, "L1sp");
				gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
							
				drawCylinder();
				gl.glLineWidth(1);
				drawColorCoord(2, 2, 2);		
				myPopMatrix();
		
				myPushMatrix();
				spherem = spherem + sphereD;
				myRotatef(spherem, 0.0f, 1.0f, 0.0f);
				// rotating around the "earth"
				myTranslatef(M, 0.0f, 0.0f);
				myScalef(E / 8, E / 8, E / 8);
		
				// send the material property to the vertex shader: green sphere
				M_emission[1] = 1; // Material property: emission
				cBuf = Buffers.newDirectFloatBuffer(M_emission);
				colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
				gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
				M_emission[1] = 0.1f;
		
				// retrieve the center of the sphere
				myTransHomoVertex(tmp, sphereC);
				cBuf = Buffers.newDirectFloatBuffer(sphereC);
				colorLoc = gl.glGetUniformLocation(vfPrograms, "L2sp");
				gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
		
				drawSphere();
				drawColorCoord(2, 2, 2);		
				myPopMatrix();
		
				myPushMatrix();
				conem = conem + coneD;
				myRotatef(conem, 0.0f, 1.0f, 0.0f);
				// rotating around the "earth"
				myTranslatef(M, 0.0f, 0.0f);
				myScalef(E / 8, E / 8, E / 8);
				
				// send the material property to the vertex shader: blue cone
				M_emission[2] = 1; // Material property: emission
				cBuf = Buffers.newDirectFloatBuffer(M_emission);
				colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
				gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
				M_emission[2] = 0.1f;
		
				// retrieve the center of the cone
				myTransHomoVertex(tmp, coneC);
				// sent to the Vertex Shader in Display() for displaying robot arm
				cBuf = Buffers.newDirectFloatBuffer(coneC);
				colorLoc = gl.glGetUniformLocation(vfPrograms, "L3sp");
				gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
				
				drawCone();
				drawColorCoord(2, 2, 2);
				myPopMatrix();
	
				// restore emission color for the rest of drawing 
				cBuf = Buffers.newDirectFloatBuffer(M_emission);
				colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
				gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);	
				
				gl.glDepthMask(false); // this will make the earth and cone not blocking 
				
				myPushMatrix();
				myTranslatef(0, -2.7f*E/4, 0); 
				myScalef(E, E/4, E);
				myRotatef(90 * dg, 1.0f, 0.0f, 0.0f); // orient the cone
				// upload matrix
		
				drawCone(); // cone goes with the earth: it draws twice so will be twice emission and ambient

				myPopMatrix();
				
				myPushMatrix();
				myScalef(WIDTH / 8, WIDTH / 8, WIDTH / 8);
					
				drawSphere(); // the earth: draw after retrieving the light sources
				gl.glLineWidth(2);
				drawColorCoord(2, 2, 2);
				
				// get the center of the earth
				myTransHomoVertex(tmp, earthC);
				myPopMatrix();
				
				gl.glDepthMask(true); // this will make the rest blocking (robot arm, and moons)  
				
			myPopMatrix();

		    if (distance(coneC, sphereC)<E/5) {
		        // collision detected, swap the rotation directions
		        float tmpD = coneD;
		        coneD = sphereD;
		        sphereD = tmpD;
			    conem = conem+2*coneD;
			    spherem = spherem+2*sphereD;
		      }
		      if (distance(coneC, cylinderC)<E/5) {
		        // collision detected, swap the rotation directions
		        float tmpD = coneD;
		        coneD = cylinderD;
		        cylinderD = tmpD;
			    conem = conem+2*coneD;
			    cylinderm = cylinderm+2*cylinderD;
		      }
		      if (distance(cylinderC, sphereC)<E/5) {
		        // collision detected, swap the rotation directions
		        float tmpD = cylinderD;
		        cylinderD = sphereD;
		        sphereD = tmpD;
			    cylinderm = cylinderm+2*cylinderD;
			    spherem = spherem+2*sphereD;
		      } 		}

		// same as super, but to send over lighting parameters with alpha values changed
		public void display(GLAutoDrawable glDrawable) { 
			cnt++;
			depth = (cnt / 50) % 6;

			gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			if (cnt % 150 == 0) {
				dalpha = -dalpha;
				dbeta = -dbeta;
				dgama = -dgama;
			}
			alpha += dalpha;
			beta += dbeta;
			gama += dgama;

			// send the material property to the vertex shader
			FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission);

			// Connect JOGL variable with shader variable by name
			int colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(L_ambient);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "La");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(M_ambient);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Ma");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(L_diffuse);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Ld");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(M_diffuse);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Md");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(L_position);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Lsp");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(L_specular);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Ls");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(M_specular);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Ms");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Msh");
			gl.glProgramUniform1f(vfPrograms, colorLoc, M_shininess);

			cBuf = Buffers.newDirectFloatBuffer(V_position);

			// Connect JOGL variable with shader variable by name
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Vsp");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(L1_diffuse);
			colorLoc = gl.glGetUniformLocation(vfPrograms, "L1d");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(L2_diffuse);
			colorLoc = gl.glGetUniformLocation(vfPrograms, "L2d");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			cBuf = Buffers.newDirectFloatBuffer(L3_diffuse);
			colorLoc = gl.glGetUniformLocation(vfPrograms, "L3d");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

			if (cnt % 1600<800) {
				gl.glViewport(0, 0, WIDTH, HEIGHT);
				myPushMatrix(); 
			    if (cnt%750<311) 
	 				 myCamera(WIDTH/4, 2f*cnt*dg, WIDTH/6, spherem+sphereD); 			    
				drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
			    myPopMatrix(); 
			}
			else {
				
				myReshape1();
				viewPort1();	
				
				myReshape2();
				viewPort2();					
				viewPort3();
				viewPort4();
				viewPort5();
			}
			
		}

		
	  public static void main(String[] args) {
	    new JOGL4_1_Blending();
	  }

}
