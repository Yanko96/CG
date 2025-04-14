
#version	430	

uniform mat4 mv_matrix, light_matrix; 

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]

void	main(void)	{	
	
	// here light_matrix transform's the projection matrix
	gl_Position = light_matrix*mv_matrix*vec4(iPosition, 1.0);	

}

