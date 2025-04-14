package ykong7_CS551;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL.*;
import java.nio.FloatBuffer;

public class ykong7_3_7 extends JOGL3_6_Materials {

    float[] Y_diffuse = {1.0f, 1.0f, 0.2f, 1.0f};
    float[] Y_specular = {1.0f, 1.0f, 0.5f, 1.0f};
    float[] Y_position = new float[4];
    
    float[] K_diffuse = {0.2f, 1.0f, 1.0f, 1.0f};
    float[] K_specular = {0.5f, 1.0f, 1.0f, 1.0f};
    float[] K_position = new float[4];
    
    float[] Global_diffuse = {0.7f, 0.7f, 0.7f, 1.0f};
    float[] Global_specular = {1.0f, 1.0f, 1.0f, 1.0f};
    float[] Global_position = {10.0f, 10.0f, 10.0f, 1.0f};
    float[] Global_ambient = {0.2f, 0.2f, 0.2f, 1.0f};
    
    float[] Material_ambient = {0.2f, 0.2f, 0.2f, 1.0f};
    float[] Material_diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
    float[] Material_specular = {1.0f, 1.0f, 1.0f, 1.0f};
    float Material_shininess = 64.0f;
    
    float[] Emission = {0.0f, 0.0f, 0.0f, 1.0f};
    
    float[] View_position = {0.0f, 0.0f, 5.0f, 1.0f};

    float Ym = 0, Km = 180;
    float Yd = 0.03f, Kd = -0.025f; 

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = (GL4) drawable.getGL();
        String vShaderSource[], fShaderSource[];

        System.out.println("a) init: prepare shaders, VAO/VBO");
        String path = this.getClass().getPackageName().replace(".", "/");

