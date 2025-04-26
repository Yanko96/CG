package ykong7_CS551;


import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_POINTS;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLAutoDrawable;

public class JOGL3_12_rayTracing extends JOGL3_11_Phong {

	static int MAX = 20; // maximum number of spheres or light sources	
	float[][] sphere = new float[MAX][4]; // spheres with radius and center
	float[][] cylinder = new float[MAX/2][5]; // horizontal cylinder: (y-yc)^2 + (z-zc)^2 = r^2; x0<x<x1 
	float[][] cone = new float[MAX/2][4]; // vertical cone: (x-xc)^2 + (z-zc)^2 = (r-y)^2; y0<y<r; 
	static int ns; // number of spheres
	static int ny; // number of cylinders
	static int nc; // number of cones

	float[][] lightSrc = new float[MAX][3]; // light sources
	float[][] lightClr = new float[MAX][3]; // light sources color
	static int nl; // number of light sources

	static int depth = 5; // depth of ray tracing recursion
	static int yplane = -HEIGHT / 4; // a reflective plane
	static int zplane = -2*HEIGHT; // a reflective plane

    // replace with a perspective projection
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {

	    gl.glViewport(0, 0, w, h);
		WIDTH = w; HEIGHT = h;
		 
		System.out.println("b) reshape: myOrtho for raytracing"); 

		    //projection is carried on the projection matrix
		myLoadIdentity();
		myOrtho(-WIDTH / 2, WIDTH / 2, -HEIGHT / 2, HEIGHT / 2, -4 * HEIGHT, 4 * HEIGHT); 		    
	}


