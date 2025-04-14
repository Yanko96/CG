



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;

import java.io.File;
import java.nio.FloatBuffer;


public class JOGL4_5_Image extends JOGL4_3_Antialiasing {
	
	// used for texture objects
	int	gmuTexture; 
	
	// read a texture image
	public Texture loadTexture(String textureFileName) {		
		Texture	tex	=	null;									
		try	{	tex	=	TextureIO.newTexture(new	File(textureFileName),	false);	}
		catch	(Exception	e)	{	e.printStackTrace();	}
        return	tex;
	}


	public void init(GLAutoDrawable drawable) {
		
		gl = (GL4) drawable.getGL();
		String vShaderSource[], fShaderSource[] ;

		System.out.println("a) init: prepare shaders, VAO/VBO, read an image as texture"); 
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource = readShaderSource("src/"+path+"/JOGL4_5_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/"+path+"/JOGL4_5_F.shader"); // read fragment shader
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
					
		//4. specify drawing into only the back_buffer
		gl.glDrawBuffer(GL_BACK); 
		
		// 5. Enable zbuffer and clear framebuffer and zbuffer
		gl.glEnable(GL_DEPTH_TEST); 

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// read an image as texture
		Texture	joglTexture	=	loadTexture("src/"+path+"/gmu.jpg");		
		gmuTexture	=	joglTexture.getTextureObject(); 

		// activate texture #0 and bind it to the texture object
		gl.glActiveTexture(GL_TEXTURE0); // means the following texture commands are about TEXTURE0. 
		gl.glBindTexture(GL_TEXTURE_2D,	gmuTexture);
		
		//Width and Height are used to retrieve corresponding texel in the shader
		//Connect JOGL variable with shader variable by name
		int whLoc = gl.glGetUniformLocation(vfPrograms,  "Height"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  HEIGHT);

		//Connect JOGL variable with shader variable by name
		whLoc = gl.glGetUniformLocation(vfPrograms,  "Width"); 
		gl.glProgramUniform1f(vfPrograms,  whLoc,  WIDTH);
	}


	  public static void main(String[] args) {
	    new JOGL4_5_Image();
	  }

}
