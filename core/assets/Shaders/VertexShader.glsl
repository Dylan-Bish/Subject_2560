attribute vec4 a_color;
attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform vec2 u_lightPos;

varying vec4 v_color;
varying vec2 v_texCoord0;
varying vec2 v_lightPos;
varying vec4 v_position;

void main() {
    v_color = a_color;
    v_position = vec4(a_position, 1);
    v_lightPos = u_lightPos;
    v_position.xy    = a_position.xy;
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projTrans * vec4(a_position, 1);
}
