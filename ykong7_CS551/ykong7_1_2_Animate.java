package ykong7_CS551;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;

public class ykong7_1_2_Animate extends ykong7_1_1_PointVFfiles {
    static FPSAnimator animator;
    protected static int vfPrograms;
    protected static int cnt = 0;
    float angle = 0.0f;
    float deltaAngle = 0.02f;  // Controls speed of rotation
    float radius = 0.5f;       // Controls size of circle
    
    public ykong7_1_2_Animate() {
        animator = new FPSAnimator(canvas, 60);  // 60 FPS for smoother animation
        animator.start();
        System.out.println("\nAnimator started: Drawing point moving in circle.");
    }
    
    public void init(GLAutoDrawable drawable) {
        String vShaderSource[], fShaderSource[];
        gl = (GL4) drawable.getGL();
        
        String path = this.getClass().getPackageName().replace(".", "/");
        
        vShaderSource = readShaderSource("src/" + path + "/ykong7_1_2_Animate_V.shader");
        fShaderSource = readShaderSource("src/" + path + "/ykong7_1_2_Animate_F.shader");
        
        vfPrograms = initShaders(vShaderSource, fShaderSource);
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        WIDTH = w;
        HEIGHT = h;
    }
    
    public void display(GLAutoDrawable drawable) {
        // Set black background
        // float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        // FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
        // gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer);
        
        // Calculate circle position using parametric equations
        float x = (float)(radius * Math.cos(angle));
        float y = (float)(radius * Math.sin(angle));
        
        // Update angle for next frame
        angle += deltaAngle;
        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;  // Keep angle in reasonable range
        }
        
        // Send position to shaders
        int xLoc = gl.glGetUniformLocation(vfPrograms, "xPos");
        int yLoc = gl.glGetUniformLocation(vfPrograms, "yPos");
        gl.glProgramUniform1f(vfPrograms, xLoc, x);
        gl.glProgramUniform1f(vfPrograms, yLoc, y);
        
        gl.glPointSize(10.0f);
        
        // Draw the point
        gl.glViewport(0, 0, WIDTH, HEIGHT);
        gl.glDrawArrays(GL_POINTS, 0, 1);
    }
    
    public void dispose(GLAutoDrawable drawable) {
        animator.stop();
        System.out.println("Animation stopped.");
    }
    
    public static void main(String[] args) {
        new ykong7_1_2_Animate();
    }
}