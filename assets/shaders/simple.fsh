#ifdef GL_ES
   #define LOWP lowp
   precision mediump float;
#else
   #define LOWP
#endif

uniform sampler2D uTexture;
varying vec2 vTextureCoord;

void main(void) {
	gl_FragColor = texture2D(uTexture, vTextureCoord);
}