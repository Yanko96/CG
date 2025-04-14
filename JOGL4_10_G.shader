
#version	450	

uniform mat4 proj_matrix; 
uniform float Cnt; 

in vec3 gcolor[]; // in from vertex shader

layout (triangles) in;
layout (triangle_strip, max_vertices=12) out; // 6. Add 3 triangles on top of a triangle
//layout (triangle_strip, max_vertices=3) out; // the output is triangle strip! be careful!


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
	int i; 
	vec4 normal; 
	
	normal = gl_in[0].gl_Position + gl_in[1].gl_Position + gl_in[2].gl_Position; 
	normal = normalize(normal); 

	//5. average the primitive vertex colors
	vec3 pcolor = (gcolor[0]+gcolor[1]+gcolor[2])/3.0; 
	color = pcolor; 			

	// 6. create 3 new triangles 
	vec4 v01 = (gl_in[0].gl_Position + gl_in[1].gl_Position)/2.0; 
	vec4 v12 = (gl_in[1].gl_Position + gl_in[2].gl_Position)/2.0; 
	vec4 v02 = (gl_in[0].gl_Position + gl_in[2].gl_Position)/2.0; 
	vec4 v00 = 
				ScaleMatrix(1.5, 1.5, 1.5)*
				(gl_in[0].gl_Position + gl_in[1].gl_Position + gl_in[2].gl_Position)/3.0; 

//	int myCnt = (int) Cnt; 
	if (mod(Cnt, 1234) < 610) { // 6. subdivde triangles: triangle strip

		gl_Position = proj_matrix*v01; 
	    EmitVertex();	
		gl_Position = proj_matrix*v02; 
	    EmitVertex();	
		gl_Position = proj_matrix*v00; 
		EmitVertex();	
		gl_Position = proj_matrix*v12; 
	    EmitVertex();	
		gl_Position = proj_matrix*v01; 
	    EmitVertex();	
		color= gcolor[1]; 
		gl_Position = proj_matrix*gl_in[1].gl_Position; 
		EmitVertex();	
		color = pcolor;
		gl_Position = proj_matrix*v01; 
	    EmitVertex();	
	 	color= gcolor[0]; 
		gl_Position = proj_matrix*gl_in[0].gl_Position; 
		EmitVertex();	
		color = pcolor; 
		gl_Position = proj_matrix*v02; 
	    EmitVertex();	
		color= gcolor[2]; 
		gl_Position = proj_matrix*gl_in[2].gl_Position; 
		EmitVertex();	
		color = pcolor; 
		gl_Position = proj_matrix*v12; 
	    EmitVertex();	
	}
	else { // 1. delete a triangle; 2. extrude along the normal; 3. scale some triangles; 4. rotate; 
	 //color= (gcolor[0] + gcolor[1] + gcolor[2])/3; 
	 	for (i=0; i<3; i++) { 
	
		
			if (mod(gl_PrimitiveIDIn,2) == 1) { // alternating (odd ID) triangles 
				gl_Position = 
								proj_matrix*
								(normal*0.95 + //2. extrude along the normal											
								ScaleMatrix(0.45, 0.45, 0.45)* //3. scale larger or smaller 
								RotateMatrix(Cnt/25.0, normal[0], normal[1], normal[2])* //4. rotate along the normal
								gl_in[i].gl_Position); 
			}
			else 
			gl_Position = proj_matrix*gl_in[i].gl_Position;
			
			// 5. comment out the following line for uniform triangle color
		    color= gcolor[i]; 
			
			//if (mod(gl_PrimitiveIDIn,2) == 0) //1. eliminate half of the triangles (primitives)
			EmitVertex();
		}	
	}
	EndPrimitive();
}

