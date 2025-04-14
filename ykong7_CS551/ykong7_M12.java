package ykong7_CS551;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;

/**
 * This class implements Phong shading with diffuse and specular lighting components
 * in the fragment shader, particularly for cylinder objects.
 * @author leiming ykong7_M12
 */
public class ykong7_M12 extends JOGL3_7_MoveLight {
    
    // Matrix locations in shader
    private int mvMatrixLoc;
    private int projMatrixLoc;
    
    // Background polygon vertices and normals with Z pointing towards viewer
    private float[] bgVertices = {
        -5.0f, -5.0f, -2.0f,
        5.0f, -5.0f, -2.0f,
        5.0f, 5.0f, -2.0f,
        -5.0f, 5.0f, -2.0f
    };
    
    private float[] bgNormals = {
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
    };
    
    // Material properties for specular lighting
    float[] M_specular = {0.8f, 0.8f, 0.8f, 1.0f}; // Material specular property
    float M_shininess = 32.0f; // Material shininess property
    
    // Override drawCylinder method to implement Phong shading
    @Override
    public void drawCylinder() {
        int numofTriangle = 4 * (int)Math.pow(2, depth); // number of triangles after subdivision
        float vPoints[] = new float[3 * 3 * numofTriangle]; // 3 vertices each triangle, and 3 values each vertex
        float vNormals[] = new float[3 * 3 * numofTriangle]; // 3 vertices each triangle, and 3 values each vertex

        count = 0; // start filling triangle array to be sent to vertex shader

        // Use parent's subdivideCone method for cylinder subdivision
        subdivideCone(vPoints, vNormals, cVdata[0], cVdata[1], depth);
        subdivideCone(vPoints, vNormals, cVdata[1], cVdata[2], depth);
        subdivideCone(vPoints, vNormals, cVdata[2], cVdata[3], depth);
        subdivideCone(vPoints, vNormals, cVdata[3], cVdata[0], depth);
        
        // Upload current modelview matrix
        uploadMV();
        FloatBuffer mvBuf = Buffers.newDirectFloatBuffer(getMvMatrix());
        gl.glUniformMatrix4fv(mvMatrixLoc, 1, false, mvBuf);

        // Load vbo[0] with vertex data
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW); 
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // Load vbo[1] with normal data
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        vBuf = Buffers.newDirectFloatBuffer(vNormals);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW); 
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        
        // Draw front faces
        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);
        gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit() / 3); 

        // Reversing the normals for two-sided lighting
        for (int i = 0; i < 3 * 3 * numofTriangle; i++) {
            vNormals[i] = -vNormals[i];
        }
        
        // Update normal data for back faces
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        vBuf = Buffers.newDirectFloatBuffer(vNormals);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW); 
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
                
        // Draw back faces
        gl.glCullFace(GL_FRONT);
        gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit() / 3); 
        
        gl.glDisable(GL_CULL_FACE);
    }
    
    // Draw the background polygon
    public void drawBackground() {
        myPushMatrix(); // Save current matrix
        
        // Position the background behind the scene
        myLoadIdentity();
        myTranslatef(0.0f, 0.0f, -5.0f);
        
        // Upload current modelview matrix
        uploadMV();
        FloatBuffer mvBuf = Buffers.newDirectFloatBuffer(getMvMatrix());
        gl.glUniformMatrix4fv(mvMatrixLoc, 1, false, mvBuf);
        
        // Create vertices buffer
        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(bgVertices);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        // Create normals buffer
        FloatBuffer nBuf = Buffers.newDirectFloatBuffer(bgNormals);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glBufferData(GL_ARRAY_BUFFER, nBuf.limit() * Float.BYTES, nBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        
        // Draw the background as a quad
        gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        
        myPopMatrix(); // Restore previous matrix
    }
    
    // Override display method to update projection matrix and handle background
    @Override
    public void display(GLAutoDrawable glDrawable) {
        // First call the parent display method
        super.display(glDrawable);
        
        // Upload projection matrix to the shader
        FloatBuffer projBuf = Buffers.newDirectFloatBuffer(getProjection());
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projBuf);
        
        // Draw our background polygon
        drawBackground();
    }
    
    // Method to get the modelview matrix from parent class
    private float[] getMvMatrix() {
        return mv_matrix; // Assuming the parent class has this as a protected field
    }
    
    // Method to get the projection matrix from parent class
    private float[] getProjection() {
        return projection; // Assuming the parent class has this as a protected field
    }
    
    // Override init method to set up our Phong shaders
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) drawable.getGL();
        String vShaderSource[], fShaderSource[];
                
        System.out.println("Initializing Phong Shading..."); 
        String path = this.getClass().getPackageName().replace(".", "/"); 

        // Use our custom Phong vertex and fragment shaders
        vShaderSource = readShaderSource("src/"+path+"/ykong7_M12_V.shader");
        fShaderSource = readShaderSource("src/"+path+"/ykong7_M12_F.shader");
        vfPrograms = initShaders(vShaderSource, fShaderSource);
        
        // Set up VAO and VBO - using similar code as JOGL3_11_Phong
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        
        gl.glGenBuffers(vbo.length, vbo, 0);
        
        gl.glEnableVertexAttribArray(0); // enable vertex positions
        gl.glEnableVertexAttribArray(1); // enable vertex normals
        
        gl.glDrawBuffer(GL_BACK);
        gl.glEnable(GL_DEPTH_TEST);
    }
    
    public static void main(String[] args) {
        new ykong7_M12();
    }
}