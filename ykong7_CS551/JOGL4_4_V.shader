
#version	430	

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 


layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iNormal; // VBO: vbo[1]

out vec4 position; // output to fragment shader for lighting calculation
out vec4 normal; // output to fragment shader for lighting calculation


void	main(void)	{	
	
	gl_Position = proj_matrix*mv_matrix*vec4(iPosition, 1.0);	

	position = mv_matrix*vec4(iPosition, 1.0);	

	// normal is transformed by inverse-transpose of the current matrix
	mat4 mv_it = transpose(inverse(mv_matrix)); 
	normal  = mv_it*vec4(iNormal, 1); 
}

