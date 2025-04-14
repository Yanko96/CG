


import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL4.*;

public class JOGL2_15_LookAt extends JOGL2_14_Perspective {

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

		myReshape2();	
		if (cnt % 1000 > 500) {
			gl.glViewport(0, 0, WIDTH, HEIGHT);
		    if (cnt%750<311) 
				 myCamera(WIDTH/4, 2f*cnt*dg, WIDTH/6, spherem+sphereD); 			    
			drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
		}
		else {
			myReshape1();
			viewPort1();
			myReshape2();
			viewPort2();
			viewPort3();
			viewPort4();
			viewPort5();			
		}
	}

	public void myReshape1() {
		int w = WIDTH;
		int h = HEIGHT;

		myLoadIdentity();
		myPerspective(40, w / h, 2*w, 6 * w);

		myLoadIdentity();
		myTranslatef(0, 0, -4 * w);
	}

	public void myReshape2() {
		int w = WIDTH;
		int h = HEIGHT;

		myLoadIdentity();
		myOrtho(-w, w, -h, h, 2*w, 6 * w);

		myLoadIdentity();
		myTranslatef(0, 0, -4 * w);
	}

	public void viewPort1() {
		int w = WIDTH;
		int h = HEIGHT;

		gl.glViewport(0, 0, w / 2, h / 2);
		drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
	}

	public void viewPort2() {
		int w = WIDTH;
		int h = HEIGHT;

		gl.glViewport(w / 2, 0, w / 2, h / 2);
		drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
		// negate the movement
		spherem = spherem - sphereD;
		cylinderm = cylinderm - cylinderD;
		conem = conem - coneD;
	}

	public void viewPort3() {
		int w = WIDTH;
		int h = HEIGHT;

		gl.glViewport(w / 2, h / 2, w / 2, h / 2);

		myPushMatrix();
		// the objects' centers are retrieved here
		myLoadIdentity();
		drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
		// negate the movement
		spherem = spherem - sphereD;
		cylinderm = cylinderm - cylinderD;
		conem = conem - coneD;
		myPopMatrix();

		myPushMatrix();

		myLookAt(coneC[0], coneC[1], coneC[2], earthC[0], earthC[1], earthC[2], earthC[0] - solarOriginC[0],
				earthC[1] - solarOriginC[1], earthC[2] - solarOriginC[2]);
		drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
		// negate the movement
		spherem = spherem - sphereD;
		cylinderm = cylinderm - cylinderD;
		conem = conem - coneD;
		myPopMatrix();

	}

	public void viewPort4() {
		int w = WIDTH;
		int h = HEIGHT;
		gl.glViewport(0, h / 2, w / 2, h / 2);

		myPushMatrix();
		myLoadIdentity();
		// the objects' centers are retrieved here
		drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
		// negate the movement
		spherem = spherem - sphereD;
		cylinderm = cylinderm - cylinderD;
		conem = conem - coneD;
		myPopMatrix();

		myPushMatrix();
		mygluLookAt(earthC[0], earthC[1], earthC[2], sunC[0], sunC[1], sunC[2], 0, 1, 0);
		drawRobot(O, A, B, C, alpha * dg, beta * dg, gama * dg);
		
		//try { Thread.sleep(1050); } catch (Exception ignore) {}
	
		// negate the movement
		spherem = spherem - sphereD;
		cylinderm = cylinderm - cylinderD;
		conem = conem - coneD;
		myPopMatrix();
	}

	public void viewPort5() {
		int w = WIDTH, h = HEIGHT;

		gl.glViewport(w/3, h/3, w/3, h/3);

		myPushMatrix();
		// earthC retrieved
		myLoadIdentity();
		drawSolar(WIDTH / 4, 2.5f * cnt * dg, WIDTH / 6, cnt * dg);
		// negate the movement
		spherem = spherem - sphereD;
		cylinderm = cylinderm - cylinderD;
		conem = conem - coneD;
		myPopMatrix();
		myPushMatrix();
		// earthC retrieved in drawSolar() before
		myLookAt(sphereC[0], sphereC[1], sphereC[2], earthC[0], earthC[1], earthC[2], earthC[0], earthC[1],
				earthC[2]);
		drawSolar(WIDTH / 4, 2.5f * cnt * dg, WIDTH / 6, cnt * dg);
		myPopMatrix();
	}

	public void myLookAt(double eX, double eY, double eZ, double cX, double cY, double cZ, double upX, double upY,
			double upZ) {
		// eye and center are points, but up is a vector

		// 1. change center into a vector:
		// glTranslated(-eX, -eY, -eZ);
		cX = cX - eX;
		cY = cY - eY;
		cZ = cZ - eZ;

		// 2. The angle of center on xz plane and x axis
		// i.e. angle to rot so center in the neg. yz plane
		double a = Math.atan(cZ / cX);
		if (cX == 0)
			a = Math.PI;
		else if (cX >= 0) {
			a = a + Math.PI / 2;
		} else {
			a = a - Math.PI / 2;
		}

		// 3. The angle between the center and y axis
		// i.e. angle to rot so the center is in the negative z axis
		double b = Math.acos(cY / Math.sqrt(cX * cX + cY * cY + cZ * cZ));
		b = b - Math.PI / 2;

		// 4. up rotate around y axis (a) radians
		double upx = upX * Math.cos(a) + upZ * Math.sin(a);
		double upz = -upX * Math.sin(a) + upZ * Math.cos(a);
		upX = upx;
		upZ = upz;

		// 5. up rotate around x axis (b) radians
		double upy = upY * Math.cos(b) - upZ * Math.sin(b);
		upz = upY * Math.sin(b) + upZ * Math.cos(b);
		upY = upy;
		upZ = upz;

		// 6. the angle between the vector of (up projected on xy plane) and y axis
		// i.e. the angle to rot around z so that up is in yz plane
		double c = Math.atan(upX / upY);
		if (upY < 0) {
			c = c + Math.PI;
		}
		myRotatef((float) c, 0, 0, 1);
		// up in yz plane
		myRotatef((float) b, 1, 0, 0);
		// center in negative z axis
		myRotatef((float) a, 0, 1, 0);
		// center in yz plane
		myTranslatef((float) -eX, (float) -eY, (float) -eZ);
		// eye at the origin
	}

	public void mygluLookAt(double eX, double eY, double eZ, double cX, double cY, double cZ, double upX, double upY,
			double upZ) {
		// eye and center are points, but up is a vector

		double[] F = new double[3];
		double[] UP = new double[3];
		double[] s = new double[3];
		double[] u = new double[3];

		F[0] = cX - eX;
		F[1] = cY - eY;
		F[2] = cZ - eZ;
		UP[0] = upX;
		UP[1] = upY;
		UP[2] = upZ;
		normalize(F);
		normalize(UP);
		crossProd(F, UP, s);
		crossProd(s, F, u);

		float[][] M = new float[4][4];

		M[0][0] = (float) s[0];
		M[1][0] = (float) u[0];
		M[2][0] = (float) -F[0];
		M[3][0] = (float) 0;
		M[0][1] = (float) s[1];
		M[1][1] = (float) u[1];
		M[2][1] = (float) -F[1];
		M[3][1] = (float) 0;
		M[0][2] = (float) s[2];
		M[1][2] = (float) u[2];
		M[2][2] = (float) -F[2];
		M[3][2] = 0;
		M[0][3] = 0;
		M[1][3] = 0;
		M[2][3] = 0;
		M[3][3] = 1;

		myMultMatrix(M);
		myTranslatef((float) -eX, (float) -eY, (float) -eZ);
	}

	public void normalize(double v[]) {

		double d = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

		if (d == 0) {
			System.out.println("0 length vector: normalize().");
			return;
		}
		v[0] /= d;
		v[1] /= d;
		v[2] /= d;
	}

	public void crossProd(double U[], double V[], double W[]) {
		// W = U X V
		W[0] = U[1] * V[2] - U[2] * V[1];
		W[1] = U[2] * V[0] - U[0] * V[2];
		W[2] = U[0] * V[1] - U[1] * V[0];
	}

	public static void main(String[] args) {
		JOGL2_15_LookAt f = new JOGL2_15_LookAt();
		f.setTitle("LL: perspective; LR: Orthographic; UL: earth to sun; UR: moon to earth");
	}

}
