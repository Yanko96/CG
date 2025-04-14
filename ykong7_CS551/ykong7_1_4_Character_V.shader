#version 430

layout (location = 0) in vec3 position;

uniform vec4 vColor;    
out vec4 varyingColor;   

void main(void)
{
    gl_Position = vec4(position, 1.0);
    varyingColor = vColor; 
}