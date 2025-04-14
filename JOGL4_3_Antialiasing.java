




import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;



public class JOGL4_3_Antialiasing extends JOGL4_1_Blending {
 
	  public void display(GLAutoDrawable glDrawable) {
		    gl.glEnable(GL_BLEND);
		    gl.glBlendFunc(GL_SRC_ALPHA,
		                   GL_ONE_MINUS_SRC_ALPHA);
		    
		    gl.glEnable(GL_LINE_SMOOTH);
		    
		    gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		    
		    
		    super.display(glDrawable);
	  }
	  
	  public static void main(String[] args) {
	    new JOGL4_3_Antialiasing();
	  }

}
