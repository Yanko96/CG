


import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;

public class JOGL3_7_MoveLight extends JOGL3_6_Materials {

	float L1_diffuse[] = { 1, 0.1f, 0.1f, 1 }; // Light source property: diffuse
	float L1_position[] = { 4 * WIDTH, 4 * WIDTH, 4 * WIDTH, 1 }; // Light source property: position
	float L2_diffuse[] = { 0.1f, 1, 0.1f, 1 }; // Light source property: diffuse
	float L2_position[] = { 4 * WIDTH, 4 * WIDTH, 4 * WIDTH, 1 }; // Light source property: position
	float L3_diffuse[] = { 0.1f, 0.1f, 1, 1 }; // Light source property: diffuse
	float L3_position[] = { 4 * WIDTH, 4 * WIDTH, 4 * WIDTH, 1 }; // Light source property: position

	// draw earth after drawing the moons, to retrieve them as light sources for multiple viewports
	// This was not a problem with a single viewport, but multiple viewports may have different views, 
	// so previous viewport call may retrieve earth centers at different locations
	protected void drawSolar(float E, float e, float M, float m) {
		float tiltAngle = 45 * dg;
		float[] tmp = { 0, 0, 0, 1 };

		myPushMatrix();
			// Global coordinates
			gl.glLineWidth(3);
			// coordOff = false; // cjx
			drawSphere(); // for loading matrix purpose: sun
			myTransHomoVertex(tmp, sunC);
			drawColorCoord(WIDTH / 5, WIDTH / 5, WIDTH / 5);
	
			myRotatef(e, 0.0f, 1.0f, 0.0f);
			// rotating around the "sun"; proceed angle
			myRotatef(tiltAngle, 0.0f, 0.0f, 1.0f); // tilt angle
			myTranslatef(0.0f, E, 0.0f);
			
	
			myPushMatrix();
			cylinderm = cylinderm + cylinderD;
			myRotatef(cylinderm, 0.0f, 1.0f, 0.0f);
			// rotating around the "earth"
			myTranslatef(M, 0.0f, 0.0f);
			myScalef(E / 8, E / 8, E / 8);
	
			// the earth: code moved -- draw after retrieving the light sources	for multiple viewports
			
			// send the material property to the vertex shader: red cylinder
			M_emission[0] = 1; // Material property: emission
			FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission);
			int colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
			M_emission[0] = 0.1f;
	
			myTransHomoVertex(tmp, cylinderC);
			// retrieve the center of the cylinder: light source position
			// send the light source positions to the vertex shader: done in display() for robot arm
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
	
			// restore emission color for the rest of drawing 
			cBuf = Buffers.newDirectFloatBuffer(M_emission);
			colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
			gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
	
			myPopMatrix();
			
			myPushMatrix();
			myScalef(WIDTH / 8, WIDTH / 8, WIDTH / 8);
	
			gl.glLineWidth(2);
			drawColorCoord(3, 3, 3);
			drawSphere(); // the earth: draw after retrieving the light sources
			
			// get the center of the earth
			myTransHomoVertex(tmp, earthC);
			myPopMatrix();
			
			myPushMatrix();
			myTranslatef(0, -2.7f*E/4, 0); 
			myScalef(E/2.7f, E/4, E/2.7f);
			myRotatef(90 * dg, 1.0f, 0.0f, 0.0f); // orient the cone
			// upload matrix
	
			drawCone(); // cone goes with the earth
			
			myPopMatrix();

		myPopMatrix();

		// for collision detection: change of direction of movement
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
	      } 
	}


	public void display(GLAutoDrawable glDrawable) {


		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(L1_diffuse);
		int colorLoc = gl.glGetUniformLocation(vfPrograms, "L1d");
		gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

		cBuf = Buffers.newDirectFloatBuffer(L2_diffuse);
		colorLoc = gl.glGetUniformLocation(vfPrograms, "L2d");
		gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

		cBuf = Buffers.newDirectFloatBuffer(L3_diffuse);
		colorLoc = gl.glGetUniformLocation(vfPrograms, "L3d");
		gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

		super.display(glDrawable);
	}

	public void init(GLAutoDrawable drawable) {

		gl = (GL4) drawable.getGL();
		String vShaderSource[], fShaderSource[];

		System.out.println("a) init: prepare shaders, VAO/VBO"); 
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource = readShaderSource("src/"+path+"/JOGL3_7_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/"+path+"/JOGL3_2_F.shader"); // read fragment shader
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

		// 4. specify drawing into only the back_buffer
		gl.glDrawBuffer(GL_BACK);

		// 5. Enable zbuffer and clear framebuffer and zbuffer
		gl.glEnable(GL_DEPTH_TEST);
	}

	
	public void drawSphere() {
		myPushMatrix();
		myRotatef(-45 * dg, 0, 1, 0);
		myRotatef(-135 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(-45 * dg, 0, 1, 0);
		myRotatef(135 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(45 * dg, 0, 1, 0);
		myRotatef(-135 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(45 * dg, 0, 1, 0);
		myRotatef(135 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(-45 * dg, 0, 1, 0);
		myRotatef(-45 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(-45 * dg, 0, 1, 0);
		myRotatef(45 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(45 * dg, 0, 1, 0);
		myRotatef(-45 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(45 * dg, 0, 1, 0);
		myRotatef(45 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.15f, 0.15f, 1f);
		drawCone();
		myPopMatrix();
		myPushMatrix();
		myRotatef(270 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.05f, 0.05f, 0.8f);
		drawCylinder();
		myPopMatrix();
		myPushMatrix();
		myRotatef(90 * dg, 1, 0, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.05f, 0.05f, 0.8f);
		drawCylinder();
		myPopMatrix();
		myPushMatrix();
		myRotatef(270 * dg, 0, 1, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.05f, 0.05f, 0.8f);
		drawCylinder();
		myPopMatrix();
		myPushMatrix();
		myRotatef(180 * dg, 0, 1, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.05f, 0.05f, 0.8f);
		drawCylinder();
		myPopMatrix();
		myPushMatrix();
		myRotatef(90 * dg, 0, 1, 0);
		myTranslatef(0, 0, 0.8f);
		myScalef(0.05f, 0.05f, 0.8f);
		drawCylinder();
		myPopMatrix();
		myPushMatrix();
		myTranslatef(0, 0, 0.8f);
		myScalef(0.05f, 0.05f, 0.8f);
		drawCylinder();
		myPopMatrix();
		super.drawSphere();
	}
	

	public static void main(String[] args) {
		new JOGL3_7_MoveLight();
	}

}
