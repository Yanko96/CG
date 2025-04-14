
#version	430	

//uniform vec3 uColor; // color from the JOGL program (same value for all vertices) 
uniform mat4 mv_matrix; // current matrix from the JOGL program

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]


//out vec3 color; // output to fragment shader

void	main(void)	{	
	
	gl_Position = mv_matrix*vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	
	
    //color =	uColor;
}