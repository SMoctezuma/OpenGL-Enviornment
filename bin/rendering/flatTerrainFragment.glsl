#version 330

flat in vec3 pass_colors;

out vec4 out_colors;


void main(void){

	out_colors = vec4(pass_colors, 1.0);

}
