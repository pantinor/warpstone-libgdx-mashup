package com.me.mygdxgame;

import org.antinori.dungeon.DungeonLevel;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.wavefront.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.mygdxgame.lights.LightManager;

public class MashupMain extends SimpleGame {
	
	private boolean started = false;

	private ShaderProgram defaultShader;
	private TextureRenderer normalRenderer;
	
	private Skin skin;
	private SpriteBatch batch;
	//private ShapeRenderer renderer;
	private Stage stage;

	private Actor actor;

	private GeometricObject circlingLight;;

	public Vector3 lightCenter = new Vector3(25f, 7f, 25f);
	public float radiusA = 13f;
	public float radiusB = 13f;
	
	private Vector3 cameraPosition = new Vector3(25f, 7f, 25f);
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
	
	LightManager lightManager;

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "my-gdx-game";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 768;
		new LwjglApplication(new MashupMain(), cfg);
	}

	public MashupMain() {
		super(true);
	}

	@Override
	public void init() {

		defaultShader = tag(new ShaderProgram(Gdx.files.internal("shaders/light.vsh").readString(), Gdx.files.internal("shaders/light.fsh").readString()));

		ShaderProgram simpleShader = tag(new ShaderProgram(Gdx.files.internal("shaders/simple.vsh").readString(), Gdx.files.internal("shaders/simple.fsh").readString()));
		normalRenderer = tag(new TextureRenderer(1920, 1080, simpleShader));
	
		batch = new SpriteBatch();
		//renderer = new ShapeRenderer();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		skin.getAtlas().getTextures().iterator().next().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		stage = new Stage(0, 0, false);
		
		
		Table table = new Table();
		stage.addActor(table);
		table.setPosition(200, 65);

		table.add(new Label("This is regular text.", skin));
		table.pack();


		FileHandle cstream = Gdx.files.internal("meshes/cube.obj");
		FileHandle sstream = Gdx.files.internal("meshes/sphere.obj");

		ObjLoader loader = new ObjLoader();
		StillModel cmodel = loader.loadObj(cstream, true);
		StillModel smodel = loader.loadObj(sstream, true);

		Mesh sphereMesh = smodel.getSubMeshes()[0].getMesh();
		Mesh cubeMesh = cmodel.getSubMeshes()[0].getMesh();
		
		
//		lightManager = new LightManager(1, LightQuality.FORWARD_VERTEX);
//		lightManager.setAmbient(new Color(0.3f,0.4f,0.3f,1f), new Vector3(0, 1, 0));
//		lightManager.fixReferences();


		circlingLight = new GeometricObject(defaultShader, sphereMesh, GL20.GL_TRIANGLES);
		circlingLight.setUseLighting(false);

		ROCK_TEXTURE = tag(new Texture(Gdx.files.internal("data/rock.png"), true));
		GRASS_TEXTURE = tag(new Texture(Gdx.files.internal("data/grass.png"), true));
		DIRT_TEXTURE = tag(new Texture(Gdx.files.internal("data/dirt.png"), true));
		MAP_TEXTURE = tag(new Texture(Gdx.files.internal("data/map.png"), true));


		dlevel = new DungeonLevel(0, 0, MAP_WIDTH, MAP_HEIGHT, MAP_CEILING);
		dlevel.initCubes(defaultShader, cubeMesh);
		
		actor = new Actor("Freddy", defaultShader, sphereMesh, GL20.GL_TRIANGLES, dlevel);
		actor.setScale(new Vector3(0.2f, 0.2f, 0.2f));
		actor.setPos(new Vector3(dlevel.dmap.getStartX(), 1f, dlevel.dmap.getStartY()));

	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.setViewport(width, height, false);
	}

	@Override
	public void draw(float delta) {

		if (!started) {
			camera.position.set(cameraPosition);
			camera.lookAt(dlevel.dmap.getStartX(), 0.2f, dlevel.dmap.getStartY());
			started = true;
		}

		camera.update();

		lightPosition += delta * 1.0f;

		float lx = (float) (radiusA * Math.cos(lightPosition));
		float ly = (float) (radiusB * Math.sin(lightPosition));
		Vector3 lightVector = new Vector3(lx, 0, ly).add(lightCenter);
		circlingLight.setPos(lightVector);
		circlingLight.setScale(new Vector3(0.1f, 0.1f, 0.1f));

		actor.update();

		normalRenderer.begin();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

		defaultShader.begin();

		defaultShader.setUniformf("uAmbientColor", 0.2f, 0.2f, 0.2f);
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
		normalRenderer.endAndRender();

		checkTileTouched();
		

		


	}

	@Override
	public boolean keyTyped(char character) {
		
		if (character == '1') {
			//do a view reset in case we get lost in space
			camera.position.set(actor.getPos().x+10, actor.getPos().y+5, actor.getPos().z+10);
			camera.lookAt(actor.getPos().x, actor.getPos().y, actor.getPos().z);
			return false;
		}

		if (character == 'w') cameraPosition.z += 1f;
		if (character == 'a') cameraPosition.x += 1f;
		if (character == 's') cameraPosition.z -= 1f;
		if (character == 'd') cameraPosition.x -= 1f;
		if (character == 'q') cameraPosition.y += 1f;
		if (character == 'e') cameraPosition.y -= 1f;

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
					if (path == null) {
						x += 0.5f;
						//path = dlevel.getPath(actor.getPos().x, actor.getPos().z, x, z);
					}
					if (path == null) {
						x -= 1f;
						//path = dlevel.getPath(actor.getPos().x, actor.getPos().z, x, z);
					}
					
					if (path != null) actor.setCurrentPath(path);
				}
				
				
			}

		}
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {

		Gdx.app.log("touchDragged", "x: " + x);

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
		
		Vector3 mousePos = new Vector3(x, y, 0);
		camera.unproject(mousePos);
		camera.lookAt(mousePos.x, mousePos.y, mousePos.z);
		Gdx.app.log("touchUp", "look at: " + mousePos.toString());
		
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
		
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		
		//stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		//stage.draw();
		
		batch.begin();
		batch.draw(MAP_TEXTURE, 16, 10, 16, 16, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), 1, 1, 0, 0, 0, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), false, false);
		batch.draw(texture, 16, 10, 16, 16, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), 1, 1, 0, 0, 0, MAP_TEXTURE.getWidth(), MAP_TEXTURE.getHeight(), false, false);
		batch.end();
	}
	


}
