#version	430	

uniform float sPos; 

in  vec3 clr; // (interpolated) value from vertex shader
out vec4 fclr; // out to display


void main(void) { 

	 fclr = vec4(sPos*sPos, 0.0, 0.0, 0.1); 
 	 fclr = vec4(1-clr, 1.0); // note the vector operators
		
		
}
