package ykong7_CS551;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;
import java.util.Random;

public class ykong7_1_3_VertexArray extends ykong7_1_2_Animate {
    protected static int vao[] = new int[2]; // One for circle, one for points
    protected static int vbo[] = new int[4]; // Position and color for both circle and points
    
    private static final int NUM_CIRCLE_POINTS = 100;
    private static final int NUM_BOUNCING_POINTS = 20;
    private static final float CIRCLE_RADIUS = 0.8f;
    private static final float POINT_SPEED = 0.01f;
    
    private float[] circleVertices;
    private float[] circleColors;
    private float[] pointVertices;
    private float[] pointColors;
    private float[] pointVelocities;
    
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) drawable.getGL();
        
        // Shaders
        String vShaderSource[], fShaderSource[];
        String path = this.getClass().getPackageName().replace(".", "/");
        vShaderSource = readShaderSource("src/" + path + "/ykong7_1_3_VertexArray_V.shader");
        fShaderSource = readShaderSource("src/" + path + "/ykong7_1_3_VertexArray_F.shader");
        vfPrograms = initShaders(vShaderSource, fShaderSource);
        
        initializeCircle();
        initializePoints();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glGenBuffers(vbo.length, vbo, 0);
        setupCircleBuffers();
        setupPointBuffers();
    }
    
    private void initializeCircle() {
        circleVertices = new float[NUM_CIRCLE_POINTS * 3];
        circleColors = new float[NUM_CIRCLE_POINTS * 3];
        
        for (int i = 0; i < NUM_CIRCLE_POINTS; i++) {
            float angle = (float) (2.0f * Math.PI * i / NUM_CIRCLE_POINTS);
            circleVertices[i * 3] = (float) (CIRCLE_RADIUS * Math.cos(angle));
            circleVertices[i * 3 + 1] = (float) (CIRCLE_RADIUS * Math.sin(angle));
            circleVertices[i * 3 + 2] = 0.0f;
            
            circleColors[i * 3] = 1.0f;
            circleColors[i * 3 + 1] = 1.0f;
            circleColors[i * 3 + 2] = 1.0f;
        }
    }
    
    private void initializePoints() {
        Random rand = new Random();
        pointVertices = new float[NUM_BOUNCING_POINTS * 3];
        pointColors = new float[NUM_BOUNCING_POINTS * 3];
        pointVelocities = new float[NUM_BOUNCING_POINTS * 2];
        
        for (int i = 0; i < NUM_BOUNCING_POINTS; i++) {
            // Random position inside circle
            float r = rand.nextFloat() * CIRCLE_RADIUS;
            float angle = rand.nextFloat() * (float)(2.0 * Math.PI);
            pointVertices[i * 3] = r * (float)Math.cos(angle);
            pointVertices[i * 3 + 1] = r * (float)Math.sin(angle);
            pointVertices[i * 3 + 2] = 0.0f;
            
            // Random color
            pointColors[i * 3] = rand.nextFloat();
            pointColors[i * 3 + 1] = rand.nextFloat();
            pointColors[i * 3 + 2] = rand.nextFloat();
            
            // Random velocity
            angle = rand.nextFloat() * (float)(2.0 * Math.PI);
            pointVelocities[i * 2] = POINT_SPEED * (float)Math.cos(angle);
            pointVelocities[i * 2 + 1] = POINT_SPEED * (float)Math.sin(angle);
        }
    }
    
    private void setupCircleBuffers() {
        gl.glBindVertexArray(vao[0]);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer circleBuf = Buffers.newDirectFloatBuffer(circleVertices);
        gl.glBufferData(GL_ARRAY_BUFFER, circleBuf.limit() * Float.BYTES, circleBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer colorBuf = Buffers.newDirectFloatBuffer(circleColors);
        gl.glBufferData(GL_ARRAY_BUFFER, colorBuf.limit() * Float.BYTES, colorBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
    }
    
    private void setupPointBuffers() {
        gl.glBindVertexArray(vao[1]);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer pointBuf = Buffers.newDirectFloatBuffer(pointVertices);
        gl.glBufferData(GL_ARRAY_BUFFER, pointBuf.limit() * Float.BYTES, pointBuf, GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        FloatBuffer pointColorBuf = Buffers.newDirectFloatBuffer(pointColors);
        gl.glBufferData(GL_ARRAY_BUFFER, pointColorBuf.limit() * Float.BYTES, pointColorBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
    }
    
    private void updatePoints() {
        for (int i = 0; i < NUM_BOUNCING_POINTS; i++) {
            // Update position
            pointVertices[i * 3] += pointVelocities[i * 2];
            pointVertices[i * 3 + 1] += pointVelocities[i * 2 + 1];
            
            // Check for collision with circle
            float x = pointVertices[i * 3];
            float y = pointVertices[i * 3 + 1];
            float distanceFromCenter = (float)Math.sqrt(x * x + y * y);
            
            if (distanceFromCenter > CIRCLE_RADIUS) {
                // Calculate normal vector
                float nx = x / distanceFromCenter;
                float ny = y / distanceFromCenter;
                
                // Calculate dot product of velocity and normal
                float dot = pointVelocities[i * 2] * nx + pointVelocities[i * 2 + 1] * ny;
                
                // Reflect velocity around normal
                pointVelocities[i * 2] -= 2 * dot * nx;
                pointVelocities[i * 2 + 1] -= 2 * dot * ny;
                
                // Move point back inside circle
                pointVertices[i * 3] = CIRCLE_RADIUS * nx;
                pointVertices[i * 3 + 1] = CIRCLE_RADIUS * ny;
            }
        }
        
        // Update position buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer pointBuf = Buffers.newDirectFloatBuffer(pointVertices);
        gl.glBufferData(GL_ARRAY_BUFFER, pointBuf.limit() * Float.BYTES, pointBuf, GL_DYNAMIC_DRAW);
    }
    
    public void display(GLAutoDrawable drawable) {
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        gl.glBindVertexArray(vao[0]);
        gl.glDrawArrays(GL_LINE_LOOP, 0, NUM_CIRCLE_POINTS);
        
        updatePoints();
        gl.glBindVertexArray(vao[1]);
        gl.glPointSize(5.0f);
        gl.glDrawArrays(GL_POINTS, 0, NUM_BOUNCING_POINTS);
    }
    
    public static void main(String[] args) {
        new ykong7_1_3_VertexArray();
    }
}