package com.me.mygdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.me.mygdxgame.lights.Shapes;

public class TextureRenderer extends Renderer implements Disposable {

   private FrameBuffer frameBuffer;
   private ShaderProgram shader;
   private Texture texture;
   private Mesh quad;

   public TextureRenderer(int width, int height, ShaderProgram shader) {
      this(width, height, shader, true);
   }

   public TextureRenderer(int width, int height, ShaderProgram shader, boolean inverted) {
      this.shader = shader;
      frameBuffer = new FrameBuffer(Format.RGBA8888, width, height, true);
      texture = frameBuffer.getColorBufferTexture();
      texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      quad = Shapes.makeQuad(inverted);
   }

   @Override
   public void begin() {
      frameBuffer.begin();
   }

   @Override
   public void end() {
      frameBuffer.end();
   }

   @Override
   public void render() {
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
      shader.begin();
      texture.bind();
      quad.render(shader, GL20.GL_TRIANGLE_FAN);
      shader.end();
   }

   @Override
   public void endAndRender() {
      end();
      render();
   }

   @Override
   public Texture getTexture() {
      return texture;
   }

   @Override
   public void dispose() {
      frameBuffer.dispose();
      quad.dispose();
   }
}
