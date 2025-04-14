#version 430

uniform mat4 proj_matrix; 
uniform mat4 mv_matrix; 

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

uniform vec4 L1d; // light source diffuse  
uniform vec4 L1sp; // light source position  
uniform vec4 L2d; // light source diffuse  
uniform vec4 L2sp; // light source position  
uniform vec4 L3d; // light source diffuse  
uniform vec4 L3sp; // light source position  

layout (location = 0) in vec3 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iNormal; // VBO: vbo[1]

out vec4 color; // output to fragment shader

void main(void) {    
    gl_Position = proj_matrix * mv_matrix * vec4(iPosition, 1.0);    

    // for lighting calculation
    vec4 position = mv_matrix * vec4(iPosition, 1.0);
    
    // normal is transformed by inverse-transpose of the current matrix
    mat4 mv_it = transpose(inverse(mv_matrix)); 
    vec4 normal = mv_it * vec4(iNormal, 1.0); 

    // Lighting calculation      
    vec4 Ie = Me; 
    vec4 Ia = La * Ma; 
   
    // calculate diffuse component
    vec4 Lg = Lsp - position; // 全局光源方向
    vec4 L1g = L1sp - position; // Y 字母光源方向 
    vec4 L2g = L2sp - position; // K 字母光源方向 
    vec4 L3g = L3sp - position; // Z 字母光源方向
    
    // 标准化方向向量
    vec3 L = normalize(Lg.xyz); // 全局光源
    vec3 L1 = normalize(L1g.xyz); // Y 光源
    vec3 L2 = normalize(L2g.xyz); // K 光源
    vec3 L3 = normalize(L3g.xyz); // Z 光源
    vec3 N = normalize(normal.xyz); // 法线
    
    // 计算漫反射强度 (N·L)
    float NL = max(dot(N, L), 0.0); 
    float NL1 = max(dot(N, L1), 0.0); 
    float NL2 = max(dot(N, L2), 0.0); 
    float NL3 = max(dot(N, L3), 0.0); 
    
    // 漫反射计算
    vec4 Id = Ld * Md * NL;
    vec4 I1d = L1d * Md * NL1;
    vec4 I2d = L2d * Md * NL2;
    vec4 I3d = L3d * Md * NL3;
 
    // 计算镜面反射
    vec4 Vg = Vsp - position; // 视线方向
    vec3 V = normalize(Vg.xyz); // 标准化视线方向
    
    // 计算半角向量
    vec3 H = normalize(L + V);
    vec3 H1 = normalize(L1 + V);
    vec3 H2 = normalize(L2 + V);
    vec3 H3 = normalize(L3 + V);
    
    // 计算镜面反射系数 (N·H)
    float NH = max(dot(N, H), 0.0);
    float NH1 = max(dot(N, H1), 0.0);
    float NH2 = max(dot(N, H2), 0.0);
    float NH3 = max(dot(N, H3), 0.0);
    
    // 在无漫反射时不计算镜面反射
    if (NL == 0.0) NH = 0.0; 
    if (NL1 == 0.0) NH1 = 0.0; 
    if (NL2 == 0.0) NH2 = 0.0; 
    if (NL3 == 0.0) NH3 = 0.0; 
    
    // 计算镜面反射分量
    vec4 Is = Ms * Ls * pow(NH, Msh); 
    vec4 I1s = Ms * L1d * pow(NH1, Msh); 
    vec4 I2s = Ms * L2d * pow(NH2, Msh); 
    vec4 I3s = Ms * L3d * pow(NH3, Msh); 
 
    // 最终颜色 = 发射光 + 环境光 + 4个光源的(漫反射 + 镜面反射)
    color = Ie + Ia + (Id + I1d + I2d + I3d) + (Is + I1s + I2s + I3s);
    
    // 防止颜色溢出
    color = clamp(color, 0.0, 1.0);
}