        vShaderSource = readShaderSource("src/" + path + "/JOGL3_7_V.shader");
        fShaderSource = readShaderSource("src/" + path + "/JOGL3_2_F.shader");
        vfPrograms = initShaders(vShaderSource, fShaderSource);

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);

        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glEnableVertexAttribArray(0); // position
        gl.glEnableVertexAttribArray(1); // normal

        gl.glDrawBuffer(GL_BACK);
        gl.glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        gl = (GL4) drawable.getGL();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(vfPrograms);
        
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "La"), 1, Buffers.newDirectFloatBuffer(Global_ambient));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Ld"), 1, Buffers.newDirectFloatBuffer(Global_diffuse));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Ls"), 1, Buffers.newDirectFloatBuffer(Global_specular));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Lsp"), 1, Buffers.newDirectFloatBuffer(Global_position));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Vsp"), 1, Buffers.newDirectFloatBuffer(View_position));
        
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Ma"), 1, Buffers.newDirectFloatBuffer(Material_ambient));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Md"), 1, Buffers.newDirectFloatBuffer(Material_diffuse));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Ms"), 1, Buffers.newDirectFloatBuffer(Material_specular));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Me"), 1, Buffers.newDirectFloatBuffer(Emission));
        gl.glProgramUniform1f(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Msh"), Material_shininess);

        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "L1d"), 1, Buffers.newDirectFloatBuffer(Y_diffuse));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "L2d"), 1, Buffers.newDirectFloatBuffer(K_diffuse));
        
        float[] dummy_light = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "L3d"), 1, Buffers.newDirectFloatBuffer(dummy_light));
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "L3sp"), 1, Buffers.newDirectFloatBuffer(dummy_light));

        super.display(drawable); 
    }

    @Override
    protected void drawSolar(float E, float e, float M, float m) {
        float tiltAngle = 45 * dg;
        float[] tmp = {0, 0, 0, 1};

        myPushMatrix();
        gl.glLineWidth(3);
        drawSphere(); // sun
        myTransHomoVertex(tmp, sunC);
        drawColorCoord(WIDTH / 5, WIDTH / 5, WIDTH / 5);

        myRotatef(e, 0.0f, 1.0f, 0.0f);
        myRotatef(tiltAngle, 0.0f, 0.0f, 1.0f);
        myTranslatef(0.0f, E, 0.0f);

        // === Y ===
        myPushMatrix();
        Ym += Yd;
        myRotatef(Ym, 0.0f, 1.0f, 0.0f);
        myTranslatef(M, 0, 0);
        myScalef(E / 8, E / 8, E / 8);
        
        Emission[0] = Y_diffuse[0];
        Emission[1] = Y_diffuse[1];
        Emission[2] = Y_diffuse[2];
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Me"), 1, Buffers.newDirectFloatBuffer(Emission));
        
        drawLetterY();
        
        myTransHomoVertex(tmp, Y_position);
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "L1sp"), 1, Buffers.newDirectFloatBuffer(Y_position));
        
        Emission[0] = 0.0f;
        Emission[1] = 0.0f;
        Emission[2] = 0.0f;
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Me"), 1, Buffers.newDirectFloatBuffer(Emission));
        
        myPopMatrix();

        // === K ===
        myPushMatrix();
        Km += Kd;
        myRotatef(Km, 0.0f, 1.0f, 0.0f);
        myTranslatef(M, 0, 0);
        myScalef(E / 8, E / 8, E / 8);
        
        Emission[0] = K_diffuse[0];
        Emission[1] = K_diffuse[1];
        Emission[2] = K_diffuse[2];
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Me"), 1, Buffers.newDirectFloatBuffer(Emission));
        
        drawLetterK();
        
        myTransHomoVertex(tmp, K_position);
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "L2sp"), 1, Buffers.newDirectFloatBuffer(K_position));

        Emission[0] = 0.0f;
        Emission[1] = 0.0f;
        Emission[2] = 0.0f;
        gl.glProgramUniform4fv(vfPrograms, gl.glGetUniformLocation(vfPrograms, "Me"), 1, Buffers.newDirectFloatBuffer(Emission));
        
        myPopMatrix();

        myPushMatrix();
        myScalef(WIDTH / 8, WIDTH / 8, WIDTH / 8);
        gl.glLineWidth(2);
        drawColorCoord(3, 3, 3);
        drawSphere();
        myTransHomoVertex(tmp, earthC);
        myPopMatrix();
        
        myPushMatrix();
        myTranslatef(0, -2.7f*E/4, 0); 
        myScalef(E/2.7f, E/4, E/2.7f);
        myRotatef(90 * dg, 1.0f, 0.0f, 0.0f);
        drawCone();
        myPopMatrix();

        myPopMatrix(); 
        
        if (distance(Y_position, K_position) < E/5) {
            float tmpD = Yd;
            Yd = Kd;
            Kd = tmpD;
            Ym += 2*Yd;
            Km += 2*Kd;
        }
    }

    private void drawLetterY() {
        float thickness = 0.08f;
    
        myPushMatrix();
        myTranslatef(-0.15f, 0.25f, 0); 
        myRotatef(-50 * dg, 0, 0, 1);   
        myScalef(thickness, 0.5f, thickness);
        drawCylinder();
        myPopMatrix();

        myPushMatrix();
        myTranslatef(0.15f, 0.25f, 0); 
        myRotatef(50 * dg, 0, 0, 1);   
        myScalef(thickness, 0.5f, thickness);
        drawCylinder();
        myPopMatrix();
    
        myPushMatrix();
        myTranslatef(0f, -0.15f, 0);   
        myScalef(thickness, 0.5f, thickness); 
        drawCylinder();
        myPopMatrix();
    }
    

    private void drawLetterK() {
        float thickness = 0.08f; 
        
        myPushMatrix();
        myTranslatef(-0.2f, 0, 0);
        myScalef(thickness, 1.0f, thickness);
        drawCylinder();
        myPopMatrix();

        myPushMatrix();
        myTranslatef(0.0f, 0.2f, 0);
        myRotatef(-40 * dg, 0, 0, 1);
        myScalef(thickness, 0.6f, thickness);
        drawCylinder();
        myPopMatrix();

        myPushMatrix();
        myTranslatef(0.0f, -0.2f, 0);
        myRotatef(40 * dg, 0, 0, 1);
        myScalef(thickness, 0.6f, thickness);
        drawCylinder();
        myPopMatrix();
    }

    public static void main(String[] args) {
        new ykong7_3_7();
    }
}