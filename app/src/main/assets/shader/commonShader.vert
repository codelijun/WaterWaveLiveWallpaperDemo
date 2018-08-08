uniform mat4 uMVPMatrix; 
attribute vec4 vPosition;
attribute vec2 aTexcoord;
varying vec2 vTexcoord;

void main() {
    gl_Position = uMVPMatrix * vPosition;
    vTexcoord = aTexcoord;
}