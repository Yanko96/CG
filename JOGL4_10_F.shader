#version	450	

in  vec3 color; // (interpolated) value from vertex shader


out vec4 fColor; // out to display


void main(void) { 

    fColor = vec4(color, 1.0);  
 
}
