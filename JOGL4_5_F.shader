#version	430	

uniform float Width; // Viewport Width 
uniform float Height; // Viewport Height 

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

uniform vec4 L1d; // light source diffuse  
uniform vec4 L1sp; // light source position  
uniform vec4 L2d; // light source diffuse  
uniform vec4 L2sp; // light source position  
uniform vec4 L3d; // light source diffuse  
uniform vec4 L3sp; // light source position  

in  vec4 position; // (interpolated) value from vertex shader
in  vec4 normal; // (interpolated) value from vertex shader

layout	(binding=0)	uniform	sampler2D	gmuTex; 

out vec4 fColor; // out to display


void	main(void)	{	
	

	// Lighting calculation	  
	vec4 Ie = Me; 
	vec4 Ia = La*Ma; 
   
  	// calculate diffuse component
    vec4 Lg = Lsp - position; // light source direction 
    vec4 L1g = L1sp - position; // light source direction 
    vec4 L2g = L2sp - position; // light source direction 
    vec4 L3g = L3sp - position; // light source direction 
 	vec3 L = normalize(Lg.xyz); // 3D normalized light source direction
 	vec3 L1 = normalize(L1g.xyz); // 3D normalized light source direction
	vec3 L2 = normalize(L2g.xyz); // 3D normalized light source direction
	vec3 L3 = normalize(L3g.xyz); // 3D normalized light source direction
	vec3 N = normalize(normal.xyz); // 3D normalized normal
  	float NL = max(dot(N, L), 0); 
  	float NL1 = max(dot(N, L1), 0); 
  	float NL2 = max(dot(N, L2), 0); 
  	float NL3 = max(dot(N, L3), 0); 
    vec4 Id = Ld*Md*NL;
    vec4 I1d = L1d*Md*NL1;
    vec4 I2d = L2d*Md*NL2;
    vec4 I3d = L3d*Md*NL3;
 
 	// calculate specular component
    vec4 Vg = Vsp - position; // view direction 
 	vec3 V = normalize(Vg.xyz); // 3D normalized light source direction
  	vec3 s = normalize(L + V);
  	vec3 s1 = normalize(L1 + V);
 	vec3 s2 = normalize(L2 + V);
 	vec3 s3 = normalize(L3 + V);
  	float Ns = max(dot(N, s), 0); 
  	float Ns1 = max(dot(N, s1), 0); 
 	float Ns2 = max(dot(N, s2), 0); 
 	float Ns3 = max(dot(N, s3), 0); 
  	if (NL==0) Ns = 0; 
    if (NL1==0) Ns1 = 0; 
    if (NL2==0) Ns2 = 0; 
    if (NL3==0) Ns3 = 0; 
    vec4 Is  = Ms*Ls*pow(Ns, Msh); 
    vec4 I1s = Ms*L1d*pow(Ns1, Msh); 
    vec4 I2s = Ms*L2d*pow(Ns2, Msh); 
    vec4 I3s = Ms*L3d*pow(Ns3, Msh); 

    fColor = (Ie + Ia + (Id + I1d + I2d + I3d) + (Is + I1s + I2s + I3s))/2;      


    // texture coordinates corresponds to the viewport    
    vec2 tc = vec2(gl_FragCoord.x/Width, gl_FragCoord.y/Height); 
 
   fColor = 0.4*fColor + 0.6*texture(gmuTex, tc); // integrate color and texture
    
}