#ifdef GL_ES
   #define LOWP lowp
   precision mediump float;
#else
   #define LOWP
#endif

uniform sampler2D uTexture;
varying vec2 vTextureCoord;

void main(void) {
	float blurSize = 1.0 / 512.0;
	vec4 sum = vec4(0.0);
	
	float factor = 1.0 / 21.0;
	
	
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 4.0 * blurSize)) * 0.05;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 3.0 * blurSize)) * 0.09;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 2.0 * blurSize)) * 0.12;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - blurSize)) * 0.15;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y)) * 0.16;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + blurSize)) * 0.15;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 2.0 * blurSize)) * 0.12;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 3.0 * blurSize)) * 0.09;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 4.0  *blurSize)) * 0.05;
	
	/*
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 10.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 9.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 8.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 7.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 6.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 5.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 4.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 3.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 2.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 1.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y - 0.0 * blurSize)) * factor;
	
	
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 10.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 9.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 8.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 7.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 6.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 5.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 4.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 3.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 2.0 * blurSize)) * factor;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y + 1.0 * blurSize)) * factor;
	*/
	
	sum.a = 1.0;
	gl_FragColor = sum;
}