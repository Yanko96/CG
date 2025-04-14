

/*************************************************
 * Created on August 1, 2017, @author: Jim X. Chen
 *
 * JOGL initial set up: 
 * Open a frame with a background color: Cyan
 *
 *
 */

//import java.awt.Frame;

// 

// using frame or 

import java.awt.Frame; 
//import javax.swing.JFrame;

// window closing 
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.jogamp.opengl.GL4.GL_COLOR_BUFFER_BIT;
import com.jogamp.opengl.*; // access to opengl functions
import com.jogamp.opengl.awt.GLCanvas;



public class JOGL1_0_Frame extends Frame implements GLEventListener {
	protected static GLCanvas canvas; // drawable in a frame
	protected static GL4 gl; // handle to OpenGL functions
	protected static int WIDTH = 1280, HEIGHT = 1024; // used to set the window size

	public JOGL1_0_Frame() { 
		
		// 1. specify a drawable: canvas
		canvas = new GLCanvas();
		
		System.out.println("Hello, JOGL! JOGL1_0_Frame constructor initializes window and GLEventListener\n");

		// 2. listen to the events related to canvas: init, reshape, display, and dispose
		canvas.addGLEventListener(this); // "this" is the current instantiated object in main()
		this.add(canvas);

		// 3. set the size of the frame and make it visible
		this.setSize(WIDTH, HEIGHT);
		this.setVisible(true);

		//System.out.println("If your program runs very slow, do the following."); 
		//System.out.println("Select 'Run->Run Configurations', 'Arguments'; add the following lines under 'VM arguments': ");
		//System.out.println("-Djogamp.gluegen.UseTempJarCache=false");

		// 4. external window destroy event: dispose resources before exit
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose(); // perform the release of all OpenGL resources.
				System.exit(0);
			}
		});
	}

	
	public static void main(String[] args) {
		// 0. starts by instantiating a class, which calls the constructor
		new JOGL1_0_Frame();
		
		// constructor: 1, 2, 3, 4
		// 5. after the instantiation, the system gets into the event loop: init, reshape, display, and dispose
	}
	

	// called once for OpenGL initialization
	public void init(GLAutoDrawable drawable) {
		System.out.println("a) init is called once for initialization.");

		// 6. interface to OpenGL functions
	    gl = (GL4) drawable.getGL();
		
		// 7. specify background color
	    gl.glClearColor(0.0f, 1.0f, 1.0f, 1.0f); // Cyan
	}

	// called for handling drawing area when it is reshaped
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		
		System.out.println("b) reshape is called whenever the window is resized.");
	}

	
	// called for OpenGL rendering for every reshaping
	public void display(GLAutoDrawable drawable) {
		System.out.println("c) display is called for every reshape.");
	    
		// 8. clear the back-buffer into the background color
		gl.glClear(GL_COLOR_BUFFER_BIT);
		
 		//draw(); 
	}

	
	// For releasing of all OpenGL resources related to a drawable manually called
	// at the end 
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("d) dispose is called before exiting.");
	}
}
