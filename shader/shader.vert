#version 300 es

in vec3 position;

out vec3 colour;
void main(){
	gl_Position = vec4(position, 1.0);
	colour = vec3(positions.x+0.5, 1.0,position.y+0.5);
}