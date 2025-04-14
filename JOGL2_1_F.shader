#version	430	

in  vec3 color; // (interpolated) value from vertex shader
in vec2 tc; // (interpolated) texture coordinates

layout	(binding=0)	uniform	sampler2D	samp; 
layout	(binding=1)	uniform	sampler2D	samp1; 


out vec4 fColor; // out to display


void main(void) { 

    fColor = vec4(color, 1.0);  
 // fColor = texture(samp, tc); // read the texture-pixel
 // fColor = 0.5*fColor + 0.5*texture(samp, tc); // integrate color and texture
    fColor = 0.2*fColor + 0.3*texture(samp1, tc) + 0.5*texture(samp, tc); // blend
  //fColor = fColor*(0.5*texture(samp1, tc) + 0.5*texture(samp, tc)); // modulate

}