	public void display(GLAutoDrawable glDrawable) {
		float[] viewpt = new float[3], raypt = new float[3];
		// initial ray: viewpt -> raypt

		float[] color = {0, 0, 0}; // traced color

		ns = (int) (MAX*Math.random()); // random number of spheres
		ny = (int) (MAX*Math.random()/2); // random number of cylinders
		nc = (int) (MAX*Math.random()/2); // random number of cones
		nl = (int) (MAX*Math.random()); // random number of light sources
		
		// initialize 'ns' number of spheres
		for (int i = 0; i < ns; i++) {
			sphere[i][0] = 5 + (float) (Math.random() * WIDTH)/8; // sphere radius
			for (int j = 1; j < 4; j++) { //sphere center
				sphere[i][j] = -WIDTH/2 +(float) (Math.random() * WIDTH); 
			}
		}
		// initialize 'ny' number of cylinders
		for (int i = 0; i < ny; i++) { //horizontal cylinder: (y-yc)^2 + (z-zc)^2 = r^2; x0<x<x1 
			cylinder[i][0] = 5 + (float) (Math.random() * WIDTH)/15; // Cylinder radius
			for (int j = 1; j < 3; j++) { // yc, zc (center), x0, x1 (two ends)
				cylinder[i][j] = -WIDTH/2 +(float) (Math.random() * WIDTH); 
			}
			// x0, x1 (two ends)
			cylinder[i][3] = -WIDTH/2 +(float) (Math.random() * WIDTH); 
			cylinder[i][4] = cylinder[i][3] +(float) (Math.random() * WIDTH/4); 
			

		}
		// initialize 'nc' number of cones
		for (int i = 0; i < nc; i++) { // vertical cone: (x-xc)^2 + (z-zc)^2 = (r-y)^2; y0<y<r; 
			cone[i][0] = 5 + (float) (Math.random() * WIDTH)/8; // cone radius
			for (int j = 1; j < 4; j++) { //cone: xc, zc (center), and y0 (base y= r-y0) 
				cone[i][j] = -WIDTH/2 +(float) (Math.random() * WIDTH); 
			}
		}

		// initialize 'nl' light source locations
		for (int i = 0; i < nl; i++) {
			for (int j = 0; j < 3; j++) { // light source positions
				lightSrc[i][j] = -1000*WIDTH + (float) (2000*Math.random()*WIDTH); 
				lightClr[i][j] = (float)((Math.random() * WIDTH)/WIDTH); 
			}
		}

		// starting viewpoint on positive z axis
		viewpt[0] = 0;
		viewpt[1] = 0;
		viewpt[2] = 1.5f*HEIGHT;

		// trace rays against the spheres and a plane
		for (float y = -HEIGHT / 2; y < HEIGHT / 2; y++) {
			for (float x = -WIDTH / 2; x < WIDTH / 2; x++) {

				// ray from viewpoint to a pixel on the screen
				raypt[0] = x;
				raypt[1] = y;
				raypt[2] = 0;

				// tracing the ray (viewpt to raypt) for depth bounces
				rayTracing(color, viewpt, raypt, depth);

				// send vertex data to vertex shader through uniform
		 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(raypt);
				int colorLoc = gl.glGetUniformLocation(vfPrograms,  "rPoint"); 
				gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
				
				// send color data to fragment shader through uniform
		 		 cBuf = Buffers.newDirectFloatBuffer(color);
				 colorLoc = gl.glGetUniformLocation(vfPrograms,  "rColor"); 
				gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
				
				//draw the point
				gl.glDrawArrays(GL_POINTS, 0, 1);	
			}
		}
	}

	
	// recursive rayTracing from vpt to rpt for depth bounces, finding final color
	public void rayTracing(float[] color, float[] vpt, float[] rpt, int depth) {

		float[] reflectClr = new float[3], transmitClr = new float[3];
		float[] rpoint = new float[3]; // a point on ray direction
		float[] tpoint = new float[3]; // a point on ray direction
		float[] rD = new float[3]; // ray direction
		float[] vD = new float[3]; // view direction
		float[] n = new float[3]; // normal
		float[] p = new float[3]; // intersection point

		for (int i = 0; i < 3; i++) {
			color[i] = 0.0f;
		}
		
		if (depth > 0)  {// calculate color

			// find intersection of ray from vpt to rpt
			// with the closest object or the background
			intersect(vpt, rpt, p, n); // intersect at p with normal n

			// calculate lighting of the intersection point
			if (n[0] * n[0] + n[1] * n[1] + n[2] * n[2] > 0.001) {

				// view direction vector for lighting and reflection
				for (int i = 0; i < 3; i++) {
					vD[i] = vpt[i] - rpt[i];
				}

				normalize(n);
				normalize(vD);

				// calculate color using Phong shading
				phong(color, p, vD, n);

				// reflected ray
				reflect(vD, n, rD);

				for (int i = 0; i < 3; i++) {
					// a point on the reflected ray starting from p
					rpoint[i] = rD[i] + p[i];
					tpoint[i] = -vD[i] + p[i] - n[i]*0.51f;
				}
				
				// recursion to find a bounce at lower level
				rayTracing(reflectClr, p, rpoint, depth - 1);

				// let's say the yPlane is transparent 
				if (n[1] == 1) // normal 
					rayTracing(transmitClr, p, tpoint, depth - 1);
				
				for (int i = 0; i < 3; i++) {
					color[i] = (color[i] + 0.9f*reflectClr[i] + 0.3f*transmitClr[i]);
					if (color[i] > 1) //color values are not normalized. 
						color[i] = 1; 
				}
			}
			else { // calculate if it hits any light sources
				float[] lD = new float[3]; // lightSrc direction

				for (int i = 0; i < 3; i++) {
					rD[i] = rpt[i] - vpt[i];
				}
				normalize(rD);
				
				for (int i=0; i<nl; i++) {
					for (int j = 0; j < 3; j++) {
						lD[j] = lightSrc[i][j] - vpt[j];
					}
					normalize(lD);
					float Lsrc; 
					Lsrc=dotprod(rD, lD); 
					Lsrc = (float) Math.pow(Lsrc, 120); 
					for (int j = 0; j < 3; j++) {
						color[j] = color[j] + lightClr[i][j]*Lsrc/nl;
					}
				}
			}
			
		}
	}
	

	public void phong(float[] color, float[] point, float[] vD, float[] n) {
		float[] s = new float[3]; // light source direction + view direction
		float[] lgtsd = new float[3]; // light source direction
		float[] inormal = new float[3]; // for shadow
		float[] ipoint = new float[3]; // for shadow

		for (int i = 0; i < nl; i++) {

			// if intersect objects between light source, point in shadow
			intersect(point, lightSrc[i], ipoint, inormal);

			if (inormal[0] * inormal[0] + inormal[1] * inormal[1] + inormal[2]
					* inormal[2] < 0.001) { // point not in shadow

				for (int j = 0; j < 3; j++) {
					lgtsd[j] = lightSrc[i][j] - point[j];
					// light source direction
				}
				normalize(lgtsd);
				for (int j = 0; j < 3; j++) {
					s[j] = lgtsd[j] + vD[j]; // for specular term
				}
				normalize(s);

				float diffuse = dotprod(lgtsd, n);
				float specular = (float) Math.pow(dotprod(s, n), 120);

				if (diffuse < 0)
					diffuse = 0;
				if (specular < 0)
					specular = 0;
				// 3 color channels nl light sources -- may overload the color 
				color[0] = color[0] + (0.1f * diffuse + specular)*lightClr[i][0]; 
				color[1] = color[1] + (0.1f * diffuse + specular)*lightClr[i][1];
				color[2] = color[2] + (0.1f * diffuse + specular)*lightClr[i][2];
			}
		}
	}

