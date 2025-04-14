


import com.jogamp.opengl.*;


public class JOGL2_3_Robot2d extends JOGL2_2_Reshape {
	
	protected static float dg = (float) Math.PI /180; 
	protected static float alpha=-40;
	protected static float beta=-40;
	protected static float gama=80;
	protected static float dalpha = 0.75f;
	protected static float dbeta = 0.75f;
	protected static float dgama = -1.5f;
	
	  // homogeneous coordinates
	static float O[] = {0, 0, 0, 1}, A[] = {300f/(float)WIDTH, 0, 0, 1};
	static float B[] = {450f/(float) WIDTH, 0, 0, 1}, C[] = {600f/(float)WIDTH, 0, 0, 1};

	  public void display(GLAutoDrawable glDrawable) {
		  float color[] = {0, 0, 0}; 

	    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

	    alpha += dalpha*dg;
	    beta += dbeta*dg;
	    gama += dgama*dg;

	    //gl.glColor3f(0, 1, 1);
	    color[0] = 1;  color[1] = 1;   color[2] = 1; 
	    uploadColor(color); 
	    transDrawArm1(alpha, beta, gama);

	    //gl.glColor3f(1, 1, 0);
	    color[0] = 1;  color[1] = 1;   color[2] = 0; 
	    uploadColor(color); 
	    transDrawArm2(-beta, -gama, alpha);

	    //gl.glColor3f(1, 0, 1);
	    color[0] = 1;  color[1] = 0;   color[2] = 1; 
	    uploadColor(color); 
	    transDrawArm3(gama, -alpha, -beta);
	  }


	  // Method I: 2D robot arm transformations
	  public void transDrawArm1(float a, float b, float g) {
	    float Af[] = new float[4];
	    float B1[] = new float[4];
	    float C1[] = new float[4];
	    float Bf[] = new float[4];
	    float C2[] = new float[4];
	    float Cf[] = new float[4];

	    myLoadIdentity();
	    myRotatef(a, 0, 0, 1);
	    myTransHomoVertex(A, Af);
	    myTransHomoVertex(B, B1);
	    myTransHomoVertex(C, C1);

	    drawArm(O, Af);

	    myLoadIdentity();
	    myTranslatef(Af[0], Af[1], 0);
	    myRotatef(b, 0, 0, 1);
	    myTranslatef(-Af[0], -Af[1], 0);
	    myTransHomoVertex(B1, Bf);
	    myTransHomoVertex(C1, C2);
	    drawArm(Af, Bf);

	    
	    myLoadIdentity();
	    myTranslatef(Bf[0], Bf[1], 0);
	    myRotatef(g, 0, 0, 1);
	    myTranslatef(-Bf[0], -Bf[1], 0);
	    myTransHomoVertex(C2, Cf);
	    drawArm(Bf, Cf);
	  }


	  // Method II: 2D robot arm transformations
	  public void transDrawArm2(float a, float b, float g) {
		  float color[] = {0, 0, 0}; 

	    myLoadIdentity();

	    myRotatef(a, 0, 0, 1);
	    
	    gl.glLineWidth(6);
	    color[0] = 1;  color[1] = 0;   color[2] = 0; 
	    uploadColor(color); 
	    transDrawArm(O, A);
	    
	    gl.glLineWidth(4);
	    color[0] = 0;  color[1] = 1;   color[2] = 0; 
	    uploadColor(color); 
	    myTranslatef(A[0], A[1], 0);
	    myRotatef(b, 0, 0, 1);
	    myTranslatef(-A[0], -A[1], 0);
	    transDrawArm(A, B);   
	    
	    gl.glLineWidth(2);
	    color[0] = 0;  color[1] = 0;   color[2] = 1; 
	    uploadColor(color); 
	    myTranslatef(B[0], B[1], 0);
	    myRotatef(g, 0, 0, 1);
	    myTranslatef(-B[0], -B[1], 0);
	    transDrawArm(B, C);
	    gl.glLineWidth(1);
	    
	  }


	  // Method III: 2D robot arm transformations
	  public void transDrawArm3(float a, float b, float g) {
	    float Af[] = new float[4];
	    float Bf[] = new float[4];
	    float Cf[] = new float[4];

	    myLoadIdentity();
	    myRotatef(a, 0, 0, 1);
	    myTransHomoVertex(A, Af);
	    drawArm(O, Af);
	    myLoadIdentity();
	    myTranslatef(Af[0], Af[1], 0);
	    myRotatef(a+b, 0, 0, 1);
	    myTranslatef(-A[0], -A[1], 0);
	    myTransHomoVertex(B, Bf);
	    drawArm(Af, Bf);
	    myLoadIdentity();
	    myTranslatef(Bf[0], Bf[1], 0);
	    myRotatef(a+b+g, 0, 0, 1);
	    myTranslatef(-B[0], -B[1], 0);
	    myTransHomoVertex(C, Cf);
	    drawArm(Bf, Cf);
	  }


	  // trasform the coordinates and then draw
	  protected void transDrawArm(float C[], float H[]) {

		    transDrawClock(C, H);
	  }


	  // draw the coordinates directly
	  public void drawArm(float C[], float H[]) {

		myLoadIdentity(); 
		transDrawClock(C, H);
	  }




  public static void main(String[] args) {
    new JOGL2_3_Robot2d();
  }

}
