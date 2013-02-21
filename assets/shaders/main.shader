#version 410

vertex:
    in float occlusion;
    in vec3 normal, texcoord, s, t, light;
    in vec4 position;
    uniform mat4 mvp, modelview;

    out Data{
        vec3 texcoord, light;
        float depth, occlusion;
        mat3 matfn;
    } data;

    void main(void){
        data.occlusion = occlusion;
        data.texcoord = texcoord;
        data.depth = length((modelview * position).xyz);
        data.matfn = transpose(mat3(s, normal, t));
        data.light = light;
        gl_Position = mvp * position;
    }

fragment:
    import: spherical_harmonics
    import: util

    uniform vec2 viewport;
    uniform mat3 normalmatrix;
    uniform mat4 inverse_projection;
    uniform sampler2DArray material, normalmap, specularmap;

    in Data{
        vec3 texcoord, light;
        float depth, occlusion;
        mat3 matfn;
    } data;
    
    out vec3 fragment;

    void main(){
        vec3 eye_normal = get_eye_normal(viewport, inverse_projection);
        vec3 material_color = texture(material, data.texcoord).rgb;
        vec3 map_normal = normalize(texture(normalmap, data.texcoord).rgb);
        vec3 frag_normal = normalize(map_normal * data.matfn);
        vec3 eye_frag_normal = normalize(normalmatrix * frag_normal);

        vec3 outside = sh_light(frag_normal, beach);
        vec3 inside = sh_light(frag_normal, groove)*0.004;
        vec3 ambient = mix(outside, inside, data.occlusion);
        
        float frag_specular = texture(specularmap, data.texcoord).r;
        vec3 torch_color = vec3(1.0, 0.83, 0.42);
        float intensity = 2.0/pow(data.depth, 2);
        float lambert = abs(min(0, dot(eye_frag_normal, eye_normal)));
        float specular = pow(lambert, 1+frag_specular*8);
        vec3 torch = specular * intensity * torch_color;
        float highlight = pow(specular, 4) * intensity * frag_specular;
        
        vec3 color = (
            material_color * ambient +
            material_color * torch +
            highlight * torch_color +
            material_color * data.light
        );

        vec3 at_observer = fog(color, vec3(0.8), data.depth, 0.005);
        fragment = gamma(color);
    }
