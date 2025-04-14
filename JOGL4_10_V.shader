
#version	450	

//uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 


layout (location = 0) in vec3 iPosition; // VBO: vbo[0]

out vec3 gcolor; // output to geometry shader

void	main(void)	{	
	vec3 vColor; // vertex color

	
	gl_Position = mv_matrix*vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	

	vColor = normalize(iPosition); // avoid zero length vector


	// color specified according to the vertex position
    gcolor =	vec3(abs(vColor.x), abs(vColor.y), abs(vColor.z));

}