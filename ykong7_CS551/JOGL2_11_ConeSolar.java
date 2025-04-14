package ykong7_CS551;




public class JOGL2_11_ConeSolar extends JOGL2_10_GenSolar {
  
	 void drawSolar(float E, float e, float M, float m) {
			float tiltAngle = 45*dg;

		    // Global coordinates
		    gl.glLineWidth(4);
		    //coordOff = false; // cjx
		    drawSphere(); // for loading matrix purpose
		    drawColorCoord(WIDTH/4, WIDTH/4, WIDTH/4);
		    
		    myPushMatrix();
		    myRotatef(e, 0.0f, 1.0f, 0.0f);
		    // rotating around the "sun"; proceed angle
		    myRotatef(tiltAngle, 0.0f, 0.0f, 1.0f); // tilt angle
		    myTranslatef(0.0f, E, 0.0f);
		    myPushMatrix();
		    myScalef(WIDTH/8, WIDTH/8, WIDTH/8);
		    drawSphere();

		    gl.glLineWidth(2);
		    drawColorCoord(3, 3, 3);
		    myPopMatrix();
		    
		    myPushMatrix();
		    myScalef(E/4, E, E/4);
		    myRotatef(90*dg, 1.0f, 0.0f, 0.0f); // orient the cone
		    drawCone();
		    myPopMatrix();

		    myRotatef(m, 0.0f, 1.0f, 0.0f);
		    // rotating around the "earth"
		    myTranslatef(M, 0.0f, 0.0f);
		    gl.glLineWidth(2);
		    myScalef(E/12, E/12, E/12);
		    myRotatef(cnt*dg, 0.0f, 1.0f, 0.0f); // self rotation
		    drawSphere();

		    gl.glLineWidth(1);
		    drawColorCoord(2, 2, 2);
		    myPopMatrix();
		  }

	  
	  
	  public static void main(String[] args) {
	    new JOGL2_11_ConeSolar();
	  }

}
