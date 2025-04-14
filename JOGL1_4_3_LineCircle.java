

import static com.jogamp.opengl.GL2ES3.GL_COLOR;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

/*************************************************
 * Created on September 17, 2018, @author: Jim X. Chen
 *
 * Draw a circle using line segments
 * 
 * This is to implement the text book's example: J_1_3_LineCircle
 * 
 * The program use JOGL1_4_2_V.shader and JOGL1_4_2_F.shader
 */

import com.jogamp.opengl.*;

public class JOGL1_4_3_LineCircle extends JOGL1_4_3_Line {

	public void display(GLAutoDrawable drawable) {
		float color[] = {0.0f, 0.0f, 1.0f}; 

		// just use uniform color: use super's shaders, uniform or per-vertex color
	 	int colorLoc = gl.glGetUniformLocation(vfPrograms,  "uColorLine"); 
		gl.glProgramUniform1i(vfPrograms,  colorLoc, 1);
		
		// 3. generate a random color		
		color[0] = (float) Math.random();
		color[1] = (float) Math.random();
		color[2] = (float) Math.random();
		uploadColor(color);
		
		cnt++; 
		double r = (double) (cnt*10 % HEIGHT/2); 
		circle(WIDTH/2, HEIGHT/2, r); //two buffers different circles
		drawable.swapBuffers(); 
		circle(WIDTH/2, HEIGHT/2, r); //two buffers different circles
	}

	// Our circle algorithm: center (cx, cy); radius: r
	void circle(double cx, double cy, double r) {
		double xn, yn, theta = 0;
		double delta = 10 / r; // the delta angle for a line segment: 10 pixels
		double x0 = r * Math.cos(theta) + cx;
		double y0 = r * Math.sin(theta) + cy;

		while (theta < 2 * Math.PI) {
			theta = theta + delta;
			xn = r * Math.cos(theta) + cx;
			yn = r * Math.sin(theta) + cy;
			bresenhamLine((int) x0, (int) y0, (int) xn, (int) yn);
			x0 = xn;
			y0 = yn;
		}
	}

	public static void main(String[] args) {
		new JOGL1_4_3_LineCircle();

	}
}
