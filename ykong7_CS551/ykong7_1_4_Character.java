package ykong7_CS551;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;

public class ykong7_1_4_Character extends ykong7_1_3_VertexArray {
    
    private int currentChar = 0; // 0=Y, 1=K, 2=X
    private long lastChangeTime;
    private static final long DISPLAY_DURATION = 3000; 
    
    protected static int charVao[] = new int[1];
    protected static int charVbo[] = new int[1]; 
    
    private int charProgram;
    
    private int colorLoc;
    
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        String vShaderSource[], fShaderSource[];
        String path = this.getClass().getPackageName().replace(".", "/");
        
        vShaderSource = readShaderSource("src/" + path + "/ykong7_1_4_Character_V.shader");
        fShaderSource = readShaderSource("src/" + path + "/ykong7_1_4_Character_F.shader");
        charProgram = initShaders(vShaderSource, fShaderSource);
        colorLoc = gl.glGetUniformLocation(charProgram, "vColor");
        System.out.println("Color uniform location: " + colorLoc);
        gl.glGenVertexArrays(charVao.length, charVao, 0);
        gl.glGenBuffers(charVbo.length, charVbo, 0);
        
        lastChangeTime = System.currentTimeMillis();
        System.out.println("Character drawing initialized with custom shaders");
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClear(GL_COLOR_BUFFER_BIT);
        super.display(drawable);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChangeTime > DISPLAY_DURATION) {
            currentChar = (currentChar + 1) % 3;
            lastChangeTime = currentTime;
            System.out.println("Switching to character: " + 
                (currentChar == 0 ? "Y" : (currentChar == 1 ? "K" : "X")));
        }
        
        float[] color;
        switch (currentChar) {
            case 0: 
                color = new float[]{1.0f, 0.0f, 0.0f, 1.0f}; // Red for Y
                myCharacter('Y', color);
                break;
            case 1: 
                color = new float[]{0.0f, 1.0f, 0.0f, 1.0f}; // Green for K
                myCharacter('K', color);
                break;
            case 2: 
                color = new float[]{0.0f, 0.0f, 1.0f, 1.0f}; // Blue for X
                myCharacter('X', color);
                break;
        }
    }
    
    public void myCharacter(char character, float[] color) {
        gl.glUseProgram(charProgram);
        gl.glUniform4fv(colorLoc, 1, color, 0);
        gl.glBindVertexArray(charVao[0]);
        
        if (character == 'Y') {
            drawLine(new float[]{-0.5f, 0.5f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f});
            drawLine(new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.5f, 0.5f, 0.0f});
            drawLine(new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, -0.5f, 0.0f});
        } 
        else if (character == 'K') {
            drawLine(new float[]{-0.4f, 0.5f, 0.0f}, new float[]{-0.4f, -0.5f, 0.0f});
            drawLine(new float[]{-0.4f, 0.0f, 0.0f}, new float[]{0.4f, 0.5f, 0.0f});
            drawLine(new float[]{-0.4f, 0.0f, 0.0f}, new float[]{0.4f, -0.5f, 0.0f});
        } 
        else {
            drawLine(new float[]{-0.5f, 0.5f, 0.0f}, new float[]{0.5f, -0.5f, 0.0f});
            drawLine(new float[]{0.5f, 0.5f, 0.0f}, new float[]{-0.5f, -0.5f, 0.0f});
        }
        
        gl.glUseProgram(vfPrograms);
    }
    
    private void drawLine(float[] start, float[] end) {
        float[] lineVertices = {
            start[0], start[1], start[2],
            end[0], end[1], end[2]
        };
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, charVbo[0]);
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(lineVertices);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * Float.BYTES, vertBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glLineWidth(8.0f);
        gl.glDrawArrays(GL_LINES, 0, 2);
    }
    
    public static void main(String[] args) {
        new ykong7_1_4_Character();
        System.out.println("Drawing characters Y, K, and X with custom shaders");
    }
}