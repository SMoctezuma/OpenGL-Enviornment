#version 330
const vec3 watercolors = vec3(0,0,0);
//const vec3 watercolors = vec3(0.604, 0.867, 0.851);
const float fresnelReflective = 0.5;
const float edgeSoftness = 1;
const float minBlueness = 0.4;
const float maxBlueness = 0.8;
const float murkyDepth = 14;

out vec4 out_colors;

in vec4 pass_clipSpaceGrid;
in vec4 pass_clipSpaceReal;
in vec3 pass_normal;
in vec3 pass_toCameraVector;
in vec3 pass_specular;
in vec3 pass_diffuse;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D depthTexture;
uniform vec2 nearFarPlanes;

vec3 applyMurkiness(vec3 refractcolors, float waterDepth){
	float murkyFactor = clamp(waterDepth / murkyDepth, 0.0, 1.0);
	float murkiness = minBlueness + murkyFactor * (maxBlueness - minBlueness);
	return mix(refractcolors, watercolors, murkiness);
}

float toLinearDepth(float zDepth){
	float near = nearFarPlanes.x;
	float far = nearFarPlanes.y;
	return 2.0 * near * far / (far + near - (2.0 * zDepth - 1.0) * (far - near));
}

float calculateWaterDepth(vec2 texCoords){
	float depth = texture(depthTexture, texCoords).r;
	float floorDistance = toLinearDepth(depth);
	depth = gl_FragCoord.z;
	float waterDistance = toLinearDepth(depth);
	return floorDistance - waterDistance;
}

float calculateFresnel(){
	vec3 viewVector = normalize(pass_toCameraVector);
	vec3 normal = normalize(pass_normal);
	float refractiveFactor = dot(viewVector, normal);
	refractiveFactor = pow(refractiveFactor, fresnelReflective);
	return clamp(refractiveFactor, 0.0, 1.0);
}

vec2 clipSpaceToTexCoords(vec4 clipSpace){
	vec2 ndc = (clipSpace.xy / clipSpace.w);
	vec2 texCoords = ndc / 2.0 + 0.5;
	return clamp(texCoords, 0.002, 0.998);
}

void main(void){

	vec2 texCoordsReal = clipSpaceToTexCoords(pass_clipSpaceReal);
	vec2 texCoordsGrid = clipSpaceToTexCoords(pass_clipSpaceGrid);
	
	vec2 refractionTexCoords = texCoordsGrid;
	vec2 reflectionTexCoords = vec2(texCoordsGrid.x, 1.0 - texCoordsGrid.y);
	float waterDepth = calculateWaterDepth(texCoordsReal);
	
	vec3 refractcolors = texture(refractionTexture, refractionTexCoords).rgb;
	vec3 reflectcolors = texture(reflectionTexture, reflectionTexCoords).rgb;
	
	//apply some blueness
	refractcolors = applyMurkiness(refractcolors, waterDepth);
	reflectcolors = mix(reflectcolors, watercolors, minBlueness);
	
	vec3 finalcolors = mix(reflectcolors, refractcolors, calculateFresnel());
	finalcolors = finalcolors * pass_diffuse + pass_specular;
	
	out_colors = vec4(finalcolors, 1.0);
	
	//apply soft edges
	out_colors.a = clamp(waterDepth / edgeSoftness, 0.0, 1.0);

}
