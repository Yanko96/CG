package ykong7_CS551;

import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL4.*;

public class ykong7_M10 extends JOGL2_15_LookAt {
    private float[] moonC = {0, 0, 0, 1};
    private float[] earthC = {0, 0, 0, 1};
    private float[] sunC = {0, 0, 0, 1};

    @Override
    public void display(GLAutoDrawable glDrawable) {
        cnt++;
        depth = (cnt / 150) % 6;
        if (cnt % 150 == 0) {
            dalpha = -dalpha;
            dbeta = -dbeta;
            dgama = -dgama;
        }
        alpha += dalpha;
        beta += dbeta;
        gama += dgama;

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (cnt % 1000 > 500) {
            gl.glViewport(0, 0, WIDTH, HEIGHT);
            myReshape2();
            if (cnt % 750 < 311) 
                myCamera(WIDTH/4, 2f*cnt*dg, WIDTH/6, spherem+sphereD);             
            drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        }
        else {
            setupFourViewports();
        }
    }
    
    private void setupFourViewports() {
        int w = WIDTH;
        int h = HEIGHT;
        
        gl.glViewport(0, h/2, w/2, h/2);
        myReshape1();
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        resetSolarSystem();
        
        gl.glViewport(w/2, h/2, w/2, h/2);
        myReshape2();
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        resetSolarSystem();
        
        gl.glViewport(0, 0, w/2, h/2);
        myLoadIdentity();
        myPerspective(40, (float)w/h, 10.0f, 3000.0f); 
        myLoadIdentity();
        
        mygluLookAt(
            300.0*6, 150.0*6, -150.0*6,  
            0.0, 0.0, -200.0,    
            0.0, 1.0, 0.0
        );
        
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        resetSolarSystem();
        
        gl.glViewport(w/2, 0, w/2, h/2);
        myLoadIdentity();
        myPerspective(40, (float)w/h, 10.0f, 3000.0f); 
        myLoadIdentity();
        
        mygluLookAt(
            0.0*6, 0.0*6, -300.0*6,  
            0.0, 0.0, 0.0,  
            0.0, 1.0, 0.0
        );
        
        drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
        resetSolarSystem();
    }
    
    private void resetSolarSystem() {
        spherem = spherem - sphereD;
        cylinderm = cylinderm - cylinderD;
        conem = conem - coneD;
    }
    
    @Override
    public void drawSolar(float E, float e, float M, float m) {
        float tiltAngle = 45 * dg;
        
        float[] tmp = {0, 0, 0, 1};
        
        myPushMatrix();
        tmp[0] = 0; tmp[1] = 0; tmp[2] = 0; tmp[3] = 1;
        myTransHomoVertex(tmp, sunC);
        myPopMatrix();
        
        drawSphere();
        drawColorCoord(WIDTH/4, WIDTH/4, WIDTH/4);
        
        myPushMatrix();
        myRotatef(e, 0.0f, 1.0f, 0.0f);
        myRotatef(tiltAngle, 0.0f, 0.0f, 1.0f);
        myTranslatef(0.0f, E, 0.0f);
        
        tmp[0] = 0; tmp[1] = 0; tmp[2] = 0; tmp[3] = 1;
        myTransHomoVertex(tmp, earthC);
        
        myPushMatrix();
        myScalef(WIDTH/8, WIDTH/8, WIDTH/8);
        drawSphere();
        drawColorCoord(3, 3, 3);
        myPopMatrix();
        
        myRotatef(m, 0.0f, 1.0f, 0.0f);
        myTranslatef(M, 0.0f, 0.0f);
        
        tmp[0] = 0; tmp[1] = 0; tmp[2] = 0; tmp[3] = 1;
        myTransHomoVertex(tmp, moonC);
        
        gl.glLineWidth(1);
        myScalef(WIDTH/40, WIDTH/40, WIDTH/40);
        myRotatef(cnt*dg, 0.0f, 1.0f, 0.0f);
        drawSphere();
        drawColorCoord(2, 2, 2);
        
        myPopMatrix();
    }
    
    public static void main(String[] args) {
        System.out.println("Starting ykong7_M10 application...");
        new ykong7_M10();
    }
}