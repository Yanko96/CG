#version	430	

//uniform vec3 iColor; // color from the JOGL program (same value for all vertices or pixels) 


in  vec3 color; // (interpolated) value from vertex shader
out vec4 fColor; // out to display


void main(void) { 

	fColor = vec4(color, 1); 
 	//fColor = 1 - fColor/2; // all vec components are divided by 2 
		
		
}
