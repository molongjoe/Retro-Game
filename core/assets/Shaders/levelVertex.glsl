//MVP matrix
uniform mat4 u_projTrans;

//In from the spriteBatch attributes
attribute vec2 a_texCoord0;
attribute vec4 a_position;
attribute vec4 a_color;

//Out to the fragment shader
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}