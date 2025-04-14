
#version	430	

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 


uniform vec4 Lsp; // light source position  
uniform vec4 Ls; // light source specular  
uniform vec4 Ms; // material specular
uniform vec4 Vsp; // view position  
uniform float Msh; // material shininess  


layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iNormal; // VBO: vbo[1]

out vec4 color; // output to fragment shader

void	main(void)	{	
	
	gl_Position = proj_matrix*mv_matrix*vec4(iPosition, 1.0);	

	// for lighting calculation
	vec4 position = mv_matrix*vec4(iPosition, 1.0);

	// normal is transformed by inverse-transpose of the current matrix
	mat4 mv_it = transpose(inverse(mv_matrix)); 
	vec4 normal  = mv_it*vec4(iNormal, 1); 

	// Lighting calculation	  
    vec4 Lg = Lsp - position; // light source direction 
 	vec3 L = normalize(Lg.xyz); // 3D normalized light source direction
	vec3 N = normalize(normal.xyz); // 3D normalized normal
   
 	// calculate specular component
    vec4 Vg = Vsp - position; // view direction 
 	vec3 V = normalize(Vg.xyz); // 3D normalized view direction direction
  	vec3 R = reflect(L, N);
  	float VR = max(dot(V, R), 0); 
    vec4 Is = Ls * Ms * pow(VR, Msh); 
    
    color = Is; 
    
    
}