	public void intersect(float[] vpt, float[] rpt, float[] point,
			float[] normal) {

		// calculate intersection of ray with the closest sphere
		// Ray equation:
		// x/y/z = vpt + t*(rpt - vpt);
		// Sphere equation:
		// (x-cx)^2 + (y-cy)^2 + (z-cz)^2 = r^2;
		// We can solve quadratic formula for t and find the intersection
		// t has to be > 0 to intersect with an object
		float t = 0;
		float a, b, c, d, e, f; // temp for solving the intersection

		normal[0] = 0;
		normal[1] = 0;
		normal[2] = 0;

		for (int i = 0; i < ns; i++) { // for each sphere
			a = vpt[0] - sphere[i][1];
			b = rpt[0] - vpt[0];
			c = vpt[1] - sphere[i][2];
			d = rpt[1] - vpt[1];
			e = vpt[2] - sphere[i][3];
			f = rpt[2] - vpt[2];

			float A = b * b + d * d + f * f;
			float B = 2 * (a * b + c * d + e * f);
			float C = a * a + c * c + e * e - sphere[i][0] * sphere[i][0];

			float answers[] = new float[2];

			if (quadraticFormula(A,B,C,answers)) {// intersection
				if (answers[0] < answers[1])
					t = answers[0];
				else t = answers[1]; 
				if (t < 0.001) {
					t = 0; 
					break;
				}
				else {
					// return point and normal
					point[0] = vpt[0] + t * (rpt[0] - vpt[0]);
					point[1] = vpt[1] + t * (rpt[1] - vpt[1]);
					point[2] = vpt[2] + t * (rpt[2] - vpt[2]);
					normal[0] = point[0] - sphere[i][1];
					normal[1] = point[1] - sphere[i][2];
					normal[2] = point[2] - sphere[i][3];
				}
			}
		}
		
		// calculate intersection of ray with the closest Cylinder
		// Ray equation:
		// y/z = vpt + t*(rpt - vpt);
		// Cylinder equation:
		// horizontal cylinder: (y-yc)^2 + (z-zc)^2 = r^2; x0<x<x1 
		// We can solve quadratic formula for t and find the intersection
		// t has to be > 0 to intersect with an object

		if (t==0) // not hitting a sphere
		for (int i = 0; i < ny; i++) { // for each cylinder
			a = vpt[1] - cylinder[i][1]; // y component with t
			b = rpt[1] - vpt[1]; // y component
			c = vpt[2] - cylinder[i][2]; // z component
			d = rpt[2] - vpt[2]; // z component with t

			float A = b * b + d * d;
			float B = 2 * (a * b + c * d);
			float C = a * a + c * c  - cylinder[i][0] * cylinder[i][0];

			float answers[] = new float[2];

			if (quadraticFormula(A,B,C,answers)) {// intersection
				if (answers[0] < answers[1]) {
					t = answers[0];
				}
				else {  t = answers[1]; }
				
				if (t < 0.001) {
					t = 0; 
					break;
				}
				else {
					// return point and normal
					point[0] = vpt[0] + t * (rpt[0] - vpt[0]);
					point[1] = vpt[1] + t * (rpt[1] - vpt[1]);
					point[2] = vpt[2] + t * (rpt[2] - vpt[2]);
					
					if (point[0]<cylinder[i][3] || point[0]>cylinder[i][4]) {
						t=0; 
						break; // outside cylinder
					}
					
					normal[0] = 0;
					normal[1] = point[1] - cylinder[i][1];
					normal[2] = point[2] - cylinder[i][2];
				}
			}
		}
		
		// calculate intersection of ray with the closest Cone
		// Ray equation:
		// x/y/z = vpt + t*(rpt - vpt);
		// Cone equation:
		// vertical cone: (x-xc)^2 + (z-zc)^2 = (r-y)^2; y0<y<r; 
		// We can solve quadratic formula for t and find the intersection
		// t has to be > 0 to intersect with an object
/*
		if (t==0) // not hitting a sphere
		for (int i = 0; i < nc; i++) { // for each cone
			a = vpt[0] - cone[i][1]; // x component with t
			b = rpt[0] - vpt[0]; // x component
			c = vpt[1] - cone[i][0]; // y component
			d = rpt[1] - vpt[1]; // y component with t
			e = vpt[2] - cone[i][2]; // z component
			f = rpt[2] - vpt[2]; // z component with t

			float A = b*b + f*f - d*d;
			float B = 2 * (a * b + e*f - c * d);
			float C = a * a + e*e - c * c;

			float answers[] = new float[2];

			if (quadraticFormula(A,B,C,answers)) {// intersection
				if (answers[0] < answers[1]) {
					t = answers[0];
				}
				else {  t = answers[1]; }
				
				if (t < 0.001) {
					t = 0; 
					break;
				}
				else {
					// return point and normal
					point[0] = vpt[0] + t * (rpt[0] - vpt[0]);
					point[1] = vpt[1] + t * (rpt[1] - vpt[1]);
					point[2] = vpt[2] + t * (rpt[2] - vpt[2]);
					
					if (point[1]<cone[i][3] || point[1]>cone[i][0]) {
						t=0; 
						break; // outside cylinder
					}
					
					normal[0] = point[0] - cone[i][1];
					normal[1] = point[1] - cone[i][3];
					normal[2] = point[2] - cone[i][2];


				}
			}
		}
		
*/			
		
		
		// calculate ray intersect with plane y = yplane
		// y = vpt + t(rpt - vpt) = yplane; => t = (yplane - vpt)/(rpt -vpt);

		float tmp = (yplane - vpt[1]) / (rpt[1] - vpt[1]);
		float[] ipoint = new float[3]; // for shadow
		if ((tmp > 0.001) && (tmp < t || t == 0)) {
			t = tmp;
			ipoint[0] = vpt[0] + t * (rpt[0] - vpt[0]);
			ipoint[1] = yplane;
			ipoint[2] = vpt[2] + t * (rpt[2] - vpt[2]);
			// if x&z in the rectangle, intersect with plane
			if ((ipoint[0] > -HEIGHT / 2) && (ipoint[0] < HEIGHT / 2)
					&& (ipoint[2] > -HEIGHT / 2) && (ipoint[2] < HEIGHT / 2)
					&& t > 0) {
				// plane normal
				point[0] = ipoint[0];
				point[1] = ipoint[1];
				point[2] = ipoint[2];
				normal[0] = 0;
				normal[1] = 1;
				normal[2] = 0;
			}
		}
		
		
		// calculate ray intersect with zplane 
		// z = vpt + t(rpt - vpt) = zplane; => t = (zplane - vpt)/(rpt -vpt);
		tmp = (zplane - vpt[2]) / (rpt[2] - vpt[2]);
		if ((tmp > 0.001) && (tmp < t || t == 0)) {
			t = tmp;
			ipoint[0] = vpt[0] + t * (rpt[0] - vpt[0]);
			ipoint[1] = vpt[1] + t * (rpt[1] - vpt[1]);
			ipoint[2] = zplane;

			// if x&z in the rectangle, intersect with plane
			if ((ipoint[0] > -HEIGHT/1.5) && (ipoint[0] < HEIGHT/1.5)
					&& (ipoint[1] > -HEIGHT/1.5) && (ipoint[1] < HEIGHT/1.5)
					&& t > 0) {
				// plane normal
				point[0] = ipoint[0];
				point[1] = ipoint[1];
				point[2] = ipoint[2];
				normal[0] = 0;
				normal[1] = 0;
				normal[2] = 1;
			}
		}		
	}
	
