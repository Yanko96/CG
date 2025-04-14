package ykong7_CS551;

// import related to read a text file
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import com.jogamp.opengl.*;


public class ykong7_1_1_PointVFfiles extends ykong7_1_0_Point {
		
	public void init(GLAutoDrawable drawable) {
		gl = (GL4) drawable.getGL();
		String vShaderSource[], fShaderSource[] ;
		
		System.out.println("a) init: prepare vertex shader and fragment shader from files.");

		// shader file conventions: Vertex and Fragment shaders

		// 7/26/2022: this is added for accommodating packages
		String path = this.getClass().getPackageName().replace(".", "/"); 
		System.out.println("	include package directory: " + path); 

		vShaderSource = readShaderSource("src/"+path+"/ykong7_1_1_PointVFfiles_V.shader"); // read vertex shader	
		fShaderSource = readShaderSource("src/"+path+"/ykong7_1_1_PointVFfiles_F.shader"); // read vertex shader			}
		
		initShaders(vShaderSource, fShaderSource);	

		// what is the background color? 
		gl.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
	}

	
	
	public String[] readShaderSource(String filename) { // read a shader file into an array
		Vector<String> lines = new Vector<String>(); // Vector object for storing shader program
		Scanner sc;
		
		try {
			sc = new Scanner(new File(filename)); //Scanner object for reading a shader program
		} catch (IOException e) {
			System.err.println("IOException reading file: " + e);
			return null;
		}
		while (sc.hasNext()) {
			lines.addElement(sc.nextLine());
		}
		String[] shaderProgram = new String[lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			shaderProgram[i] = (String) lines.elementAt(i) + "\n";
		}
		sc.close(); 
		return shaderProgram; // a string of shader programs
	}
	
	
	
	public static void main(String[] args) {
		new ykong7_1_1_PointVFfiles();
	}
}
