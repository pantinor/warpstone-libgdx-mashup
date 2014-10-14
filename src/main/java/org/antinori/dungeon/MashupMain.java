package org.antinori.dungeon;

import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;


public class MashupMain extends SimpleGame {
	
	private boolean started = false;

	private ShaderProgram defaultShader;
	
	private SpriteBatch batch;
	private Stage stage;

	private Actor actor;

	private GeometricObject circlingLight;;

	public Vector3 lightCenter = new Vector3(25f, 7f, 25f);
	public float radiusA = 13f;
	public float radiusB = 13f;
	
	public static final float CAMERA_HEIGHT = 25f;
	private Vector3 cameraPosition = new Vector3(50f, CAMERA_HEIGHT, 50f);
	private float lightPosition = 0;

	private DungeonLevel dlevel;

	public static final int MAP_WIDTH = 50;
	public static final int MAP_HEIGHT = 50;
	public static final int MAP_CEILING = 6;

	public static Texture ROCK_TEXTURE;
	public static Texture GRASS_TEXTURE;
	public static Texture DIRT_TEXTURE;
	public static Texture MAP_TEXTURE;

	//properties for selecting cube with mouse clicks
	final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
	final Vector3 intersection = new Vector3();
	final Vector3 curr = new Vector3();
	final Vector3 last = new Vector3(-1, -1, -1);
	final Vector3 delta = new Vector3();
	Cube lastSelectedTile = null;
	
