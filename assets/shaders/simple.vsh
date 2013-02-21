attribute vec4 a_position;
attribute vec2 a_texCoord;

varying vec2 vTextureCoord;

void main(void) {
	gl_Position = a_position;
	vTextureCoord = a_texCoord;
}