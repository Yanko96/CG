#version 430

uniform float xPos;  
uniform float yPos;  

void main(void) {
    gl_Position = vec4(xPos, yPos, 0.0, 1.0);
}