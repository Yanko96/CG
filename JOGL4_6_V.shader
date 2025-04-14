
#version	430	

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 
uniform float Cnt; 
uniform int dCone; // draw cone indicator 
uniform int dCylinder; // draw cone indicator 

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iNormal; // VBO: vbo[1]
layout (location = 2) in vec2 iTexCoord; // VBO: vbo[2]

out vec4 position; // output to fragment shader for lighting calculation
out vec4 normal; // output to fragment shader for lighting calculation
out vec2 texCoord; 

float dt=0.0; 		
	
void	main(void)	{	
					 
	gl_Position = proj_matrix*mv_matrix*vec4(iPosition, 1.0);	
	
	position = mv_matrix*vec4(iPosition, 1.0);	

	// normal is transformed by inverse-transpose of the current matrix
	mat4 mv_it = transpose(inverse(mv_matrix)); 
	normal  = mv_it*vec4(iNormal, 1); 

	texCoord = iTexCoord; 

	/*
	//Texture animation: simple translation
	// need to specify texture coordinate S and T wrap with repeat or repeat mirror
	dt = Cnt/10.0; 	
	//tc = iTexCoord + dt; // simple translation (creeping)


	//Texture animation: rotate around the center (along z axis)	
	mat4 rotz = mat4(cos(dt), sin(dt), 0.0, 0.0,
					-sin(dt), cos(dt), 0.0, 0.0,
						 0.0,     0.0, 1.0, 0.0,
						 0.0,     0.0, 0.0, 1.0);		
	vec4 ttc = rotz*vec4(iTexCoord.x-0.5, iTexCoord.y-0.5, 0, 1); // -0.5 to center the texture
	tc = vec2(ttc.x+0.5, ttc.y+0.5); 	
	*/
}

