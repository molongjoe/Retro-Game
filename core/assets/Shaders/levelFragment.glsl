#ifdef GL_ES
    precision mediump float;
#endif

//Original texture sampled from the scene
uniform sampler2D u_texture;

//In - From the vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {

    gl_FragColor = texture2D(u_texture, v_texCoords) * vec4(0.97, 1, 0.67, 1.0);
}