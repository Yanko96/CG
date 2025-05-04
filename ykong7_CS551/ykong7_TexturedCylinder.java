package ykong7_CS551;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2.*;

import java.io.File;
import java.nio.FloatBuffer;

/**
 * This class extends the M13 assignment by adding texture mapping to cylinders.
 * It applies a custom texture to cylinders and animates the texture coordinates.
 * @author ykong7
 */
public class ykong7_TexturedCylinder extends ykong7_M13 {
    
    // Cylinder texture ID
    private int cylinderTexture;
    
    // Texture rotation animation variables
    private float textureRotation = 0.0f;
    private float rotationSpeed = 0.01f;
    
    // Texture scaling factors for more control
    private float texScaleS = 1.0f;
    private float texScaleT = 1.0f;

    /**
     * Initialize the rendering environment, load textures and shaders
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) drawable.getGL();
        String vShaderSource[], fShaderSource[];
        
        System.out.println("Init: preparing shaders and textures for Textured Cylinder");
        String path = this.getClass().getPackageName().replace(".", "/");

        try {
            // Load our custom texture shaders
            vShaderSource = readShaderSource("src/"+path+"/ykong7_TexturedCylinder_V.shader");
            fShaderSource = readShaderSource("src/"+path+"/ykong7_TexturedCylinder_F.shader");
            
            if (vShaderSource == null || fShaderSource == null) {
                System.out.println("Shader files not found, falling back to M13 shaders");
                // Fall back to parent init if shader files aren't found
                super.init(drawable);
            } else {
                System.out.println("Initializing custom shaders for texture support");
                // Initialize our custom shaders
                vfPrograms = initShaders(vShaderSource, fShaderSource);
                
                // Setup VAO/VBO
                gl.glGenVertexArrays(vao.length, vao, 0);
                gl.glBindVertexArray(vao[0]);
                gl.glGenBuffers(vbo.length, vbo, 0);
                
                // Enable vertex attributes
                gl.glEnableVertexAttribArray(0);  // position
                gl.glEnableVertexAttribArray(1);  // normal
                gl.glEnableVertexAttribArray(2);  // texture coordinates
                
                gl.glDrawBuffer(GL_BACK);
                gl.glEnable(GL_DEPTH_TEST);
                
                // Initialize basic M13 parameters
                gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                
                // Load fog textures from M13
                Texture joglTexture = loadTexture("src/"+path+"/clouds.jpg");
                fogTexture = joglTexture.getTextureObject();
                joglTexture = loadTexture("src/"+path+"/clouds1.jpg");
                fog1Texture = joglTexture.getTextureObject();
                joglTexture = loadTexture("src/"+path+"/clouds2.jpg");
                fog2Texture = joglTexture.getTextureObject();
                
                // Set viewport parameters
                int whLoc = gl.glGetUniformLocation(vfPrograms, "Height");
                gl.glProgramUniform1f(vfPrograms, whLoc, HEIGHT);
                whLoc = gl.glGetUniformLocation(vfPrograms, "Width");
                gl.glProgramUniform1f(vfPrograms, whLoc, WIDTH);
            }
            
            // Load cylinder texture
            try {
                System.out.println("Loading cylinder texture");
                Texture cylinderImg = loadTexture("src/"+path+"/flowerBump.jpg");
                cylinderTexture = cylinderImg.getTextureObject();
                
                // Get shader uniform locations for texture parameters
                gl.glUseProgram(vfPrograms);
                
                // Try to set texture uniforms - these might not exist in base shader
                try {
                    int texLoc = gl.glGetUniformLocation(vfPrograms, "cylinderTex");
                    if (texLoc >= 0) {
                        gl.glProgramUniform1i(vfPrograms, texLoc, 1);  // texture unit 1
                    }
                    
                    int useTexLoc = gl.glGetUniformLocation(vfPrograms, "useTexture");
                    if (useTexLoc >= 0) {
                        gl.glProgramUniform1i(vfPrograms, useTexLoc, 0);  // Initially disabled
                    }
                    
                    int texScaleSLoc = gl.glGetUniformLocation(vfPrograms, "texScaleS");
                    if (texScaleSLoc >= 0) {
                        gl.glProgramUniform1f(vfPrograms, texScaleSLoc, texScaleS);
                    }
                    
                    int texScaleTLoc = gl.glGetUniformLocation(vfPrograms, "texScaleT");
                    if (texScaleTLoc >= 0) {
                        gl.glProgramUniform1f(vfPrograms, texScaleTLoc, texScaleT);
                    }
                    
                    int texRotLoc = gl.glGetUniformLocation(vfPrograms, "textureRotation");
                    if (texRotLoc >= 0) {
                        gl.glProgramUniform1f(vfPrograms, texRotLoc, textureRotation);
                    }
                } catch (Exception e) {
                    System.out.println("Texture uniform setup error (non-fatal): " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Texture loading error (non-fatal): " + e.getMessage());
            }
            
            System.out.println("Textured Cylinder initialized successfully");
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
            
            // Fall back to parent init if anything fails
            super.init(drawable);
        }
    }

    /**
     * Override drawCylinder to add texture mapping
     */
    @Override
    public void drawCylinder() {
        try {
            float[] originalEmission = new float[4];
            gl.glGetUniformfv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Me"), originalEmission, 0);
            
            float[] brightEmission = {0.3f, 0.3f, 0.3f, 1.0f};
            gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Me"), 1, 
                                Buffers.newDirectFloatBuffer(brightEmission));
            
            float[] brightAmbient = {0.7f, 0.7f, 0.7f, 1.0f};
            gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Ma"), 1, 
                                Buffers.newDirectFloatBuffer(brightAmbient));
            // Only use texture if it was loaded successfully
            if (cylinderTexture > 0) {
                gl.glActiveTexture(GL_TEXTURE1);
                gl.glBindTexture(GL_TEXTURE_2D, cylinderTexture);
                
                // Set texture parameters
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                
                // Update texture rotation for animation
                textureRotation += rotationSpeed;
                if (textureRotation > 1.0f) {
                    textureRotation -= 1.0f;
                }
                
                // Pass texture parameters to shader if the uniforms exist
                int texRotLoc = gl.glGetUniformLocation(vfPrograms, "textureRotation");
                if (texRotLoc >= 0) {
                    gl.glUniform1f(texRotLoc, textureRotation);
                }
                
                int texScaleSLoc = gl.glGetUniformLocation(vfPrograms, "texScaleS");
                if (texScaleSLoc >= 0) {
                    gl.glUniform1f(texScaleSLoc, texScaleS);
                }
                
                int texScaleTLoc = gl.glGetUniformLocation(vfPrograms, "texScaleT");
                if (texScaleTLoc >= 0) {
                    gl.glUniform1f(texScaleTLoc, texScaleT);
                }
                
                // Enable texturing for this drawing
                int useTexLoc = gl.glGetUniformLocation(vfPrograms, "useTexture");
                if (useTexLoc >= 0) {
                    gl.glUniform1i(useTexLoc, 1);  // 1 means use texture
                }
            }
            
