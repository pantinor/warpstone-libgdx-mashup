#ifdef GL_ES
   #define LOWP lowp
   precision mediump float;
#else
   #define LOWP
#endif

uniform sampler2D uTexture;
varying vec2 vTextureCoord;

void main( void )
{
	float n = 2.0 * asin(1.0) / 0.1;

	vec2 coo = vTextureCoord;
	
	coo -= vec2(0.5, 0.5);

	float ang = atan(coo.y, coo.x);
	float len = sqrt(coo.x * coo.x + coo.y * coo.y);

	float count = ang / n;

	ang = mod(ang, n);

	if(mod(count, 2.0) < 1.0){
		ang = n - ang;
	}

	coo.x = cos(ang * 0.1) * len;
	coo.y = sin(ang * 0.1) * len;

	coo += vec2(0.5, 0.5);
	
	//coo *= 0.1;

	gl_FragColor = texture2D(uTexture, coo);
}
