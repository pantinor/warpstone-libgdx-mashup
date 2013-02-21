package com.me.mygdxgame;

import com.badlogic.gdx.graphics.Texture;

public abstract class Renderer {

   public abstract void begin();

   public abstract void end();

   public abstract void render();

   public void endAndRender() {
      end();
      render();
   }

   public abstract Texture getTexture();
}
