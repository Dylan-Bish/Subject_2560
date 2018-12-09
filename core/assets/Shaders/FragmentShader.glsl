varying vec4 v_color;
varying vec2 v_texCoord0;
varying vec2 v_lightPos;
varying vec4 v_position;

uniform sampler2D u_texture;

void main() {
    int dropoffdist = 400;
    float dist = sqrt(pow(abs(v_lightPos.x - v_position.x), 2) + pow(abs(v_lightPos.y - v_position.y), 2));
    gl_FragColor = v_color * texture2D(u_texture, v_texCoord0) *0.5f*(dropoffdist/max(1,dist));
}
