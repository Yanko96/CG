#version	430	

uniform float Width;            // Viewport Width 
uniform float Height;           // Viewport Height 
uniform float fogOffsetX;       // X offset for animating fog
uniform float fogOffsetY;       // Y offset for animating fog
uniform float fogDensityFactor; // Custom density factor per viewport

// Lighting parameters
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

// Additional light sources
uniform vec4 L1d;  // light source diffuse  
uniform vec4 L1sp; // light source position  
uniform vec4 L2d;  // light source diffuse  
uniform vec4 L2sp; // light source position  
uniform vec4 L3d;  // light source diffuse  
uniform vec4 L3sp; // light source position  

in vec4 position; // (interpolated) value from vertex shader
in vec4 normal;   // (interpolated) value from vertex shader
in vec2 texCoord; // (interpolated) texture coordinates

layout (binding=0) uniform sampler2D fogTex;  // fog texture

out vec4 fColor; // final output color

void main(void) {
    vec4 Ie = Me; 
    vec4 Ia = La * Ma; 
   
    vec4 Lg = Lsp - position;  
    vec4 L1g = L1sp - position;  
    vec4 L2g = L2sp - position; 
    vec4 L3g = L3sp - position; 
    
    vec3 L = normalize(Lg.xyz);   
    vec3 L1 = normalize(L1g.xyz); 
    vec3 L2 = normalize(L2g.xyz);
    vec3 L3 = normalize(L3g.xyz);
    
    vec3 N = normalize(normal.xyz); 
    
    float NL = max(dot(N, L), 0.0); 
    float NL1 = max(dot(N, L1), 0.0); 
    float NL2 = max(dot(N, L2), 0.0); 
    float NL3 = max(dot(N, L3), 0.0);
    
    vec4 Id = Ld * Md * NL;
    vec4 I1d = L1d * Md * NL1;
    vec4 I2d = L2d * Md * NL2;
    vec4 I3d = L3d * Md * NL3;
 
    vec4 Vg = Vsp - position; 
    vec3 V = normalize(Vg.xyz); 
    
    vec3 s = normalize(L + V);
    vec3 s1 = normalize(L1 + V);
    vec3 s2 = normalize(L2 + V);
    vec3 s3 = normalize(L3 + V);
    
    float Ns = max(dot(N, s), 0.0); 
    float Ns1 = max(dot(N, s1), 0.0); 
    float Ns2 = max(dot(N, s2), 0.0); 
    float Ns3 = max(dot(N, s3), 0.0);
    
    if (NL == 0.0) Ns = 0.0; 
    if (NL1 == 0.0) Ns1 = 0.0; 
    if (NL2 == 0.0) Ns2 = 0.0; 
    if (NL3 == 0.0) Ns3 = 0.0;
    
    vec4 Is = Ls * Ms * pow(Ns, Msh); 
    vec4 I1s = L1d * Ms * pow(Ns1, Msh); 
    vec4 I2s = L2d * Ms * pow(Ns2, Msh); 
    vec4 I3s = L3d * Ms * pow(Ns3, Msh); 
    
    vec4 lightingColor = (Ie + Ia + (Id + I1d + I2d + I3d) + (Is + I1s + I2s + I3s))/2.0;

    vec2 fogCoord = vec2(gl_FragCoord.x/Width, gl_FragCoord.y/Height);
    
    fogCoord.x = mod(fogCoord.x + fogOffsetX, 1.0);
    fogCoord.y = mod(fogCoord.y + fogOffsetY, 1.0);
    
    vec4 fogColor = texture(fogTex, fogCoord);
    
    if (abs(normal.z) > 0.99) {
        fColor = fogColor;
        return;
    }
    
    float density = (fogColor.r + fogColor.g + fogColor.b) / 3.0 * fogDensityFactor;
    
    float distance = length(Vg) / 1000.0;
    float fogFactor = exp(-density * distance);
    
    fColor = mix(fogColor, lightingColor, fogFactor);
}