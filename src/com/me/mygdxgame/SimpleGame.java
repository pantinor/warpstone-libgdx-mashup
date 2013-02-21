package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Disposable;
import com.me.mygdxgame.Textures;

public abstract class SimpleGame implements ApplicationListener, InputProcessor {

   private List<Disposable> disposables = new ArrayList<Disposable>();

   protected <T extends Disposable> T tag(T disposable) {
      disposables.add(disposable);
      return disposable;
   }

   private float zoom = 1.0f;
   protected boolean perspective;
   protected Camera camera;

   private float aspectRatio;

   public SimpleGame(boolean perspective) {
      this.perspective = perspective;
   }

   public abstract void init();

   public abstract void draw(float delta);

   @Override
   public void create() {
      init();
      Gdx.input.setInputProcessor(this);
      Gdx.graphics.setVSync(true);
   }

   @Override
   public void render() {
      draw(Gdx.graphics.getDeltaTime());
   }

   public void resetCamera() {
      camera = createCamera();
   }

   public Camera createCamera() {
      if (perspective) {
         return new PerspectiveCamera(45, 2.0f * aspectRatio * zoom, 2.0f * zoom);
      } else {
         return new OrthographicCamera(2.0f * aspectRatio * zoom, 2.0f * zoom);
      }
   }

   public float getZoom() {
      return zoom;
   }

   public void setZoom(float zoom) {
      this.zoom = zoom;
      resetCamera();
   }

   @Override
   public boolean keyDown(int keycode) {
      return false;
   }

   @Override
   public boolean keyUp(int keycode) {
      return false;
   }

   @Override
   public boolean keyTyped(char character) {
      return false;
   }

   @Override
   public boolean touchDown(int x, int y, int pointer, int button) {
      return false;
   }

   @Override
   public boolean touchUp(int x, int y, int pointer, int button) {
      return false;
   }

   @Override
   public boolean touchDragged(int x, int y, int pointer) {
      return false;
   }

   @Override
   public boolean mouseMoved(int screenX, int screenY) {
      return false;
   }

   @Override
   public boolean scrolled(int amount) {
      return false;
   }

   @Override
   public void resize(int width, int height) {
      aspectRatio = (float) width / (float) height;
      resetCamera();
   }

   @Override
   public void pause() {
   }

   @Override
   public void resume() {
   }

   @Override
   public void dispose() {
      for (Disposable disposable : disposables) {
         disposable.dispose();
      }
      Textures.dispose();
   }
}
