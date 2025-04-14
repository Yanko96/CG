


import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;

public class JOGL2_0_2DTransform extends JOGL1_4_5_Circle {
  private static float myMatStack[][][] = new float[24][4][4]; // 24 layers for push and pop
  private static int stackPtr = 0;

  // called for OpenGL rendering every reshape
  public void display(GLAutoDrawable drawable) {
	  float color[] = {0, 0, 0};   
	 
	// use cnt as a control of transformation
	cnt = cnt+flip;
    if (cnt<1||cnt>200) {
      flip = -flip;
    }
 
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

    // white triangle is scaled
    color[0] = 1;  color[1] = 1;   color[2] = 1; 
    uploadColor(color); // send the color as uniform to the vertex shader 
    
    myLoadIdentity(); //M = I; 
    myScalef((float) cnt/200f, (float) cnt/200f, 1f); // M = M*S(x, y, 1)
    transDrawTriangle(cVdata[0], cVdata[1], cVdata[2]);

    // red triangle is rotated and scaled
    color[0] = 1;  color[1] = 0;   color[2] = 0; 
    uploadColor(color); // send the color as uniform to the vertex shader 
    
   myRotatef((float)cnt/30, 0, 0, 1f); // M = I*S(x, y, 1)*Rz(a)
   transDrawTriangle(cVdata[0], cVdata[1], cVdata[2]);

    // green triangle is translated, rotated, and scaled
    color[0] = 0;  color[1] = 1;   color[2] = 0; 	
    uploadColor(color); // send the color as uniform to the vertex shader 
   
    myTranslatef((float)cnt/100f, 0f, 0f);// S(x, y, 1)R(a)T(x, 0, 0)
    myRotatef((float)cnt/10, 0, 0, 1f); // M = I*S(x, y, 1)R(a)T(x,0,0)Rz(a1)
    transDrawTriangle(cVdata[0], cVdata[1], cVdata[2]);  
  }
  

