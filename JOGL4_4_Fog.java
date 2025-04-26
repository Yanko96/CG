package ykong7_CS551;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * This class implements a foggy environment with animated cloud textures.
 * It extends the previous ykong7_M12 class to add:
 * - Cloud textures as fog
 * - Animated texture coordinates
 * - Multiple viewports with different fog effects
 * 
 * @author [Your Name]
 */
public class ykong7_M13 extends ykong7_M12 {
    
    // Texture handles for cloud/fog images
    private int fogTexture;
    private int fog1Texture;
    private int fog2Texture;
    
    // Animation counter
    private int cnt = 0;
    
    // Texture coordinate locations in shader
    private int texCoordLoc;
    
    // Fullscreen quad for fog overlay
    private float[] fogQuadVertices = {
        -WIDTH, -HEIGHT, 0.0f,  // Bottom left
         WIDTH, -HEIGHT, 0.0f,  // Bottom right
         WIDTH,  HEIGHT, 0.0f,  // Top right
        
        -WIDTH, -HEIGHT, 0.0f,  // Bottom left
         WIDTH,  HEIGHT, 0.0f,  // Top right
        -WIDTH,  HEIGHT, 0.0f   // Top left
    };
    
    private float[] fogQuadNormals = {
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
    };
    
    private float[] fogQuadTexCoords = {
        0.0f, 0.0f,  // Bottom left
        1.0f, 0.0f,  // Bottom right
        1.0f, 1.0f,  // Top right
        
        0.0f, 0.0f,  // Bottom left
        1.0f, 1.0f,  // Top right
        0.0f, 1.0f   // Top left
    };
    
    // Method to load texture from file
    public Texture loadTexture(String textureFileName) {
        Texture tex = null;
        try {
            tex = TextureIO.newTexture(new File(textureFileName), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tex;
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) drawable.getGL();
        String vShaderSource[], fShaderSource[];
        
        System.out.println("Initializing Fog Environment..."); 
        String path = this.getClass().getPackageName().replace(".", "/"); 

        // Use our custom vertex and fragment shaders
        vShaderSource = readShaderSource("src/"+path+"/ykong7_M13_V.shader");
        fShaderSource = readShaderSource("src/"+path+"/ykong7_M13_F.shader");
        vfPrograms = initShaders(vShaderSource, fShaderSource);
        
        // Generate vertex arrays
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        
        // We need an additional VBO for texture coordinates
        // First, check how many VBOs we have in the parent class
        int parentVboCount = vbo.length;
        
        // Create a new VBO array with one more slot for texture coordinates
        int[] newVbo = new int[parentVboCount + 1];
        System.arraycopy(vbo, 0, newVbo, 0, parentVboCount);
        vbo = newVbo;
        
        // Generate all vertex buffers
        gl.glGenBuffers(vbo.length, vbo, 0);
        
        // Enable vertex attributes
        gl.glEnableVertexAttribArray(0); // position
        gl.glEnableVertexAttribArray(1); // normal
        gl.glEnableVertexAttribArray(2); // texture coordinate
        
        // Set up drawing parameters
        gl.glDrawBuffer(GL_BACK);
        gl.glEnable(GL_DEPTH_TEST);
        
        // Enable blending for fog effect
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Load fog textures
        Texture joglTexture = loadTexture("src/"+path+"/clouds.jpg");
        fogTexture = joglTexture.getTextureObject();
        
        joglTexture = loadTexture("src/"+path+"/clouds1.jpg");
        fog1Texture = joglTexture.getTextureObject();
        
        joglTexture = loadTexture("src/"+path+"/clouds2.jpg");
        fog2Texture = joglTexture.getTextureObject();
        
        // Set texture parameters
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, fogTexture);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        // Get texture sampler location in shader
        int texLoc = gl.glGetUniformLocation(vfPrograms, "textureMap");
        gl.glProgramUniform1i(vfPrograms, texLoc, 0); // Use texture unit 0
    }
    
    // Draw the fog overlay using cloud textures
    public void drawFogOverlay() {
        // Upload the current modelview matrix
        uploadMV();
        
        // Bind vertex position data
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(fogQuadVertices);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES, vBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        // Bind normal data
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        vBuf = Buffers.newDirectFloatBuffer(fogQuadNormals);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES, vBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        
        // Bind texture coordinate data
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        vBuf = Buffers.newDirectFloatBuffer(fogQuadTexCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES, vBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        
        // Draw the fog overlay
        gl.glDrawArrays(GL_TRIANGLES, 0, fogQuadVertices.length/3);
    }
    
    // Implementation of myDisplay method to handle scene rendering with fog
    public void myDisplay() {
        // Update depth based on counter
        depth = (cnt / 50) % 6;

        // Change rotation direction periodically
        if (cnt % 150 == 0) {
            dalpha = -dalpha;
            dbeta = -dbeta;
            dgama = -dgama;
        }
        
        // Update rotation angles
        alpha += dalpha;
        beta += dbeta;
        gama += dgama;

        // Send material properties to shader
        // Emission
        FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission);
        int colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Ambient light
        cBuf = Buffers.newDirectFloatBuffer(L_ambient);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "La");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Ambient material
        cBuf = Buffers.newDirectFloatBuffer(M_ambient);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Ma");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Diffuse light
        cBuf = Buffers.newDirectFloatBuffer(L_diffuse);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Ld");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Diffuse material
        cBuf = Buffers.newDirectFloatBuffer(M_diffuse);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Md");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Light position
        cBuf = Buffers.newDirectFloatBuffer(L_position);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Lsp");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Specular light
        cBuf = Buffers.newDirectFloatBuffer(L_specular);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Ls");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Specular material
        cBuf = Buffers.newDirectFloatBuffer(M_specular);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Ms");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Material shininess
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Msh");
        gl.glProgramUniform1f(vfPrograms, colorLoc, M_shininess);