	// cros product of two vectors
	public void crossprod(float[] a, float[] b, float[] v) {

		v[0] = a[1] * b[2] - a[2] * b[1];
		v[1] = a[2] * b[0] - a[0] * b[2];
		v[2] = a[0] * b[1] - a[1] * b[0];
	}

	
	// dot product of two vectors
	public float dotprod(float[] a, float[] b) {

		return (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]);
	}

	// reflect v1 around n to v2
	public void reflect(float v1[], float n[], float v2[]) {

		// v2 = 2*dot(v1, n)*n + v1
		for (int i = 0; i < 3; i++) {
			v2[i] = 2 * dotprod(v1, n) * n[i] - v1[i];
		}
	}

	
	public boolean quadraticFormula(float a, float b, float c, float ans[]) {

		float d = b*b - 4*a*c; 
		if (d<0) {
			return (false); 
			
		} else {
			ans[0] = (-b+(float)Math.sqrt(d))/(2*a); 
			ans[1] = (-b-(float)Math.sqrt(d))/(2*a); 
			return (true); 
		}
	}
	
	
	public void init(GLAutoDrawable drawable) { // reading new vertex & fragment shaders
		gl = (GL4) drawable.getGL();
		String vShaderSource[], fShaderSource[] ;

		System.out.println("a) init: prepare shaders"); 
		String path = this.getClass().getPackageName().replace(".", "/"); 

		vShaderSource = readShaderSource("src/"+path+"/JOGL3_12_V.shader"); // read vertex shader
		fShaderSource = readShaderSource("src/"+path+"/JOGL3_12_F.shader"); // read fragment shader
		
		vfPrograms = initShaders(vShaderSource, fShaderSource);		
	}

	
	

	public static void main(String[] args) {
		JOGL3_12_rayTracing f = new JOGL3_12_rayTracing();
	}
}
