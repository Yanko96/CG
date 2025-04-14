#version	430	

uniform float sPos, sPos1; // value from JOGL program

void	main(void)	{	

	//logical coordinates: -1 to 1 along x, y, and z in homogeneous coordinates. 
	gl_Position = vec4(sPos, sPos1, 0.0, 1.0);	
	
}