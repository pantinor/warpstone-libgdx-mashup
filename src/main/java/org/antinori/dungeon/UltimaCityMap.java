package org.antinori.dungeon;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class UltimaCityMap implements ApplicationListener, InputProcessor {

    private ModelBuilder builder = new ModelBuilder();

    protected Camera camera;
    private Vector3 cameraPosition;

    public Environment environment;
    public CameraInputController inputController;

    public static final int MAP_WIDTH = 32;
    public static final int MAP_HEIGHT = 32;
    public static final int MAP_CEILING = 6;

    Box[][][] cubes = new Box[MAP_CEILING][MAP_WIDTH][MAP_HEIGHT];

    public ModelBatch modelBatch;

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "UltimaCityMap";
        cfg.width = 1280;
        cfg.height = 768;
        new LwjglApplication(new UltimaCityMap(), cfg);
    }

    @Override
    public void create() {

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        load("paws.ult");

        createAxes();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.near = 0.1f;
        camera.far = 1000f;

        cameraPosition = new Vector3(15, 7, 28);
        camera.position.set(cameraPosition);
        camera.lookAt(15, 1, 15);

        inputController = new CameraInputController(camera);
        inputController.rotateLeftKey = inputController.rotateRightKey = inputController.forwardKey = inputController.backwardKey = 0;
        inputController.translateUnits = 30f;

        Gdx.input.setInputProcessor(new InputMultiplexer(this, inputController));

    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.update();

        modelBatch.begin(camera);

        for (int z = 0; z < MAP_CEILING; z++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    Box box = cubes[z][x][y];
                    if (box != null) {
                        modelBatch.render(box.instance, environment);
                    }
                }
            }
        }

        modelBatch.render(axesInstance);

        modelBatch.end();

    }

    @Override
    public boolean keyTyped(char character) {

        if (character == 'w') {
            cameraPosition.z += 1f;
        }
        if (character == 'a') {
            cameraPosition.x += 1f;
        }
        if (character == 's') {
            cameraPosition.z -= 1f;
        }
        if (character == 'd') {
            cameraPosition.x -= 1f;
        }

        camera.position.set(cameraPosition);

        return false;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    final float GRID_MIN = -1 * 32;
    final float GRID_MAX = 1 * 32;
    final float GRID_STEP = 1;
    public Model axesModel;
    public ModelInstance axesInstance;

    private void createAxes() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        // grid
        MeshPartBuilder builder = modelBuilder.part("grid", GL30.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }
        // axes
        builder = modelBuilder.part("axes", GL30.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 500, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 500, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 500);
        axesModel = modelBuilder.end();
        axesInstance = new ModelInstance(axesModel);
    }

    private void load(String fname) {

        Map<Integer, String> map = new HashMap<>();
        loadTiles(map);
        try {

            InputStream is = ClassLoader.class.getResourceAsStream("/data/" + fname);
            byte[] bytes = IOUtils.toByteArray(is);

            int pos = 0;
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    int index = bytes[pos] & 0xff;
                    pos++;
                    String tile = map.get(index);
                    if (tile == null) {
                        System.out.println("Tile index cannot be found: " + index + " using index 127 for black space.");
                        tile = map.get(127);
                    }
                    int heightValue = getHeight(tile);

                    if (heightValue == 0) {
                        this.cubes[0][x][y] = new Box(tile, x, y, 0);
                    } else if (heightValue == 1) {
                        this.cubes[0][x][y] = new Box(tile, x, y, 0);
                        this.cubes[1][x][y] = new Box(tile, x, y, 1);
                    } else if (heightValue == 2) {
                        this.cubes[0][x][y] = new Box(tile, x, y, 0);
                        this.cubes[1][x][y] = new Box(tile, x, y, 1);
                        this.cubes[2][x][y] = new Box(tile, x, y, 2);
                    } else if (heightValue == 3) {
                        this.cubes[0][x][y] = new Box(tile, x, y, 0);
                        this.cubes[1][x][y] = new Box(tile, x, y, 1);
                        this.cubes[2][x][y] = new Box(tile, x, y, 2);
                        this.cubes[3][x][y] = new Box(tile, x, y, 3);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class Box {

        String tile;
        ModelInstance instance;

        public Box(String tile, int x, int y, int z) {
            this.tile = tile;
            Model model = builder.createBox(1, 1, 1, getMaterial(getColor(tile), z > 2), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
            this.instance = new ModelInstance(model, x, z, y);
        }

    }

    private Material getMaterial(Color c, boolean blending) {
        if (blending) {
            return new Material(ColorAttribute.createDiffuse(c), ColorAttribute.createSpecular(c), new BlendingAttribute(.7f));
        } else {
            return new Material(ColorAttribute.createDiffuse(c));
        }
    }

    private void loadTiles(Map<Integer, String> map) {

        map.put(0, "sea");
        map.put(1, "water");
        map.put(2, "shallows");
        map.put(3, "swamp");
        map.put(4, "grass");
        map.put(5, "brush");
        map.put(6, "forest");
        map.put(7, "hills");
        map.put(8, "mountains");
        map.put(9, "dungeon");
        map.put(10, "city");
        map.put(11, "castle");
        map.put(12, "town");
        map.put(13, "lcb_west");
        map.put(14, "lcb_entrance");
        map.put(15, "lcb_east");
        map.put(16, "ship");
        map.put(17, "ship");
        map.put(18, "ship");
        map.put(19, "ship");
        map.put(20, "horse");
        map.put(21, "horse");
        map.put(22, "dungeon_floor");
        map.put(23, "bridge");
        map.put(24, "balloon");
        map.put(25, "bridge_piece1");
        map.put(26, "bridge_piece2");
        map.put(27, "up_ladder");
        map.put(28, "down_ladder");
        map.put(29, "ruins");
        map.put(30, "shrine");
        map.put(31, "avatar");
        map.put(32, "mage");
        map.put(33, "mage");
        map.put(34, "bard");
        map.put(35, "bard");
        map.put(36, "fighter");
        map.put(37, "fighter");
        map.put(38, "druid");
        map.put(39, "druid");
        map.put(40, "tinker");
        map.put(41, "tinker");
        map.put(42, "paladin");
        map.put(43, "paladin");
        map.put(44, "ranger");
        map.put(45, "ranger");
        map.put(46, "shepherd");
        map.put(47, "shepherd");
        map.put(48, "column");
        map.put(49, "solids1");
        map.put(50, "solids2");
        map.put(51, "solids3");
        map.put(52, "solids4");
        map.put(53, "shipmast");
        map.put(54, "shipwheel");
        map.put(55, "rocks");
        map.put(56, "corpse");
        map.put(57, "stone_wall");
        map.put(58, "locked_door");
        map.put(59, "door");
        map.put(60, "chest");
        map.put(61, "ankh");
        map.put(62, "brick_floor");
        map.put(63, "wood_floor");
        map.put(64, "moongate");
        map.put(65, "moongate");
        map.put(66, "moongate");
        map.put(67, "moongate");
        map.put(68, "poison_field");
        map.put(69, "energy_field");
        map.put(70, "fire_field");
        map.put(71, "sleep_field");
        map.put(72, "solid");
        map.put(73, "secret_door");
        map.put(74, "altar");
        map.put(75, "campfire");
        map.put(76, "lava");
        map.put(77, "miss_flash");
        map.put(78, "magic_flash");
        map.put(79, "hit_flash");
        map.put(80, "guard");
        map.put(81, "guard");
        map.put(82, "villager");
        map.put(83, "villager");
        map.put(84, "bard_singing");
        map.put(85, "bard_singing");
        map.put(86, "jester");
        map.put(87, "jester");
        map.put(88, "beggar");
        map.put(89, "beggar");
        map.put(90, "child");
        map.put(91, "child");
        map.put(92, "bull");
        map.put(93, "bull");
        map.put(94, "lord_british");
        map.put(95, "lord_british");
        map.put(96, "A");
        map.put(97, "B");
        map.put(98, "C");
        map.put(99, "D");
        map.put(100, "E");
        map.put(101, "F");
        map.put(102, "G");
        map.put(103, "H");
        map.put(104, "I");
        map.put(105, "J");
        map.put(106, "K");
        map.put(107, "L");
        map.put(108, "M");
        map.put(109, "N");
        map.put(110, "O");
        map.put(111, "P");
        map.put(112, "Q");
        map.put(113, "R");
        map.put(114, "S");
        map.put(115, "T");
        map.put(116, "U");
        map.put(117, "V");
        map.put(118, "W");
        map.put(119, "X");
        map.put(120, "Y");
        map.put(121, "Z");
        map.put(122, "spacer_middle");
        map.put(123, "spacer_right");
        map.put(124, "spacer_left");
        map.put(125, "spacer_square");
        map.put(126, "blank");
        map.put(127, "brick_wall");
        map.put(128, "pirate_ship");
        map.put(129, "pirate_ship");
        map.put(130, "pirate_ship");
        map.put(131, "pirate_ship");
        map.put(132, "nixie");
        map.put(133, "nixie");
        map.put(134, "giant_squid");
        map.put(135, "giant_squid");
        map.put(136, "sea_serpent");
        map.put(137, "sea_serpent");
        map.put(138, "sea_horse");
        map.put(139, "sea_horse");
        map.put(140, "whirlpool");
        map.put(141, "whirlpool");
        map.put(142, "twister");
        map.put(143, "twister");
        map.put(144, "rat");
        map.put(145, "rat");
        map.put(146, "rat");
        map.put(147, "rat");
        map.put(148, "bat");
        map.put(149, "bat");
        map.put(150, "bat");
        map.put(151, "bat");
        map.put(152, "spider");
        map.put(153, "spider");
        map.put(154, "spider");
        map.put(155, "spider");
        map.put(156, "ghost");
        map.put(157, "ghost");
        map.put(158, "ghost");
        map.put(159, "ghost");
        map.put(160, "slime");
        map.put(161, "slime");
        map.put(162, "slime");
        map.put(163, "slime");
        map.put(164, "troll");
        map.put(165, "troll");
        map.put(166, "troll");
        map.put(167, "troll");
        map.put(168, "gremlin");
        map.put(169, "gremlin");
        map.put(170, "gremlin");
        map.put(171, "gremlin");
        map.put(172, "mimic");
        map.put(173, "mimic");
        map.put(174, "mimic");
        map.put(175, "mimic");
        map.put(176, "reaper");
        map.put(177, "reaper");
        map.put(178, "reaper");
        map.put(179, "reaper");
        map.put(180, "insect_swarm");
        map.put(181, "insect_swarm");
        map.put(182, "insect_swarm");
        map.put(183, "insect_swarm");
        map.put(184, "gazer");
        map.put(185, "gazer");
        map.put(186, "gazer");
        map.put(187, "gazer");
        map.put(188, "phantom");
        map.put(189, "phantom");
        map.put(190, "phantom");
        map.put(191, "phantom");
        map.put(192, "orc");
        map.put(193, "orc");
        map.put(194, "orc");
        map.put(195, "orc");
        map.put(196, "skeleton");
        map.put(197, "skeleton");
        map.put(198, "skeleton");
        map.put(199, "skeleton");
        map.put(200, "rogue");
        map.put(201, "rogue");
        map.put(202, "rogue");
        map.put(203, "rogue");
        map.put(204, "python");
        map.put(205, "python");
        map.put(206, "python");
        map.put(207, "python");
        map.put(208, "ettin");
        map.put(209, "ettin");
        map.put(210, "ettin");
        map.put(211, "ettin");
        map.put(212, "headless");
        map.put(213, "headless");
        map.put(214, "headless");
        map.put(215, "headless");
        map.put(216, "cyclops");
        map.put(217, "cyclops");
        map.put(218, "cyclops");
        map.put(219, "cyclops");
        map.put(220, "wisp");
        map.put(221, "wisp");
        map.put(222, "wisp");
        map.put(223, "wisp");
        map.put(224, "evil_mage");
        map.put(225, "evil_mage");
        map.put(226, "evil_mage");
        map.put(227, "evil_mage");
        map.put(228, "liche");
        map.put(229, "liche");
        map.put(230, "liche");
        map.put(231, "liche");
        map.put(232, "lava_lizard");
        map.put(233, "lava_lizard");
        map.put(234, "lava_lizard");
        map.put(235, "lava_lizard");
        map.put(236, "zorn");
        map.put(237, "zorn");
        map.put(238, "zorn");
        map.put(239, "zorn");
        map.put(240, "daemon");
        map.put(241, "daemon");
        map.put(242, "daemon");
        map.put(243, "daemon");
        map.put(244, "hydra");
        map.put(245, "hydra");
        map.put(246, "hydra");
        map.put(247, "hydra");
        map.put(248, "dragon");
        map.put(249, "dragon");
        map.put(250, "dragon");
        map.put(251, "dragon");
        map.put(252, "balron");
        map.put(253, "balron");
        map.put(254, "balron");
        map.put(255, "balron");

    }

    private int getHeight(String tile) {
        int h = 0;
        switch (tile) {
            case "water":
            case "shallows":
            case "sea":
                h = 0;
                break;
            case "dungeon":
            case "city":
            case "lcb_west":
            case "lcb_east":
            case "lcb_entrance":
            case "castle":
            case "town":
            case "ruins":
            case "shrine":
            case "grass":
            case "brush":
            case "swamp":
            case "forest":
            case "lava":
            case "fire_field":
            case "ankh":
            case "bridge":
            case "hills":
                h = 1;
                break;
            case "brick_wall":
            case "stone_wall":
                h = 2;
                break;
            case ("A"):
            case ("B"):
            case ("C"):
            case ("D"):
            case ("E"):
            case ("F"):
            case ("G"):
            case ("H"):
            case ("I"):
            case ("J"):
            case ("K"):
            case ("L"):
            case ("M"):
            case ("N"):
            case ("O"):
            case ("P"):
            case ("Q"):
            case ("R"):
            case ("S"):
            case ("T"):
            case ("U"):
            case ("V"):
            case ("W"):
            case ("X"):
            case ("Y"):
            case ("Z"):
                h = 2;
                break;
            case "column":
            case ("spacer_middle"):
            case ("spacer_right"):
            case ("spacer_left"):
            case ("spacer_square"):
            case ("blank"):
            case ("solid"):
            case ("solids1"):
            case ("solids2"):
            case ("solids3"):
            case ("solids4"):
                h = 2;
                break;
            case "door":
            case "locked_door":
            case "secret_door":
                h = 1;
                break;
            case "rocks":
            case "bridge_piece1":
            case "bridge_piece2":
            case "wood_floor":
            case "campfire":
            case "dungeon_floor":
            case "sleep_field":
            case "energy_field":
            case "poison_field":
            case "down_ladder":
            case "up_ladder":
            case "chest":
            case "brick_floor":
                h = 1;
                break;
            case "mountains":
                h = 3;
                break;
        }

        return h;
    }

    private Color getColor(String tile) {
        Color c = Color.GRAY;
        switch (tile) {
            case "water":
            case "shallows":
            case "sea":
                c = Color.BLUE;
                break;
            case "dungeon":
            case "city":
            case "lcb_west":
            case "lcb_east":
            case "lcb_entrance":
            case "castle":
            case "town":
            case "ruins":
            case "shrine":
                c = Color.PINK;
                break;
            case "grass":
            case "brush":
            case "swamp":
            case "forest":
            case "ankh":
            case "bridge":
            case "hills":
                c = Color.FOREST;
                break;
            case "lava":
            case "fire_field":
                c = Color.RED;
                break;
            case "brick_wall":
            case "stone_wall":
                c = Color.GRAY;
                break;
            case ("A"):
            case ("B"):
            case ("C"):
            case ("D"):
            case ("E"):
            case ("F"):
            case ("G"):
            case ("H"):
            case ("I"):
            case ("J"):
            case ("K"):
            case ("L"):
            case ("M"):
            case ("N"):
            case ("O"):
            case ("P"):
            case ("Q"):
            case ("R"):
            case ("S"):
            case ("T"):
            case ("U"):
            case ("V"):
            case ("W"):
            case ("X"):
            case ("Y"):
            case ("Z"):
                c = Color.YELLOW;
                break;
            case "column":
            case ("spacer_middle"):
            case ("spacer_right"):
            case ("spacer_left"):
            case ("spacer_square"):
            case ("blank"):
            case ("solid"):
            case ("solids1"):
            case ("solids2"):
            case ("solids3"):
            case ("solids4"):
                c = Color.NAVY;
                break;
            case "door":
            case "locked_door":
            case "secret_door":
                c = Color.TEAL;
                break;
            case "rocks":
            case "bridge_piece1":
            case "bridge_piece2":
            case "wood_floor":
            case "campfire":
            case "dungeon_floor":
            case "down_ladder":
            case "up_ladder":
            case "chest":
            case "brick_floor":
                c = Color.BROWN;
                break;
            case "sleep_field":
            case "energy_field":
            case "poison_field":
                c = Color.TEAL;
                break;
            case "mountains":
                c = Color.DARK_GRAY;
                break;
        }

        return c;
    }

}
