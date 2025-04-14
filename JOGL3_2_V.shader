
#version	430	

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 

uniform vec4 Me; // emission 

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]

out vec4 color; // output to fragment shader

void	main(void)	{	
	
	gl_Position = proj_matrix*mv_matrix*vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	

	// Lighting calculation: emission
	vec4 Ie = Me; 
	
    color =	Ie;
}