            // Call parent method to draw the cylinder geometry
            super.drawCylinder();
            
            // Disable texturing after drawing if the uniform exists
            if (cylinderTexture > 0) {
                int useTexLoc = gl.glGetUniformLocation(vfPrograms, "useTexture");
                if (useTexLoc >= 0) {
                    gl.glUniform1i(useTexLoc, 0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in drawCylinder: " + e.getMessage());
            // Call parent method to ensure something is drawn
            super.drawCylinder();
        }
    }
    
    /**
     * Override subdivideCylinder to generate texture coordinates for all vertices
     */
    @Override
    protected void subdivideCylinder(float vPoints[], float vNormals[], float v1[], float v2[], int depth) {
        try {
            // Store original vertex count before subdivision
            int originalCount = count;
            
            // Call parent method to generate basic geometry
            super.subdivideCylinder(vPoints, vNormals, v1, v2, depth);
            
            // Only generate texture coordinates if texture was loaded successfully
            if (cylinderTexture > 0) {
                // Calculate new vertices added by subdivision
                int vertexCount = (count - originalCount) / 3;  // 3 floats per vertex
                
                if (vertexCount > 0) {
                    float[] texCoords = new float[vertexCount * 2];  // 2 floats per texture coordinate (s,t)
                    
                    // Generate texture coordinates for each vertex
                    for (int i = 0; i < vertexCount; i++) {
                        int vertexIndex = originalCount + i * 3;
                        float x = vPoints[vertexIndex];
                        float y = vPoints[vertexIndex + 1];
                        float z = vPoints[vertexIndex + 2];
                        
                        // Calculate texture coordinates based on position and normal
                        float s, t;
                        if (Math.abs(vNormals[vertexIndex + 1]) > 0.9f) {
                            // Top or bottom face
                            s = (x + 1.0f) / 2.0f;
                            t = (z + 1.0f) / 2.0f;
                        } else {
                            // Side faces - cylindrical mapping
                            float angle = (float)Math.atan2(z, x);
                            s = angle / (2.0f * (float)Math.PI);
                            if (s < 0) s += 1.0f;
                            t = (y + 1.0f) / 2.0f;
                        }
                        
                        // Store texture coordinates
                        texCoords[i * 2] = s;
                        texCoords[i * 2 + 1] = t;
                    }
                    
                    // Send texture coordinates to shader via VBO
                    if (vbo.length > 2) {  // Make sure we have a VBO for texture coords
                        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
                        FloatBuffer texBuffer = Buffers.newDirectFloatBuffer(texCoords);
                        gl.glBufferData(GL_ARRAY_BUFFER, texBuffer.limit() * Float.BYTES, texBuffer, GL_STATIC_DRAW);
                        gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
                        gl.glEnableVertexAttribArray(2);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in subdivideCylinder: " + e.getMessage());
        }
    }
    
    /**
     * Main entry point for the application
     */
    public static void main(String[] args) {
        try {
            new ykong7_TexturedCylinder();
        } catch (Exception e) {
            System.err.println("Error in main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}