    public ModelBatch modelBatch;
    public AssetManager assets;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
		
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "my-gdx-game";
		cfg.width = 1280;
		cfg.height = 768;
		new LwjglApplication(new MashupMain(), cfg);
	}

	public MashupMain() {
		super(false); //use orthogonal
	}

	@Override
	public void init() {

		defaultShader = tag(new ShaderProgram(
				Gdx.files.classpath("shaders/light.vsh").readString(), 
				Gdx.files.classpath("shaders/light.fsh").readString()));
		
		batch = new SpriteBatch();
		
		stage = new Stage();


		FileHandle cstream = Gdx.files.classpath("meshes/cube.obj");
		FileHandle sstream = Gdx.files.classpath("meshes/sphere.obj");
		FileHandle hexstream = Gdx.files.classpath("meshes/circle.obj");
		
		modelBatch = new ModelBatch();
		
		ModelLoader loader = new ObjLoader();
		
		Model cmodel = loader.loadModel(cstream);
		Model smodel = loader.loadModel(sstream);
		Model hmodel = loader.loadModel(hexstream);
					
		Mesh sphereMesh = smodel.nodes.get(0).parts.get(0).meshPart.mesh;
		Mesh cubeMesh = cmodel.nodes.get(0).parts.get(0).meshPart.mesh;
		Mesh hexMesh = hmodel.nodes.get(0).parts.get(0).meshPart.mesh;
		
		Material cmaterial = cmodel.nodes.get(0).parts.get(0).material;
		
		
		circlingLight = new GeometricObject(defaultShader, sphereMesh, GL20.GL_TRIANGLES);
		circlingLight.setUseLighting(false);
		circlingLight.setScale(new Vector3(0.1f, 0.1f, 0.1f));


		ROCK_TEXTURE = tag(new Texture(Gdx.files.classpath("data/rock.png"), true));
		GRASS_TEXTURE = tag(new Texture(Gdx.files.classpath("data/grass.png"), true));
		DIRT_TEXTURE = tag(new Texture(Gdx.files.classpath("data/dirt.png"), true));
		MAP_TEXTURE = tag(new Texture(Gdx.files.classpath("data/map.png"), true));


		dlevel = new DungeonLevel(0, 0, MAP_WIDTH, MAP_HEIGHT, MAP_CEILING);
		dlevel.initCubes(defaultShader, cubeMesh);
		
		actor = new Actor("Freddy", defaultShader, hexMesh, GL20.GL_TRIANGLES, dlevel);
		actor.setScale(new Vector3(0.3f, 0.3f, 0.3f));
		actor.setPos(new Vector3(dlevel.dmap.getStartX(), 1f, dlevel.dmap.getStartY()));

	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		//stage.getViewport().setScreenHeight(height);
		//stage.getViewport().setScreenWidth(width);

	}

	@Override
	public void draw(float delta) {
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

		if (!started) {
			camera.position.set(cameraPosition);
			camera.lookAt(dlevel.dmap.getStartX(), 0.2f, dlevel.dmap.getStartY());
			if (camera instanceof OrthographicCamera) {
				OrthographicCamera ocam = (OrthographicCamera) camera;
				ocam.zoom = 4.4f;
			}
			started = true;
		}

		camera.update();

		lightPosition += delta * 1.0f;

		float lx = (float) (radiusA * Math.cos(lightPosition));
		float ly = (float) (radiusB * Math.sin(lightPosition));
		Vector3 lightVector = new Vector3(lx, 0, ly).add(lightCenter);
		circlingLight.setPos(lightVector);

		actor.update();

		
		defaultShader.begin();

		defaultShader.setUniformf("uAmbientColor", 0.3f, 0.3f, 0.3f);
		defaultShader.setUniformf("uPointLightingColor", 1.0f, 0.83f, 0.42f);
		defaultShader.setUniformf("uPointLightingLocation", lightVector.x, lightVector.y, lightVector.z);
		defaultShader.setUniformMatrix("uPMatrix", camera.combined);
		

		Cube[][][] cubes = dlevel.getCubes();
		for (int i = 0; i < MAP_WIDTH; i++) {
			for (int j = 0; j < MAP_HEIGHT; j++) {
				for (int k = 0; k < MAP_CEILING; k++) {
					Cube cube = cubes[i][j][k];
					if (cube != null) {
						cube.render();
					}
				}
			}
		}

		circlingLight.render();

		actor.render();

		drawMiniMap();

		defaultShader.end();


		checkTileTouched();

	}

	@Override
	public boolean keyTyped(char character) {
		
		if (character == '1') {
			//do a view reset in case we get lost in space
			//camera.position.set(pirate.getPosVec3().x+10, CAMERA_HEIGHT, pirate.getPosVec3().z+10);
			//camera.lookAt(pirate.getPosVec3().x, pirate.getPosVec3().y, pirate.getPosVec3().z);
			return false;
		}

		if (character == 'w') cameraPosition.z += 1f;
		if (character == 'a') cameraPosition.x += 1f;
		if (character == 's') cameraPosition.z -= 1f;
		if (character == 'd') cameraPosition.x -= 1f;
		//if (character == 'q') cameraPosition.y += 1f;
		//if (character == 'e') pirate.setDirection(Pirate.SOUTH);

		camera.position.set(cameraPosition);

		return false;
	}

	private void checkTileTouched() {
		if (Gdx.input.justTouched()) {
			
			Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
			int x = (int) intersection.x;
			int z = (int) intersection.z;
			

			if (x >= 0 && x < MAP_WIDTH && z >= 0 && z < MAP_HEIGHT) {
				if (lastSelectedTile != null) lastSelectedTile.setUseLighting(true);
				Cube[][][] cubes = dlevel.getCubes();
				Cube cube = cubes[x][z][0];
				if (cube != null) cube.setUseLighting(false);
				lastSelectedTile = cube;
				
				
				if (actor.getCurrentPath() == null) {
					NavPath path = dlevel.getPath(actor.getPos().x, actor.getPos().z, x, z);
					if (path != null) actor.setCurrentPath(path);
				}
				
				
			}

		}
	}
	
	@Override
	public boolean scrolled(int amount) {

		if (camera instanceof OrthographicCamera) {
			OrthographicCamera ocam = (OrthographicCamera) camera;
			ocam.zoom += amount * 0.2f;
			//Gdx.app.log("scrolled", "ocam.zoom: " + ocam.zoom);
		}

		return false;

	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {

		Gdx.app.log("touchDragged", "x: " + x + " y:" + y);

		Ray pickRay = camera.getPickRay(x, y);
		Intersector.intersectRayPlane(pickRay, xzPlane, curr);

		if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
			pickRay = camera.getPickRay(last.x, last.y);
			Intersector.intersectRayPlane(pickRay, xzPlane, delta);
			delta.sub(curr);
			camera.position.add(delta.x, 0, delta.z);
		}
		last.set(x, y, 0);
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		
		//Vector3 mousePos = new Vector3(x, y, 0);
		//camera.unproject(mousePos);
		//camera.lookAt(mousePos.x, mousePos.y, mousePos.z);
		//Gdx.app.log("touchUp", "look at: " + mousePos.toString());
		
		last.set(-1, -1, -1);
		return false;
	}
	
	
	
	public void drawMiniMap() {
				
		Pixmap pixmap = new Pixmap(MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), Format.RGBA8888);
		pixmap.setColor(0.3f, 0.3f, 0.3f, 0.7f);
		double[][] heights = dlevel.dmap.getHeightValues();
		for (int x = 0; x < dlevel.getWidthInTiles(); x++) {
			for (int y = 0; y < dlevel.getHeightInTiles(); y++) {
				if (heights[x][y] > 0) {
					pixmap.fillRectangle(8 + (x * 3), 8 + (y * 3), 3, 3);
				}
			}
		}
		pixmap.setColor(1f, 0f, 0f, 0.7f);
		pixmap.fillRectangle(8 + ((int)actor.getPos().x * 3), 8 + ((int)actor.getPos().z * 3), 3, 3);
		//pixmap.fillRectangle(8 + ((int)pirate.getMapX() * 3), 8 + ((int)pirate.getMapY() * 3), 3, 3);
		
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		
		batch.begin();
		batch.draw(MAP_TEXTURE, 16, 10, 16, 16, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), 1, 1, 0, 0, 0, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), false, false);
		batch.draw(texture, 16, 10, 16, 16, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), 1, 1, 0, 0, 0, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), false, false);
		batch.end();
	}
	


}