        // View position
        cBuf = Buffers.newDirectFloatBuffer(V_position);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Vsp");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Additional light sources
        cBuf = Buffers.newDirectFloatBuffer(L1_diffuse);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "L1d");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        cBuf = Buffers.newDirectFloatBuffer(L2_diffuse);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "L2d");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        cBuf = Buffers.newDirectFloatBuffer(L3_diffuse);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "L3d");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        // Draw the robot with current rotation angles
        myPushMatrix();
        myCamera(WIDTH/4, 2f*cnt*dg, WIDTH/6, spherem+sphereD);
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        myPopMatrix();
    }
    
    @Override
    public void display(GLAutoDrawable glDrawable) {
        cnt++; // Increment animation counter
        
        gl = (GL4) glDrawable.getGL();
        
        // Pass animation counter to shader
        int cntLoc = gl.glGetUniformLocation(vfPrograms, "Cnt");
        gl.glProgramUniform1f(vfPrograms, cntLoc, (float) cnt);
        
        // Choose texture based on animation cycle
        if (cnt % 1453 < 547) {
            gl.glBindTexture(GL_TEXTURE_2D, fogTexture);
        } else if (cnt % 1453 < 1005) {
            gl.glBindTexture(GL_TEXTURE_2D, fog1Texture);
        } else {
            gl.glBindTexture(GL_TEXTURE_2D, fog2Texture);
        }
        
        // Set texture parameters
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        
        // Clear buffers
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Single or multiple viewports based on animation cycle
        if (cnt % 1000 < 500) {
            // Single viewport
            gl.glViewport(0, 0, WIDTH, HEIGHT);
            myReshape2(); // Use orthographic projection
            drawFogOverlay(); // Draw the fog background
            gl.glClear(GL_DEPTH_BUFFER_BIT); // Clear depth buffer
            super.display(glDrawable); // Draw the scene using parent's display method
        } else {
            // Multiple viewports with different fog effects
            
            // First viewport - top left
            gl.glViewport(0, HEIGHT/2, WIDTH/2, HEIGHT/2);
            myReshape2();
            
            // Bind first fog texture
            gl.glBindTexture(GL_TEXTURE_2D, fogTexture);
            drawFogOverlay();
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            viewPort1();
            
            // Second viewport - top right
            gl.glViewport(WIDTH/2, HEIGHT/2, WIDTH/2, HEIGHT/2);
            myReshape2();
            
            // Bind second fog texture
            gl.glBindTexture(GL_TEXTURE_2D, fog1Texture);
            // Update counter for different movement direction
            gl.glProgramUniform1f(vfPrograms, cntLoc, (float) -cnt);
            drawFogOverlay();
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            viewPort2();
            
            // Third viewport - bottom left
            gl.glViewport(0, 0, WIDTH/2, HEIGHT/2);
            myReshape2();
            
            // Bind third fog texture
            gl.glBindTexture(GL_TEXTURE_2D, fog2Texture);
            // Update counter for different movement direction
            gl.glProgramUniform1f(vfPrograms, cntLoc, (float) (cnt * 0.5f));
            drawFogOverlay();
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            viewPort3();
            
            // Fourth viewport - bottom right
            gl.glViewport(WIDTH/2, 0, WIDTH/2, HEIGHT/2);
            myReshape2();
            
            // Use a different texture again
            gl.glBindTexture(GL_TEXTURE_2D, fogTexture);
            // Update counter for different movement direction
            gl.glProgramUniform1f(vfPrograms, cntLoc, (float) (cnt * 1.5f));
            drawFogOverlay();
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            viewPort4();
        }
    }
    
    // Create viewport drawing methods for different views
    public void viewPort1() {
        myPushMatrix();
        myCamera(WIDTH/4, 0, WIDTH/6, spherem+sphereD);
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        myPopMatrix();
    }
    
    public void viewPort2() {
        myPushMatrix();
        myCamera(WIDTH/4, 90, WIDTH/6, spherem+sphereD);
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        myPopMatrix();
    }
    
    public void viewPort3() {
        myPushMatrix();
        myCamera(WIDTH/4, 180, WIDTH/6, spherem+sphereD);
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        myPopMatrix();
    }
    
    public void viewPort4() {
        myPushMatrix();
        myCamera(WIDTH/4, 270, WIDTH/6, spherem+sphereD);
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        myPopMatrix();
    }
    
    public static void main(String[] args) {
        new ykong7_M13();
    }
}