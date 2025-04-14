#version	430	

uniform float Width; // Viewport Width 
uniform float Height; // Viewport Height 

uniform int dCone; // draw cone indicator 
uniform int dSphere; // draw sphere indicator 

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
in  vec3 tc; // (interpolated) texture coordinates

layout	(binding=0)	uniform	sampler2D	gmuTex; 
layout	(binding=1)	uniform	sampler2D	vseTex; 
layout	(binding=2)	uniform	sampler2D	cubeTex; 

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
    
    fColor = (Ie + Ia + (Id + I1d + I2d + I3d) + (Is + I1s + I2s + I3s))/1.5;      


    // texture coordinates corresponds to the viewport    
	vec2 tc1;
 	tc1 = vec2(gl_FragCoord.x/Width, gl_FragCoord.y/Height);    

   // texture coordinates corresponds to the cone    
	vec2 tc2;
    if (dCone==1) tc2=vec2((tc.x+ 1.0)/2.0, (tc.y+ 1.0)/2.0); // floating point operation!!!
    

    // cube mapping texture coordinates corresponds to the sphere    
	vec2 tc3;
    if (dSphere==1) {
    	if ((abs(tc.x) > abs(tc.y)) && (abs(tc.x) > abs(tc.z))) { // intersect xplane; 
    		if (tc.x > 0) { // x=1 plane; 
    			tc3 = vec2((tc.z/tc.x + 1.0)/2.0, (tc.y/tc.x + 1.0)/2.0); // floating point operation!!!
    			tc3 = vec2(tc3.x/4.0 + 0.5, tc3.y/3.0 + 1.0/3.0); 
    		}
    		else { // x=-1 plane; 
    			tc3 = vec2((tc.z/tc.x +1.0)/2.0, (-tc.y/tc.x + 1.0)/2.0); 
    			tc3 = vec2(tc3.x/4.0, tc3.y/3.0 + 1.0/3.0); 
    		}
    	}
    	else if (abs(tc.y) > abs(tc.z)) { // intersect yplane; 
    		if (tc.y > 0) { // y=1 plane; 
    			tc3 = vec2((tc.x/tc.y +1.0)/2.0, (tc.z/tc.y + 1.0)/2.0); 
    			tc3 = vec2(tc3.x/4.0 + 0.25, tc3.y/3.0 + 2.0/3.0); 
    		}
    		else { // y=-1 plane; 
    			tc3 = vec2((-tc.x/tc.y +1.0)/2.0, (tc.z/tc.y + 1.0)/2.0); 
    			tc3 = vec2(tc3.x/4+0.25, tc3.y/3.0); 
    		}
    	}
    	else  { // intersect zplane; 
    		if (tc.z > 0) { // z=1 plane; 
    			tc3 = vec2((-tc.x/tc.z +1.0)/2.0, (tc.y/tc.z + 1.0)/2.0); 
    			tc3 = vec2(tc3.x/4.0 + 0.75, tc3.y/3.0 + 1.0/3.0); 
    		}
    		else { // z=-1 plane; 
    			tc3 = vec2((-tc.x/tc.z +1.0)/2.0, (-tc.y/tc.z + 1.0)/2.0); 
    			tc3 = vec2(tc3.x/4.0+0.25, tc3.y/3.0 + 1.0/3.0); 
    		}
    	}   	  	
 	}
   	 
    if (dSphere==1) fColor = 0.5*fColor + 0.5*texture(cubeTex, tc3); // integrate color and texture
    else        
    if (dCone==1) fColor = 0.5*fColor + 0.5*texture(vseTex, tc2); // integrate color and texture
    else 
        fColor = 0.5*fColor + 0.5*texture(gmuTex, tc1); // integrate color and texture

}