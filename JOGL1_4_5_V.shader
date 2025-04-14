
#version	430	

uniform float theta; // input angle for rotation 

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]

out vec3 color; // output to fragment shader

void	main(void)	{	
	float dt = theta/50; 
	
	//we do the rotation in shader: the matrix is the transpose of the corresponding homogeneous matrix
	mat4 rotate_z = mat4( cos(dt),  sin(dt),  0.0, 0.0,
						 -sin(dt),  cos(dt),  0.0, 0.0,
						      0.0,      0.0,  1.0, 0.0,
						      0.0,      0.0,  0.0, 1.0);
						 
	mat4 rotate_y = mat4( cos(dt), 0.0, -sin(dt), 0.0,
						      0.0, 1.0,      0.0, 0.0,
						  sin(dt), 0.0,  cos(dt), 0.0,
						      0.0, 0.0,      0.0, 1.0);					 

	
	gl_Position = vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	
	gl_Position = rotate_y*rotate_z*vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	

	if (length(iPosition) == 0) 
		color = vec3(1, 1, 1);  // center of the circle 
	else 
		color = normalize(iPosition); // avoid zero length vector

	// color specified according to the vertex position
    color =	vec3(abs(color.x), abs(color.y), abs(color.z));

}