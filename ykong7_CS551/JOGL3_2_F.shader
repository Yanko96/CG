#version	430	

in  vec4 color; // (interpolated) value from vertex shader


out vec4 fColor; // out to display


void main(void) { 

    fColor = color;  
 
}
