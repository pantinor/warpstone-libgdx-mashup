#version 410

vertex:
    in vec3 normal;
    in vec4 position;
    
    out Data{
        vec3 normal;
        vec4 position;
    } vdata;

    void main(void){
        vdata.position = position;
        vdata.normal = normal;
    }

geometry:
    layout(triangles) in;
    layout(line_strip, max_vertices = 3) out;

    uniform mat4 mvp;
    
    in Data{
        vec3 normal;
        vec4 position;
    } vdata[3];

    out Data{
        vec3 color;
    } gdata;

    void main(){
        vec4 middle = (vdata[0].position + vdata[1].position + vdata[2].position)/3;
        middle.w = 1.0;
        vec3 normal = normalize((vdata[0].normal + vdata[1].normal + vdata[2].normal)/3);

        gl_Position = mvp * middle;
        gdata.color = vec3(0);
        EmitVertex();
        
        gl_Position = mvp * (middle + vec4(normal*0.4, 0));
        gdata.color = normal*0.5+0.5;
        EmitVertex();

        EndPrimitive();
    }

fragment:
    in Data{
        vec3 color;
    } gdata;

    out vec3 fragment;

    void main(){
        fragment = gdata.color;
    }
