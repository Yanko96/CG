package ykong7_CS551;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.glu.GLU;
import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ykong7_M7 extends JFrame implements GLEventListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private GLCanvas canvas;
    private Animator animator;
    private GL2 gl;
    private GLU glu;
    
    private float angle = 0.0f;
    private boolean wireframeMode = false;
    private boolean depthTestEnabled = true;
    private int cullMode = GL_BACK; 
    
    public ykong7_M7() {
        setTitle("JOGL Cylinder");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        
        getContentPane().add(canvas);
        
        animator = new Animator(canvas);
        animator.start();
        
        setVisible(true);
        canvas.requestFocus();
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        System.out.println("OpenGL version: " + gl.glGetString(GL.GL_VERSION));
        
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        
        float[] lightAmbient = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] lightDiffuse = {0.7f, 0.7f, 0.7f, 1.0f};
        float[] lightSpecular = {0.5f, 0.5f, 0.5f, 1.0f};
        float[] lightPosition = {5.0f, 5.0f, 5.0f, 1.0f};
        
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular, 0);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
        
        gl.glShadeModel(GL_SMOOTH); 
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        gl.glEnable(GL_NORMALIZE);
        
        System.out.println("init complete");
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        animator.stop();
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        
        angle += 1.5f;
        
        if (depthTestEnabled) {
            gl.glEnable(GL.GL_DEPTH_TEST);
        } else {
            gl.glDisable(GL.GL_DEPTH_TEST);
        }
        
        if (cullMode == -1) {
            gl.glDisable(GL.GL_CULL_FACE);
        } else {
            gl.glEnable(GL.GL_CULL_FACE);
            gl.glCullFace(cullMode);
        }
        
        if (wireframeMode) {
            gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        
        drawCylinder();
    }
    
    private void drawCylinder() {
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(angle, 1.0f, 1.0f, 0.0f);
        
        float radius = 1.0f;
        float height = 2.0f;
        int slices = 30;
        
        float timeOffset = angle / 100.0f; 
        
        gl.glBegin(GL_QUAD_STRIP);
        for (int i = 0; i <= slices; i++) {
            float sliceAngle = (float) (2.0 * Math.PI * i / slices);
            float x = (float) (radius * Math.cos(sliceAngle));
            float z = (float) (radius * Math.sin(sliceAngle));
            
            gl.glNormal3f(x, 0.0f, z);
            
            float hue1 = (i / (float)slices + timeOffset) % 1.0f;
            float[] color1 = hsbToRgb(hue1, 1.0f, 1.0f);
            gl.glColor3f(color1[0], color1[1], color1[2]);
            
            gl.glVertex3f(x, height/2, z);
            
            float hue2 = ((i + slices/2) / (float)slices + timeOffset) % 1.0f;
            float[] color2 = hsbToRgb(hue2, 1.0f, 1.0f);
            gl.glColor3f(color2[0], color2[1], color2[2]);
            
            gl.glVertex3f(x, -height/2, z);
        }
        gl.glEnd();
        
        gl.glBegin(GL_TRIANGLE_FAN);
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        
        float[] centerColor = hsbToRgb((timeOffset * 2) % 1.0f, 0.8f, 1.0f);
        gl.glColor3f(centerColor[0], centerColor[1], centerColor[2]);
        gl.glVertex3f(0.0f, height/2, 0.0f);
        
        for (int i = 0; i <= slices; i++) {
            float sliceAngle = (float) (2.0 * Math.PI * i / slices);
            float x = (float) (radius * Math.cos(sliceAngle));
            float z = (float) (radius * Math.sin(sliceAngle));
            
            float hue = (i / (float)slices + timeOffset) % 1.0f;
            float[] color = hsbToRgb(hue, 1.0f, 1.0f);
            gl.glColor3f(color[0], color[1], color[2]);
            
            gl.glVertex3f(x, height/2, z);
        }
        gl.glEnd();
        
        gl.glBegin(GL_TRIANGLE_FAN);
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        
        centerColor = hsbToRgb((timeOffset * 2 + 0.5f) % 1.0f, 0.8f, 1.0f);
        gl.glColor3f(centerColor[0], centerColor[1], centerColor[2]);
        gl.glVertex3f(0.0f, -height/2, 0.0f); 
        
        for (int i = slices; i >= 0; i--) { 
            float sliceAngle = (float) (2.0 * Math.PI * i / slices);
            float x = (float) (radius * Math.cos(sliceAngle));
            float z = (float) (radius * Math.sin(sliceAngle));
            
            float hue = (i / (float)slices + timeOffset + 0.5f) % 1.0f;
            float[] color = hsbToRgb(hue, 1.0f, 1.0f);
            gl.glColor3f(color[0], color[1], color[2]);
            
            gl.glVertex3f(x, -height/2, z);
        }
        gl.glEnd();
    }
    
    private float[] hsbToRgb(float hue, float saturation, float brightness) {
        float[] rgb = new float[3];
        
        if (saturation == 0) {
            rgb[0] = rgb[1] = rgb[2] = brightness;
            return rgb;
        }
        
        float h = (hue - (float)Math.floor(hue)) * 6.0f;
        float f = h - (float)Math.floor(h);
        float p = brightness * (1.0f - saturation);
        float q = brightness * (1.0f - saturation * f);
        float t = brightness * (1.0f - (saturation * (1.0f - f)));
        
        switch ((int)h) {
            case 0:
                rgb[0] = brightness;
                rgb[1] = t;
                rgb[2] = p;
                break;
            case 1:
                rgb[0] = q;
                rgb[1] = brightness;
                rgb[2] = p;
                break;
            case 2:
                rgb[0] = p;
                rgb[1] = brightness;
                rgb[2] = t;
                break;
            case 3:
                rgb[0] = p;
                rgb[1] = q;
                rgb[2] = brightness;
                break;
            case 4:
                rgb[0] = t;
                rgb[1] = p;
                rgb[2] = brightness;
                break;
            case 5:
                rgb[0] = brightness;
                rgb[1] = p;
                rgb[2] = q;
                break;
        }
        
        return rgb;
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        
        if (height <= 0) {
            height = 1;
        }
        
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        float aspectRatio = (float) width / (float) height;
        glu.gluPerspective(45.0f, aspectRatio, 0.1f, 100.0f);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // not used
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w':
            case 'W':
                wireframeMode = !wireframeMode;
                System.out.println("wireframe mode: " + (wireframeMode ? "enabled" : "disabled"));
                break;
                
            case 's':
            case 'S':
                depthTestEnabled = !depthTestEnabled;
                System.out.println("depth-buffer: " + (depthTestEnabled ? "enabled" : "disabled"));
                break;
                
            case 'd':
            case 'D':
                if (cullMode == GL_BACK) {
                    cullMode = GL_FRONT;
                    System.out.println("front-face culling");
                } else if (cullMode == GL_FRONT) {
                    cullMode = -1;
                    System.out.println("no culling");
                } else {
                    cullMode = GL_BACK;
                    System.out.println("back-face culling");
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // not used
    }
    
    public static void main(String[] args) {
        new ykong7_M7();
    }
}