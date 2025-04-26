#version	430	

uniform vec3 rColor; 

out vec4 fColor; // out to display

void	main(void)	{	
 
    fColor = vec4(rColor, 1);      
}