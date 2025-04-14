#version 430

layout (location = 0) in vec3 iPos;
layout (location = 1) in vec3 iClr;

uniform int useUniformColor;
uniform vec3 uniformColor;

out vec3 color;

void main() {
    gl_Position = vec4(iPos, 1.0);
    color = (useUniformColor == 1) ? uniformColor : iClr;
}

// CircleBouncingPoints_F.shader
#version 430

in vec3 color;
out vec4 fColor;

void main() {
    fColor = vec4(color, 1.0);
}