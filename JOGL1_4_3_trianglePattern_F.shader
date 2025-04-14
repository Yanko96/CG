#version	430	

uniform int sPos; //used as the anchor vertex, as well as a switch between anchors

in  vec3 color; // (interpolated) value from vertex shader
out vec4 fColor; // out to display


void main(void) { 

	 
	if (sPos	<	500)  // anchor on the window  
	     fColor = vec4(sin(gl_FragCoord.x/6.28), cos(gl_FragCoord.y/6.28), tan(gl_FragCoord.x/6.28), 1.0);  
	else // anchor on the vertex
	     fColor = vec4(sin((gl_FragCoord.x-sPos)/6.28), cos((gl_FragCoord.y-sPos)/6.28), tan((gl_FragCoord.x-sPos)/6.28), 1.0);  
	
		
		
}
