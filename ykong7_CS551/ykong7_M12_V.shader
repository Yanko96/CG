#version 430

// Matrices
uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 

// Input vertex attributes from VBO
layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iNormal;   // VBO: vbo[1]

// Output to fragment shader for lighting calculation
out vec4 position;
out vec4 normal;

void main() {
    // Transform vertex to clip space
    gl_Position = proj_matrix * mv_matrix * vec4(iPosition, 1.0);
    
    // Pass position in view space to fragment shader
    position = mv_matrix * vec4(iPosition, 1.0);
    
    // Transform normal by inverse-transpose of modelview matrix
    mat4 mv_it = transpose(inverse(mv_matrix)); 
    normal = mv_it * vec4(iNormal, 1.0); 
}