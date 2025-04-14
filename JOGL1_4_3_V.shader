
#version	430	

//uniform vec3 vColor; // color from the JOGL program (same value for all vertices) 

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iColor;    // VBO: vbo[1]

out vec3 color; // output to fragment shader

void	main(void)	{	
	//gl_Position = vec4(sPos, sPos, 0.0, 1.0);	
	
	gl_Position = vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	
	color = iColor;	// each pixel is a unique color
	
}