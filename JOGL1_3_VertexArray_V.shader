#version	430	

uniform float sPos; 

layout (location = 0) in vec3 iPos; // VBO: vbo[0]
layout (location = 1) in vec3 iClr;    // VBO: vbo[1]

out vec3 clr; // output to fragment shader

void	main(void)	{	
	//gl_Position = vec4(sPos, sPos, 0.0, 1.0);	
	
	gl_Position = vec4(iPos.x, iPos.y + sPos, 0.0, 1.0);	
	clr = iClr;	
	
}