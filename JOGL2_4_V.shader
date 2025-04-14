
#version	430	

uniform float theta; // input angle for rotation 

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
uniform mat4 mv_matrix; 
uniform mat4 proj_matrix; 

out vec3 color; // output to fragment shader

void	main(void)	{	
	vec3 vColor; // vertex color
	float dt = theta/50; 
	
//we do the rotation in shader
mat4 rotate_y = mat4(cos(dt), 0.0, sin(dt), 0.0,
					 0.0, 1.0, 0.0, 0.0,
					 -sin(dt), 0.0, cos(dt), 0.0,
					 0.0, 0.0, 0.0, 1.0);				
	
	gl_Position = proj_matrix*mv_matrix*rotate_y*vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	
	

	if (iPosition.x == 0 && iPosition.y == 0 && iPosition.z == 0) vColor = vec3(0.5, 0.5, 0.5); 
	else vColor = normalize(iPosition); // avoid zero length vector

	// color specified according to the vertex position
    color =	vec3(abs(vColor.x), abs(vColor.y), abs(vColor.x*vColor.y));

}