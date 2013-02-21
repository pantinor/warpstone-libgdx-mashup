#ifdef GL_ES
   #define LOWP lowp
   precision mediump float;
#else
   #define LOWP
#endif

uniform sampler2D uTexture;
uniform float multiplyer;
varying vec2 vTextureCoord;

void main(void) {
	vec4 color = texture2D(uTexture, vTextureCoord);
	vec4 color2 = color * multiplyer;
	color2.a = color.a;
	gl_FragColor = color2;
}