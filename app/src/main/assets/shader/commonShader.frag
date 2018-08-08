//precision mediump float;
//varying vec2 vTexcoord;
//uniform sampler2D uTexture;

//uniform float time;
//uniform vec2 resolution;
//uniform float rippleOffset;
//uniform float rippleCenterUvX;
//uniform float rippleCenterUvY;

//void main() {
    // Set the origin.
    //vec2 cPos = -1.0 + 2.0 * vTexcoord;
//    cPos.x -= rippleCenterUvX;
//    cPos.y -= rippleCenterUvY;
//
//    // Don't stretch the shape.
//    float ratio = resolution.x / resolution.y;
//    cPos.x *= ratio;

    // Distance from the origin.
//    float cLength = length(cPos);

//    float velocity = 25.0;
//    float speed = velocity - 10.0;

//    vec2 uv = vTexcoord + (cPos / cLength) * cos(cLength * velocity - time * speed) * rippleOffset;
//    gl_FragColor = texture2D(uTexture, uv);
//}

#ifdef GL_ES
precision highp float;
#endif

uniform float time;
uniform vec2 resolution;
uniform sampler2D uTexture;
uniform float rippleOffset;
uniform float rippleCenterUvX;
uniform float rippleCenterUvY;
varying vec2 vTexcoord;

void main(void) {
vec2 cPos = -1.0 + 2.0 * vTexcoord;

cPos.x -= rippleCenterUvX;
cPos.y -= rippleCenterUvY;

float cLength = length(cPos);

vec2 uv = vTexcoord + (cPos/cLength)*cos(cLength*12.0-time*4.0)*rippleOffset;
vec3 col = texture2D(uTexture,uv).xyz;

gl_FragColor = vec4(col,1.0);
}