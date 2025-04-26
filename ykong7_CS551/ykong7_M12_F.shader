#version 430

// Input from vertex shader
in vec4 position; // Interpolated position in view space
in vec4 normal;   // Interpolated normal in view space

// Material properties
uniform vec4 Me;  // emission 
uniform vec4 La;  // light source ambient  
uniform vec4 Ma;  // material ambient
uniform vec4 Ld;  // light source diffuse  
uniform vec4 Md;  // material diffuse
uniform vec4 Lsp; // light source position  
uniform vec4 Ls;  // light source specular  
uniform vec4 Ms;  // material specular
uniform vec4 Vsp; // view position  
uniform float Msh; // material shininess  

// Additional light sources (from the shapes)
uniform vec4 L1d;  // light source 1 diffuse (red)
uniform vec4 L1sp; // light source 1 position (cylinder)
uniform vec4 L2d;  // light source 2 diffuse (green)
uniform vec4 L2sp; // light source 2 position (sphere)
uniform vec4 L3d;  // light source 3 diffuse (blue)
uniform vec4 L3sp; // light source 3 position (cone)

// Output fragment color
out vec4 fColor;

void main() {
    // Emission and ambient terms
    vec4 Ie = Me; 
    vec4 Ia = La * Ma; 
   
    // Calculate direction vectors to light sources
    vec4 Lg = Lsp - position;   // Main light direction 
    vec4 L1g = L1sp - position; // Light 1 direction (cylinder)
    vec4 L2g = L2sp - position; // Light 2 direction (sphere)
    vec4 L3g = L3sp - position; // Light 3 direction (cone)
    
    // Normalize direction vectors
    vec3 L = normalize(Lg.xyz);
    vec3 L1 = normalize(L1g.xyz);
    vec3 L2 = normalize(L2g.xyz);
    vec3 L3 = normalize(L3g.xyz);
    
    // Normalize normal vector
    vec3 N = normalize(normal.xyz);
    
    // Calculate diffuse factors (N·L)
    float NL = max(dot(N, L), 0.0); 
    float NL1 = max(dot(N, L1), 0.0); 
    float NL2 = max(dot(N, L2), 0.0); 
    float NL3 = max(dot(N, L3), 0.0); 
    
    // Calculate diffuse components
    vec4 Id = Ld * Md * NL;
    vec4 I1d = L1d * Md * NL1;
    vec4 I2d = L2d * Md * NL2;
    vec4 I3d = L3d * Md * NL3;
 
    // Calculate view direction for specular
    vec4 Vg = Vsp - position;
    vec3 V = normalize(Vg.xyz);
    
    // Calculate half vectors for Blinn-Phong specular
    vec3 s = normalize(L + V);
    vec3 s1 = normalize(L1 + V);
    vec3 s2 = normalize(L2 + V);
    vec3 s3 = normalize(L3 + V);
    
    // Calculate specular factors
    float Ns = max(dot(N, s), 0.0); 
    float Ns1 = max(dot(N, s1), 0.0); 
    float Ns2 = max(dot(N, s2), 0.0); 
    float Ns3 = max(dot(N, s3), 0.0); 
    
    // Don't calculate specular if surface is facing away from light
    if (NL == 0.0) Ns = 0.0; 
    if (NL1 == 0.0) Ns1 = 0.0; 
    if (NL2 == 0.0) Ns2 = 0.0; 
    if (NL3 == 0.0) Ns3 = 0.0; 
    
    // Calculate specular components
    vec4 Is = Ms * Ls * pow(Ns, Msh); 
    vec4 I1s = Ms * L1d * pow(Ns1, Msh); 
    vec4 I2s = Ms * L2d * pow(Ns2, Msh); 
    vec4 I3s = Ms * L3d * pow(Ns3, Msh); 
 
    // Sum all lighting contributions and ensure reasonable range for color
    // Dividing by 2.0 instead of 3.0 to make the lighting brighter
    fColor = (Ie + Ia + (Id + I1d + I2d + I3d) + (Is + I1s + I2s + I3s)) / 1.5;
    fColor.a = 1.0; // Ensure full opacity
}