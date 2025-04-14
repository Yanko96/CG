
#version	430	

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 

uniform float Cnt; 

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]

out vec3 color; // output to fragment shader


// builds and returns a matrix that performs a rotation around the X, Y, or Z axis
mat4 RotateMatrix(float rad, float u, float v, float w) {
	mat4 rot; 
	float x, y, z; 
	
	float sq = sqrt(u*u + v*v + w*w);
    x = u/sq; 
    y = v/sq; 
    z = w/sq; 
    
    float c = cos(rad); // gradian 
    float s = sin(rad);

 	rot = mat4(	x*x*(1-c) + c, y*x*(1-c) + z*s, z*x*(1-c) - y*s, 0.0, 
 				x*y*(1-c) - z*s, y*y*(1-c) + c, z*y*(1-c) + x*s, 0.0, 
 				x*z*(1-c) + y*s, y*z*(1-c) - x*s, z*z*(1-c) + c, 0.0, 
 				0.0, 0.0, 0.0, 1.0
 			  ); 
 			  
	return rot;
}


// builds and returns a translation matrix
mat4 TranslateMatrix(float x, float y, float z) { 
	mat4 trans = mat4( 	1.0, 0.0, 0.0, 0.0,
					   	0.0, 1.0, 0.0, 0.0,
					   	0.0, 0.0, 1.0, 0.0,
						x, y, z, 1.0 );
	return trans;
}
// builds and returns a scale matrix
mat4 ScaleMatrix(float x, float y, float z) { 
	mat4 trans = mat4( 	  x, 0.0, 0.0, 0.0,
					   	0.0,   y, 0.0, 0.0,
					   	0.0, 0.0, z,   0.0,
						0.0, 0.0, 0.0, 1.0 );
	return trans;
}


void	main(void)	{	
	vec3 vColor; // vertex color
	float i = Cnt/100.0 + gl_InstanceID;
	
	
	// build the model matrix and then the model-view matrix
	mat4 MV_matrix = mv_matrix; 
	
	if (gl_InstanceID != 0) 
		MV_matrix = TranslateMatrix(sin(i)*200.0,sin(i*0.3)*200.0,sin(i*0.4)*200.0) * ScaleMatrix(sin(i*0.3),sin(i*0.6),sin(i)) * 
						RotateMatrix(i, 1.0, 0.0, 0.0) * RotateMatrix(i, 0.0, 1.0, 0.0) * RotateMatrix(i, 0.0, 0.0, 1.0) * mv_matrix;

	
	gl_Position = proj_matrix*MV_matrix*vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	

	if (iPosition.x == 0 && iPosition.y == 0 && iPosition.z == 0) vColor = vec3(0.5, 0.5, 0.5); 
	else vColor = normalize(iPosition); // avoid zero length vector


	// color specified according to the vertex position
    color =	vec3(abs(vColor.x), abs(vColor.y), abs(vColor.z));

}




