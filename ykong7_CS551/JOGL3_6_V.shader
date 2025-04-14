
#version	430	

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 

uniform vec4 Me; // emission 
uniform vec4 La; // light source ambient  
uniform vec4 Ma; // material ambient
uniform vec4 Ld; // light source diffuse  
uniform vec4 Md; // material diffuse
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
	vec4 Ie = Me; 
	vec4 Ia = La*Ma; 
   
  	// calculate diffuse component
    vec4 Lg = Lsp - position; // light source direction 
 	vec3 L = normalize(Lg.xyz); // 3D normalized light source direction
	vec3 N = normalize(normal.xyz); // 3D normalized normal
  	float NL = max(dot(N, L), 0); 
    vec4 Id = Ld*Md*NL;
 
 	// calculate specular component
    vec4 Vg = Vsp - position; // view direction 
 	vec3 V = normalize(Vg.xyz); // 3D normalized light source direction
  	//vec3 R = reflect(L, N);
  	vec3 s = normalize(L+V); 
  	float Ns = max(dot(N, s), 0); 

    vec4 Is = Ls*Ms*pow(Ns, Msh); 
  	if (NL==0) Is = vec4(0,0,0,1); // if light source is away from the normal, there is no specular component
    
    // the final color is a combination of the components
    color = (Ie + Ia + Id + Is)/3.0;  
}