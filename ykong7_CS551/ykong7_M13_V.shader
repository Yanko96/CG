#version	430	

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 

layout (location = 0) in vec3 iPosition; 
layout (location = 1) in vec3 iNormal; 

out vec4 position;  
out vec4 normal;   
out vec2 texCoord;  

void	main(void)	{	
	gl_Position = proj_matrix * mv_matrix * vec4(iPosition, 1.0);	

	position = mv_matrix * vec4(iPosition, 1.0);	

	mat4 mv_it = transpose(inverse(mv_matrix)); 
	normal = mv_it * vec4(iNormal, 1.0); 

	texCoord = vec2(iPosition.x, iPosition.y);
}