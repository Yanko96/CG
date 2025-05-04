#version 430

// Transformation matrices
uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 

// Texture animation parameters
uniform float textureRotation = 0.0;
uniform float texScaleS = 1.0;
uniform float texScaleT = 1.0;
uniform int useTexture = 0;

// Lighting parameters
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

// Additional lights
uniform vec4 L1d;  // light source diffuse  
uniform vec4 L1sp; // light source position  
uniform vec4 L2d;  // light source diffuse  
uniform vec4 L2sp; // light source position  
uniform vec4 L3d;  // light source diffuse  
uniform vec4 L3sp; // light source position  

// Fog parameters
uniform float Width;
uniform float Height;
uniform float fogOffsetX;
uniform float fogOffsetY;
uniform float fogDensityFactor;

// Input vertex attributes
layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iNormal;   // VBO: vbo[1]
layout (location = 2) in vec2 iTexCoord; // VBO: vbo[2]

// Output to fragment shader
out vec4 position;
out vec4 normal;
out vec2 texCoord;

void main() {
    // Transform vertex to clip space
    gl_Position = proj_matrix * mv_matrix * vec4(iPosition, 1.0);
    
    // Pass position in view space to fragment shader
    position = mv_matrix * vec4(iPosition, 1.0);
    
    // Transform normal by inverse-transpose of modelview matrix
    mat4 mv_it = transpose(inverse(mv_matrix)); 
    normal = mv_it * vec4(iNormal, 1.0); 
    
    // Process texture coordinates if texturing is enabled
    if (useTexture == 1) {
        // Apply texture rotation and scaling
        float s = iTexCoord.s;
        float t = iTexCoord.t;
        
        // Rotate texture coordinates around center (0.5, 0.5)
        float s_center = s - 0.5;
        float t_center = t - 0.5;
        float s_rot = s_center * cos(textureRotation * 6.28318) - t_center * sin(textureRotation * 6.28318);
        float t_rot = s_center * sin(textureRotation * 6.28318) + t_center * cos(textureRotation * 6.28318);
        s = s_rot + 0.5;
        t = t_rot + 0.5;
        
        // Apply scaling
        s = s * texScaleS;
        t = t * texScaleT;
        
        texCoord = vec2(s, t);
    } else {
        // Default texture coordinates if texturing is disabled
        texCoord = vec2(iPosition.x, iPosition.y);
    }
}