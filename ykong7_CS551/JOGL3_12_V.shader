
#version	430	

uniform mat4 proj_matrix; 
uniform vec3 rPoint; 


out vec3 color; 

void	main(void)	{		
	gl_Position = proj_matrix*vec4(rPoint, 1.0);	
}

