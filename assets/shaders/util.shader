vec3 get_eye_normal(vec2 viewport, mat4 inverse_projection){
    vec4 device_normal = vec4(((gl_FragCoord.xy/viewport)-0.5)*2.0, 0.0, 1.0);
    return normalize((inverse_projection * device_normal).xyz);
}

vec3 gamma(vec3 color){
    return pow(color, vec3(1.0/2.0));
}

vec3 fog(vec3 color, vec3 fcolor, float depth, float density){
    const float e = 2.71828182845904523536028747135266249;
    float f = pow(e, -pow(depth*density, 2));
    return mix(fcolor, color, f);
}
