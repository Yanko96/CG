package ykong7_CS551;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import static com.jogamp.opengl.GL.*;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class ykong7_Project extends Frame implements GLEventListener {
    private static final long serialVersionUID = 1L;
    private GLCanvas canvas;
    private FPSAnimator animator;
    private GL4 gl;
    private int vfProgram;
    
    private int[] vao = new int[5]; // 0: circle, 1: points, 2: triangle, 3: initials, 4: characters
    private int[][] vbo = new int[5][2]; // position & color buffer
    
    // Constants
    private static final int NUM_CIRCLE_POINTS = 100;
    private static final int NUM_BOUNCING_POINTS = 50;
    private static final float CIRCLE_RADIUS = 0.8f;
    private static final float POINT_SPEED = 0.01f;
    private static final float ROTATION_SPEED = 0.02f;
    
    // Animation data
    private float triangleAngle = 0.0f;
    private float initialsAngle = 0.0f;
    
    // Object data
    private float[] circleVertices;
    private float[] circleColors;
    private float[] pointVertices;
    private float[] pointColors;
    private float[] pointVelocities;
    private float[] triangleVertices;
    private float[] triangleColors;
    private float[] initialVertices;
    private float[] initialColors;
    
    public ykong7_Project() {
        super("Circle Animation with Bouncing Points");
        
        GLProfile profile = GLProfile.getDefault();
        GLCapabilities capabilities = new GLCapabilities(profile);
        
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        
        setSize(800, 800);
        add(canvas);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (animator != null) {
                    animator.stop();
                }
                System.exit(0);
            }
        });
        
        animator = new FPSAnimator(canvas, 60);
        setVisible(true);
        animator.start();
    }

    private int genVAO() {
        int[] id = new int[1];
        gl.glGenVertexArrays(1, id, 0);
        return id[0];
    }
    
    private int genVBO() {
        int[] id = new int[1];
        gl.glGenBuffers(1, id, 0);
        return id[0];
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    
        vfProgram = initShaders();
    
        initializeCircle();
        initializePoints();
        initializeTriangle();
        initializeInitials();
    
        for (int i = 0; i < vao.length; i++) {
            vao[i] = genVAO();
            vbo[i][0] = genVBO();
            vbo[i][1] = genVBO();
        }
    
        setupCircleBuffers();
        setupPointBuffers();
        setupTriangleBuffers();
        setupInitialsBuffers();
    }
    
    private int initShaders() {
        int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        
        String[] vShaderSource = {
            "#version 410 \n",
            "layout (location = 0) in vec3 position; \n",
            "layout (location = 1) in vec3 color; \n",
            "out vec4 varyingColor; \n",
            "uniform mat4 mv_matrix; \n",
            "void main(void) { \n",
            "    gl_Position = mv_matrix * vec4(position, 1.0); \n",
            "    varyingColor = vec4(color, 1.0); \n",
            "} \n"
        };
        
        String[] fShaderSource = {
            "#version 410 \n",
            "in vec4 varyingColor; \n",
            "out vec4 color; \n",
            "void main(void) { \n",
            "    color = varyingColor; \n",
            "} \n"
        };
        
        gl.glShaderSource(vShader, vShaderSource.length, vShaderSource, null, 0);
        gl.glShaderSource(fShader, fShaderSource.length, fShaderSource, null, 0);
        
        gl.glCompileShader(vShader);
        gl.glCompileShader(fShader);
        
        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);
        
        return vfprogram;
    }
    
    private void initializeCircle() {
        circleVertices = new float[NUM_CIRCLE_POINTS * 3];
        circleColors = new float[NUM_CIRCLE_POINTS * 3];
        
        for (int i = 0; i < NUM_CIRCLE_POINTS; i++) {
            float angle = (float) (2.0f * Math.PI * i / NUM_CIRCLE_POINTS);
            circleVertices[i * 3] = (float) (CIRCLE_RADIUS * Math.cos(angle));
            circleVertices[i * 3 + 1] = (float) (CIRCLE_RADIUS * Math.sin(angle));
            circleVertices[i * 3 + 2] = 0.0f;
            
            circleColors[i * 3] = 0.0f;     // R
            circleColors[i * 3 + 1] = 1.0f;  // G
            circleColors[i * 3 + 2] = 1.0f;  // B
        }
    }
    
    private void initializePoints() {
        Random rand = new Random();
        pointVertices = new float[NUM_BOUNCING_POINTS * 3];
        pointColors = new float[NUM_BOUNCING_POINTS * 3];
        pointVelocities = new float[NUM_BOUNCING_POINTS * 2];
        
        for (int i = 0; i < NUM_BOUNCING_POINTS; i++) {
            float r = rand.nextFloat() * CIRCLE_RADIUS * 0.8f;
            float angle = rand.nextFloat() * (float)(2.0 * Math.PI);
            pointVertices[i * 3] = r * (float)Math.cos(angle);
            pointVertices[i * 3 + 1] = r * (float)Math.sin(angle);
            pointVertices[i * 3 + 2] = 0.0f;
            
            pointColors[i * 3] = rand.nextFloat();
            pointColors[i * 3 + 1] = rand.nextFloat();
            pointColors[i * 3 + 2] = rand.nextFloat();
            
            angle = rand.nextFloat() * (float)(2.0 * Math.PI);
            pointVelocities[i * 2] = POINT_SPEED * (float)Math.cos(angle);
            pointVelocities[i * 2 + 1] = POINT_SPEED * (float)Math.sin(angle);
        }
    }
    
    private void initializeTriangle() {
        triangleVertices = new float[3 * 3]; // 3 vertices, xyz each
        triangleColors = new float[3 * 3];   // 3 vertices, rgb each
        
        for (int i = 0; i < 3; i++) {
            triangleColors[i * 3] = 1.0f;      // R
            triangleColors[i * 3 + 1] = 1.0f;  // G
            triangleColors[i * 3 + 2] = 1.0f;  // B
        }
    }
    
    private void initializeInitials() {
        initialVertices = new float[] {
            // Y shape
            -0.05f, 0.1f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.05f, 0.1f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, -0.1f, 0.0f,
            
            // K shape
            -0.05f, 0.1f, 0.0f,
            -0.05f, -0.1f, 0.0f,
            -0.05f, 0.0f, 0.0f,
            0.05f, 0.1f, 0.0f,  
            -0.05f, 0.0f, 0.0f, 
            0.05f, -0.1f, 0.0f  
        };
        
        initialColors = new float[initialVertices.length];
        for (int i = 0; i < initialVertices.length / 3; i++) {
            initialColors[i * 3] = 1.0f;      // R
            initialColors[i * 3 + 1] = 0.0f;  // G
            initialColors[i * 3 + 2] = 0.0f;  // B
        }
    }
    
    private void setupCircleBuffers() {
        gl.glBindVertexArray(vao[0]);
    
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0][0]);
        FloatBuffer vb = Buffers.newDirectFloatBuffer(circleVertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vb.limit() * Float.BYTES, vb, GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
    
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0][1]);
        FloatBuffer cb = Buffers.newDirectFloatBuffer(circleColors);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, cb.limit() * Float.BYTES, cb, GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
    }
    
    private void setupPointBuffers() {
        gl.glBindVertexArray(vao[1]);
        
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1][0]);
        FloatBuffer pointBuf = Buffers.newDirectFloatBuffer(pointVertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, pointBuf.limit() * Float.BYTES, pointBuf, GL4.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1][1]);
        FloatBuffer pointColorBuf = Buffers.newDirectFloatBuffer(pointColors);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, pointColorBuf.limit() * Float.BYTES, pointColorBuf, GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
    }
    
    private void setupTriangleBuffers() {
        gl.glBindVertexArray(vao[2]);
        
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[2][0]);
        FloatBuffer triangleBuf = Buffers.newDirectFloatBuffer(triangleVertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, triangleBuf.limit() * Float.BYTES, triangleBuf, GL4.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[2][1]);
        FloatBuffer triangleColorBuf = Buffers.newDirectFloatBuffer(triangleColors);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, triangleColorBuf.limit() * Float.BYTES, triangleColorBuf, GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
    }
    
    private void setupInitialsBuffers() {
        gl.glBindVertexArray(vao[3]);
        
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[3][0]);
        FloatBuffer initialsBuf = Buffers.newDirectFloatBuffer(initialVertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, initialsBuf.limit() * Float.BYTES, initialsBuf, GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[3][1]);
        FloatBuffer initialsColorBuf = Buffers.newDirectFloatBuffer(initialColors);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, initialsColorBuf.limit() * Float.BYTES, initialsColorBuf, GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);
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
                pointVertices[i * 3] = CIRCLE_RADIUS * nx * 0.99f;
                pointVertices[i * 3 + 1] = CIRCLE_RADIUS * ny * 0.99f;
            }
        }
        
        // Update position buffer
        gl.glBindVertexArray(vao[1]);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1][0]);
        FloatBuffer buf = Buffers.newDirectFloatBuffer(pointVertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, buf.limit() * Float.BYTES, buf, GL4.GL_DYNAMIC_DRAW);
    }
    
    private void updateTriangle() {
        // Update triangle angle (clockwise)
        triangleAngle += ROTATION_SPEED;
        if (triangleAngle > 2 * Math.PI) {
            triangleAngle -= 2 * Math.PI;
        }
        
        // Calculate triangle vertices on the circle
        for (int i = 0; i < 3; i++) {
            float angle = triangleAngle + (float)(i * 2.0 * Math.PI / 3.0);
            triangleVertices[i * 3] = (float)(CIRCLE_RADIUS * Math.cos(angle));
            triangleVertices[i * 3 + 1] = (float)(CIRCLE_RADIUS * Math.sin(angle));
            triangleVertices[i * 3 + 2] = 0.0f;
        }
        
        gl.glBindVertexArray(vao[2]);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[2][0]);
        FloatBuffer buf = Buffers.newDirectFloatBuffer(triangleVertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, buf.limit() * Float.BYTES, buf, GL4.GL_DYNAMIC_DRAW);
    }
    
    private float[] createTranslationMatrix(float x, float y, float z) {
        float[] matrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            x, y, z, 1.0f
        };
        return matrix;
    }
    
    private float[] createRotationMatrix(float angle) {
        float cosAngle = (float)Math.cos(angle);
        float sinAngle = (float)Math.sin(angle);
        
        float[] matrix = {
            cosAngle, -sinAngle, 0.0f, 0.0f,
            sinAngle, cosAngle, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        };
        
        return matrix;
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT);
        gl.glUseProgram(vfProgram);
        
        // Identity matrix
        float[] identityMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        };
        
        int mvLoc = gl.glGetUniformLocation(vfProgram, "mv_matrix");
        
        // Step 1: Draw circle and bouncing points
        gl.glUniformMatrix4fv(mvLoc, 1, false, identityMatrix, 0);
        
        gl.glBindVertexArray(vao[0]);
        gl.glLineWidth(3.0f); 
        gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, NUM_CIRCLE_POINTS);

        
        updatePoints();
        gl.glBindVertexArray(vao[1]);
        gl.glPointSize(5.0f);
        gl.glDrawArrays(GL4.GL_POINTS, 0, NUM_BOUNCING_POINTS);
        
        // Step 2 & 3: Draw and rotate initials
        // Update initials angle (counter-clockwise)
        initialsAngle -= ROTATION_SPEED;
        if (initialsAngle < 0) {
            initialsAngle += 2 * Math.PI;
        }
        
        // Create two separate matrices for the two initials
        float[] yMatrix = createRotationMatrix(initialsAngle);
        yMatrix[12] = -0.2f; // Translate left
        
        float[] kMatrix = createRotationMatrix(initialsAngle);
        kMatrix[12] = 0.2f; // Translate right
        
        // Draw Y initial
        gl.glUniformMatrix4fv(mvLoc, 1, false, yMatrix, 0);
        gl.glBindVertexArray(vao[3]);
        gl.glLineWidth(2.0f);
        gl.glDrawArrays(GL4.GL_LINE_STRIP, 0, 5);
        
        // Draw K initial
        gl.glUniformMatrix4fv(mvLoc, 1, false, kMatrix, 0);
        gl.glDrawArrays(GL4.GL_LINES, 5, 6);
        
        // Step 4 & 5: Draw rotating triangle
        gl.glUniformMatrix4fv(mvLoc, 1, false, identityMatrix, 0);
        updateTriangle();
        
        gl.glBindVertexArray(vao[2]);
        gl.glLineWidth(3.0f); 
        gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, 3);
        
        // Step 6: Draw character labels at triangle vertices
        for (int i = 0; i < 3; i++) {
            // Get triangle vertex position
            float x = triangleVertices[i * 3];
            float y = triangleVertices[i * 3 + 1];
            
            // Create translation matrix for the character
            float[] translationMatrix = createTranslationMatrix(x, y, 0.0f);
            gl.glUniformMatrix4fv(mvLoc, 1, false, translationMatrix, 0);
            
            // Draw character using OpenGL 4 line primitives
            drawCharacter(i);
        }
    }
    
    private void drawCharacter(int index) {
        float[] vertices, colors;
        if (index == 0) {
            vertices = new float[] {
                -0.03f, -0.05f, 0.0f,
                 0.00f,  0.05f, 0.0f,
                 0.03f, -0.05f, 0.0f,
                -0.02f,  0.00f, 0.0f,
                 0.02f,  0.00f, 0.0f
            };
        } else if (index == 1) {
            vertices = new float[] {
                -0.03f, -0.05f, 0.0f,
                -0.03f,  0.05f, 0.0f,
                 0.01f,  0.05f, 0.0f,
                 0.03f,  0.02f, 0.0f,
                 0.01f,  0.00f, 0.0f,
                -0.03f,  0.00f, 0.0f,
                -0.03f,  0.00f, 0.0f,
                 0.01f,  0.00f, 0.0f,
                 0.03f, -0.02f, 0.0f,
                 0.01f, -0.05f, 0.0f,
                -0.03f, -0.05f, 0.0f
            };
        } else {
            vertices = new float[] {
                 0.03f,  0.03f, 0.0f,
                 0.01f,  0.05f, 0.0f,
                -0.02f,  0.03f, 0.0f,
                -0.03f,  0.00f, 0.0f,
                -0.02f, -0.03f, 0.0f,
                 0.01f, -0.05f, 0.0f,
                 0.03f, -0.03f, 0.0f
            };
        }
    
        colors = new float[vertices.length];
        for (int i = 0; i < colors.length / 3; i++) {
            colors[i * 3] = 1.0f;
            colors[i * 3 + 1] = 1.0f;
            colors[i * 3 + 2] = 0.0f;
        }
    
        gl.glBindVertexArray(vao[4]);
    
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[4][0]);
        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL4.GL_STREAM_DRAW);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
    
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[4][1]);
        FloatBuffer cBuf = Buffers.newDirectFloatBuffer(colors);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, cBuf.limit() * Float.BYTES, cBuf, GL4.GL_STREAM_DRAW);
        gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
    
        if (index == 0) {
            gl.glDrawArrays(GL4.GL_LINE_STRIP, 0, 3);
            gl.glDrawArrays(GL4.GL_LINES, 3, 2);
        } else if (index == 1) {
            gl.glDrawArrays(GL4.GL_LINE_STRIP, 0, 11);
        } else {
            gl.glDrawArrays(GL4.GL_LINE_STRIP, 0, 7);
        }
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        gl.glDeleteProgram(vfProgram);
        for (int i = 0; i < vao.length; i++) {
            gl.glDeleteVertexArrays(1, new int[]{vao[i]}, 0);
            gl.glDeleteBuffers(1, new int[]{vbo[i][0]}, 0);
            gl.glDeleteBuffers(1, new int[]{vbo[i][1]}, 0);
        }
    }
    
    public static void main(String[] args) {
        new ykong7_Project();
    }
}