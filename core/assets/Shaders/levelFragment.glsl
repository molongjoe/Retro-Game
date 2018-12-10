#ifdef GL_ES
    precision mediump float;
#endif

//Original texture sampled from the scene
uniform sampler2D u_texture;
uniform vec4 u_tint;

//In - From the vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {

    vec4 texColor = texture(u_texture, v_texCoords);

    gl_FragColor = texColor * u_tint;

}