  // the vertices are transformed first then drawn
  public void transDrawTriangle(float[] v1, float[] v2, float[] v3) {

	uploadMV(); // upload current matrix to MV matrix in the shaders
	
	// stores the three vertices to be sent to the vertex shader 
	float v[] = new float[9]; 
    for (int i=0; i<3; i++) v[i] = v1[i]; 
    for (int i=0; i<3; i++) v[i+3] = v2[i]; 
    for (int i=0; i<3; i++) v[i+6] = v3[i]; 


	// load vbo[0] with vertex data
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
	FloatBuffer vBuf = Buffers.newDirectFloatBuffer(v);
	gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
			vBuf, // the vertex positions
			GL_STATIC_DRAW); // the data is static
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
	
		
	// draw a triangle 
    gl.glDrawArrays(GL_TRIANGLES, 0, 3); 
  }
  
  
  public void uploadMV() 	{
	  float MV[] = new float [16];
	 
		getMatrix(MV); // get the modelview matrix from the matrix stack
		
		int mvLoc = gl.glGetUniformLocation(vfPrograms,  "mv_matrix"); 
		gl.glProgramUniformMatrix4fv(vfPrograms, mvLoc,  1,  false,  MV, 0);

  }

	public void init(GLAutoDrawable drawable) {
		
		gl = (GL4) drawable.getGL();
		String vShaderSource[], fShaderSource[] ;
		
		System.out.println("a) init: prepare shaders, VAO/VBO"); 

		String path = this.getClass().getPackageName().replace(".", "/"); 
		
		vShaderSource = readShaderSource("src/"+path+"/JOGL2_0_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/"+path+"/JOGL2_0_F.shader"); // read fragment shader
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

	}
	
  
  
  // initialize a matrix to all zeros
  private void myClearMatrix(float mat[][]) {

    for (int i = 0; i<4; i++) {
      for (int j = 0; j<4; j++) {
        mat[i][j] = 0.0f;
      }
    }
  }


  // initialize a matrix to Identity matrix
  private void myIdentity(float mat[][]) {

    myClearMatrix(mat);
    for (int i = 0; i<4; i++) {
      mat[i][i] = 1.0f;
    }
  }


  // initialize the current matrix to Identity matrix
  public void myLoadIdentity() {
    myIdentity(myMatStack[stackPtr]);
  }


  // multiply the current matrix with mat
  public void myMultMatrix(float mat[][]) {
    float matTmp[][] = new float[4][4];

    myClearMatrix(matTmp);

    for (int i = 0; i<4; i++) {
      for (int j = 0; j<4; j++) {
        for (int k = 0; k<4; k++) {
          matTmp[i][j] +=
              myMatStack[stackPtr][i][k]*mat[k][j];
        }
      }
    }
    // save the result on the current matrix
    for (int i = 0; i<4; i++) {
      for (int j = 0; j<4; j++) {
        myMatStack[stackPtr][i][j] = matTmp[i][j];
      }
    }
  }


  // multiply the current matrix with a translation matrix
  public void myTranslatef(float x, float y, float z) {
    float T[][] = new float[4][4];

    myIdentity(T);

    T[0][3] = x;
    T[1][3] = y;
    T[2][3] = z;

    myMultMatrix(T);
  }


  // multiply the current matrix with a rotation matrix
  public void myRotatef(float angle, float x, float y, float z) { // need to work on this one later
    float R[][] = new float[4][4];
    
    // normalize the vector: I notices a drifting effect in my implementation
    // if I am not rotating around a primary axis, it will drift to be larger or smaller
    x = x/(float) Math.sqrt(x*x + y*y + z*z); 
    y = y/(float) Math.sqrt(x*x + y*y + z*z); 
    z = z/(float) Math.sqrt(x*x + y*y + z*z); 
    
    float c = (float)Math.cos(angle); // gradian 
    float s = (float)Math.sin(angle);

    myIdentity(R);

    R[0][0] = x*x*(1-c) + c;	 R[0][1] = x*y*(1-c) - z*s;		R[0][2] = x*z*(1-c) + y*s; 
    R[1][0] = y*x*(1-c) + z*s;	 R[1][1] = y*y*(1-c) + c;		R[1][2] = y*z*(1-c) - x*s; 
    R[2][0] = z*x*(1-c) - y*s;	 R[2][1] = z*y*(1-c) + x*s;		R[2][2] = z*z*(1-c) + c; 
 
    myMultMatrix(R);
  }


  // multiply the current matrix with a scale matrix
  public void myScalef(float x, float y, float z) {
    float S[][] = new float[4][4];

    myIdentity(S);

    S[0][0] = x;
    S[1][1] = y;
    S[2][2] = z;

    myMultMatrix(S);
  }
  
  
  // v1 = (the current matrix) * v
  // here v and v1 are vertices in homogeneous coord.
  public void myTransHomoVertex(float v[], float v1[]) {
    int i, j;

    for (i = 0; i<4; i++) {
      v1[i] = 0.0f;

    }
    for (i = 0; i<4; i++) {
      for (j = 0; j<4; j++) {
        v1[i] +=
            myMatStack[stackPtr][i][j]*v[j];
      }
    }
  }




  // move the stack pointer up, and copy the previous
  // matrix to the current matrix
  public void myPushMatrix() {
    int tmp = stackPtr+1;

    for (int i = 0; i<4; i++) {
      for (int j = 0; j<4; j++) {
        myMatStack[tmp][i][j] =
            myMatStack[stackPtr][i][j];
      }
    }
    stackPtr++;
  }


  // move the stack pointer down
  public void myPopMatrix() {

    stackPtr--;
  }
  
  // return the current matrix on top of the matrix stack
  public void getMatrix(float M[]) {
		
		for (int i = 0; i < 4; i++ )
		for (int j = 0; j < 4; j++ ) 
			M[i*4+j] = myMatStack[stackPtr][j][i];
  }		




  public static void main(String[] args) {
    JOGL2_0_2DTransform f = new JOGL2_0_2DTransform();

    f.setTitle("JOGL JOGL2_0_2DTransform: my implementation of legacy OpenGL matrix operations");
   }
}


