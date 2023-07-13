#version 150

#moj_import <fog.glsl>
#moj_import <lygia/generative/snoise.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;
uniform mat4 DyeMat;

in float vertexDistance;
in vec4 vertexColor;
in vec4 dyeColor;
in vec2 texCoord0;
in vec2 texCoord1;
in vec2 texCoord3;
in vec4 normal;
in vec4 worldPos;

out vec4 fragColor;

void main() {
    vec4 base = texture(Sampler0, texCoord0) * ColorModulator * vertexColor;

    if (base.a < 0.1) {
        discard;
    }

    vec4 dyePos = DyeMat * worldPos;
    vec2 pos = (vec2(dyePos.x, dyePos.y) / 16.0);
    float x = snoise(pos);

    vec4 color = base + (texture(Sampler3, texCoord3 + vec2(0.0, x)) * dyeColor);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}