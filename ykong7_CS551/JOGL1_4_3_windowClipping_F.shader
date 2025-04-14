#version	430	

uniform float w, h;

in  vec3 color; // (interpolated) value from vertex shader
out vec4 fColor; // out to display


void main(void) { 

	 fColor = vec4(color, 1.0);  
	 
	if (gl_FragCoord.x	<	1+w/4)  //fragment in device coordinates // WIDTH/4
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	
	else
	if (gl_FragCoord.x	>	3*w/4)  //fragment in device coordinates //3*WIDTH/4
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	

	if (gl_FragCoord.y	<	1+h/4)  //fragment in device coordinates 
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	
	else 	
	if (gl_FragCoord.y	>	3*h/4 - 1)  //fragment in device coordinates 
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	
		
}
