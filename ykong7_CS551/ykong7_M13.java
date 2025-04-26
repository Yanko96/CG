package ykong7_CS551;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.*;

import java.io.File;
import java.nio.FloatBuffer;

public class ykong7_M13 extends JOGL4_3_Antialiasing {
    int fogTexture; 
    int fog1Texture; 
    int fog2Texture; 
    
    float[] fogMovementX = {0.0f, 0.0005f, -0.0005f, 0.0003f};
    float[] fogMovementY = {0.0f, 0.0003f, 0.0005f, -0.0005f};
    float[] fogOffsetX = {0.0f, 0.0f, 0.0f, 0.0f};
    float[] fogOffsetY = {0.0f, 0.0f, 0.0f, 0.0f};
    
    public Texture loadTexture(String textureFileName) {        
        Texture tex = null;                                    
        try { tex = TextureIO.newTexture(new File(textureFileName), false); }
        catch (Exception e) { e.printStackTrace(); }
        return tex;
    }
  
    public void display(GLAutoDrawable glDrawable) {
        for (int i = 0; i < 4; i++) {
            fogOffsetX[i] = (fogOffsetX[i] + fogMovementX[i]) % 1.0f;
            fogOffsetY[i] = (fogOffsetY[i] + fogMovementY[i]) % 1.0f;
            if (fogOffsetX[i] < 0) fogOffsetX[i] += 1.0f;
            if (fogOffsetY[i] < 0) fogOffsetY[i] += 1.0f;
        }
        cnt++; 
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawViewport(0, 0, WIDTH/2, HEIGHT/2, fogTexture, 0);
        drawViewport(WIDTH/2, 0, WIDTH/2, HEIGHT/2, fog1Texture, 1);
        drawViewport(0, HEIGHT/2, WIDTH/2, HEIGHT/2, fog2Texture, 2);
        drawViewport(WIDTH/2, HEIGHT/2, WIDTH/2, HEIGHT/2, fogTexture, 3);
    }
    
    private void drawViewport(int x, int y, int width, int height, int textureId, int viewportIndex) {
        gl.glViewport(x, y, width, height);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, textureId);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        int offsetXLoc = gl.glGetUniformLocation(vfPrograms, "fogOffsetX");
        gl.glProgramUniform1f(vfPrograms, offsetXLoc, fogOffsetX[viewportIndex]);
        int offsetYLoc = gl.glGetUniformLocation(vfPrograms, "fogOffsetY");
        gl.glProgramUniform1f(vfPrograms, offsetYLoc, fogOffsetY[viewportIndex]);
        myReshape2();
        drawBackground();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        myDisplay(viewportIndex);
    }
    
    public void myDisplay(int viewportIndex) {
        FloatBuffer cBuf = Buffers.newDirectFloatBuffer(M_emission);
        int colorLoc = gl.glGetUniformLocation(vfPrograms, "Me");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        cBuf = Buffers.newDirectFloatBuffer(L_ambient);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "La");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);

        cBuf = Buffers.newDirectFloatBuffer(M_ambient);
        colorLoc = gl.glGetUniformLocation(vfPrograms, "Ma");
        gl.glProgramUniform4fv(vfPrograms, colorLoc, 1, cBuf);
        float fogDensity = 0.0f;
        switch (viewportIndex) {
            case 0: fogDensity = 0.5f; break;  // Light fog
            case 1: fogDensity = 0.8f; break;  // Medium fog
            case 2: fogDensity = 0.3f; break;  // Very light fog
            case 3: fogDensity = 1.2f; break;  // Heavy fog
        }
        
        int fogDensityLoc = gl.glGetUniformLocation(vfPrograms, "fogDensityFactor");
        gl.glProgramUniform1f(vfPrograms, fogDensityLoc, fogDensity);
        
        myPushMatrix(); 
        if (cnt % 750 < 311) 
            myCamera(WIDTH/4, 2f*cnt*dg, WIDTH/6, spherem+sphereD);             
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        myPopMatrix();
    }
    
    public void drawBackground() {
        float tPoints[] = {
            -WIDTH, -HEIGHT, 0, 
            WIDTH, -HEIGHT, 0, 
            WIDTH, HEIGHT, 0,  
            
            -WIDTH, -HEIGHT, 0,  
            WIDTH, HEIGHT, 0, 
            -WIDTH, HEIGHT, 0
        };
        float tNormals[] = {0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f}; 
                    
        uploadMV();
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); 
        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(tPoints);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,
                vBuf, GL_STATIC_DRAW); 
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); 
        vBuf = Buffers.newDirectFloatBuffer(tNormals);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,
                vBuf, GL_STATIC_DRAW); 
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
                
        gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3);
    }
    
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) drawable.getGL();
        String vShaderSource[], fShaderSource[];
                    
        System.out.println("Init: preparing shaders, VAO/VBO, loading textures for modified fog");
        String path = this.getClass().getPackageName().replace(".", "/");

        vShaderSource = readShaderSource("src/"+path+"/ykong7_M13_V.shader");
        fShaderSource = readShaderSource("src/"+path+"/ykong7_M13_F.shader");
        vfPrograms = initShaders(vShaderSource, fShaderSource);        
        
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glDrawBuffer(GL_BACK);
        gl.glEnable(GL_DEPTH_TEST);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        Texture joglTexture = loadTexture("src/"+path+"/clouds.jpg");
        fogTexture = joglTexture.getTextureObject();
        joglTexture = loadTexture("src/"+path+"/clouds1.jpg");
        fog1Texture = joglTexture.getTextureObject();
        joglTexture = loadTexture("src/"+path+"/clouds2.jpg");
        fog2Texture = joglTexture.getTextureObject();

        gl.glActiveTexture(GL_TEXTURE0);
        
        int whLoc = gl.glGetUniformLocation(vfPrograms, "Height");
        gl.glProgramUniform1f(vfPrograms, whLoc, HEIGHT);
        whLoc = gl.glGetUniformLocation(vfPrograms, "Width");
        gl.glProgramUniform1f(vfPrograms, whLoc, WIDTH);
    }

    public static void main(String[] args) {
        new ykong7_M13();
    }
}