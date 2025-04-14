package ykong7_CS551;



import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;

import com.jogamp.opengl.util.awt.TextRenderer; 
import java.awt.Font; 

public class JOGL1_4_3_xFontTextRenderer extends JOGL1_2_Animate  {

	TextRenderer renderer; 

	public void display(GLAutoDrawable drawable) {		
		// 1. draw into the back buffers
	    gl.glDrawBuffer(GL_BACK);

		// clear the display every frame
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame

		// pos goes from -1 to 1 back and forth
		pos += delta; 
		if (pos<=-1.0f) delta = 0.01f; 
		else if (pos>=1.0f) delta = -0.01f; 
			
		// generate a random triangle and display
		int v[] = new int[2]; // each vertex is v[i]

		// generate vertices
		v[0] = (int) (WIDTH *(1+pos)/4);
		v[1] = (int) (HEIGHT*(1+pos)/2);

		renderer.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		// optionally set the color
		renderer.setColor(1.0f, 0.5f, 0.5f, 0.8f);
		renderer.draw("TextRenderer class does not work with GLSL. ", v[0], v[1]);

		v[0] = (int) (WIDTH *(1+pos)/4);
		v[1] = (int) (HEIGHT *(1-pos)/2);
		renderer.draw("Need to work on this. ", v[0], v[1]);
		renderer.endRendering();

	}

	
	
	public void init(GLAutoDrawable drawable) {
			gl = (GL4) drawable.getGL();
		
		    renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 24));
			
	}
		
	
	public static void main(String[] args) {
		 new JOGL1_4_3_xFontTextRenderer();

	}
}


