package org.antinori.dungeon;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.navmesh.NavMesh;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DungeonLevel extends Level implements TileBasedMap {
	
	private Cube[][][] cubes;
	private double[][] heightValues;
	private int[][] room_ids;
	
	public DungeonMapGenerator dmap;
	public NavMesh navMesh;
	public NonMergeNavMeshBuilder builder;

	public DungeonLevel(float xPos, float yPos, int width, int height, int zDepth) {
		
		super(xPos, yPos, width, height, zDepth);
		
		cubes = new Cube[width][height][zDepth];

		
		dmap = new DungeonMapGenerator(width, height);
		dmap.generate(100, 20, 0, 0);
			
		heightValues = dmap.getHeightValues();
		room_ids = dmap.getTiles();
		
		
//		this.map = new float[width][height];
//		this.room_ids = new int[width][height];
//		for (int i = 0; i < width; i++) {
//			for (int j = 0; j < height; j++) {
//				this.map[i][j] = 0;
//				this.room_ids[i][j] = 0;
//			}
//		}
//		this.map[3][3] = 1;
//		this.map[3][4] = 2;
//		this.room_ids[3][3] = 1;
//		this.room_ids[3][4] = 1;
		
		
		builder = new NonMergeNavMeshBuilder();
		navMesh = builder.build(this, true);

		System.out.println("Pathfinding MeshBuilder space count is " + navMesh.getSpaceCount());


	}
	
	public NavPath getPath(float sx, float sz, float tx, float tz) {
		return navMesh.findPath(sx, sz, tx, tz, false);
	}



	public void initCubes(ShaderProgram shader, Mesh mesh) {
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				
				double heightValue = this.heightValues[i][j];
				
				if (heightValue <= 0) {
					Cube cube = new Cube(shader, mesh, 0, room_ids[i][j]);
					cube.getPos().x = i;
					cube.getPos().y = 0;
					cube.getPos().z = j;
					this.cubes[i][j][0] = cube;
				} else {
					for (int k = (int) heightValue; k >= 0; k--) {
						Cube cube = new Cube(shader, mesh, heightValue, room_ids[i][j]);
						cube.getPos().x = i;
						cube.getPos().y = k;
						cube.getPos().z = j;
						this.cubes[i][j][k] = cube;
					}
				}
			}
		}

	}


	public Cube[][][] getCubes() {
		return cubes;
	}
	
	public boolean hasLos(float x1, float y1, float x2, float y2, float size) {
		float step = 0.1f;
		float dx = (x2-x1);
		float dy = (y2-y1);
		float len = Math.max(Math.abs(dx),Math.abs(dy));
		dx /= len;
		dx *= step;
		dy /= len;
		dy *= step;
		int steps = (int) (len / step);
		for (int i=0;i<steps;i++) {
			if (blocked((int) x1, (int) y1)) {
				return false;
			}
			if (blocked((int) (x1-(dy*10*size)), (int) (y1+(dx*10*size)))) {
				return false;
			}
			if (blocked((int) (x1+(dy*10*size)), (int) (y1-(dx*10*size)))) {
				return false;
			}
			
			x1 += dx;
			y1 += dy;
		}
		
		return true;
	}



	public boolean blocked(PathFindingContext context, int tx, int ty) {
		return blocked(tx, ty);
	}

	public boolean blocked(int tx, int ty) {
		if ((tx < 0) || (ty < 0) || (tx >= width) || (ty >= height)) {
			return false;
		}
		return this.heightValues[tx][ty] > 0;
	}

	public int getHeightInTiles() {
		return height;
	}

	public int getWidthInTiles() {
		return width;
	}

	public void pathFinderVisited(int arg0, int arg1) {		
	}
	
	public float getCost(PathFindingContext arg0, int arg1, int arg2) {
		return 0;
	}

}
