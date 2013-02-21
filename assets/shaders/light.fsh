#ifdef GL_ES
   #define LOWP lowp
   precision mediump float;
#else
   #define LOWP
#endif

varying vec2 vTextureCoord;
varying vec3 vTransformedNormal;
varying vec4 vPosition;

uniform bool uUseLighting;
uniform bool uUseTextures;

uniform sampler2D uTexture;
uniform vec3 uAmbientColor;
uniform vec3 uPointLightingLocation;
uniform vec3 uPointLightingColor;

void main(void) {
	vec3 lightWeighting;
	if (!uUseLighting) {
		lightWeighting = vec3(1.0, 1.0, 1.0);
	} else {
		vec3 lightDirection = normalize(uPointLightingLocation - vPosition.xyz);
		float directionalLightWeighting = max(dot(normalize(vTransformedNormal), lightDirection), 0.0);
		lightWeighting = uAmbientColor + uPointLightingColor * directionalLightWeighting;
	}
	
    vec4 fragmentColor;
	if (uUseTextures) {
		fragmentColor = texture2D(uTexture, vec2(vTextureCoord.s, vTextureCoord.t));
	} else {
		fragmentColor = vec4(1.0, 1.0, 1.0, 1.0);
	}
	//lightWeighting += vPosition.y * 0.3;
	
	vec4 result = vec4(fragmentColor.rgb * lightWeighting, fragmentColor.a);
	gl_FragColor = result;
}