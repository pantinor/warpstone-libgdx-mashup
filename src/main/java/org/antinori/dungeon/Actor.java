package org.antinori.dungeon;

import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Actor extends GeometricObject {
	
	String name;
	DungeonLevel map;
	
	private NavPath currentPath;
	private int pathStep;
	private int moveIterations = 0;

	private float dx;
	private float dz;

	private float tx;
	private float tz;

	// moving speed specified here
	private static final float SPEED = 0.05f;

	public Actor(String name, ShaderProgram shader, Mesh mesh, int type, DungeonLevel map) {
		super(shader, mesh, type, null);
		this.name = name;
		this.map = map;
	}

	public void setCurrentPath(NavPath currentPath) {
		this.currentPath = currentPath;
		pathStep = 0;
		nextStep();
	}

	public NavPath getCurrentPath() {
		return currentPath;
	}

	public void update() {

		if (currentPath != null) {
			if (!considerNextStep()) {

				moveIterations--;

				this.getPos().x += dx * 1;
				this.getPos().z += dz * 1;

				if (moveIterations <= 0) {
					nextStep();
				}
			}
		}

	}

	private boolean considerNextStep() {
		if (currentPath == null)
			return false;

		if (pathStep < currentPath.length() - 1) {
			tx = currentPath.getX(pathStep + 1);
			tz = currentPath.getY(pathStep + 1);

			if (map.hasLos(this.getPos().x, this.getPos().z, tx, tz, 0.5f)) {
				nextStep();
				return true;
			}
		}

		return false;
	}

	private void nextStep() {

		if (currentPath == null)
			return;

		pathStep++;
		if (pathStep >= currentPath.length()) {
			currentPath = null;
			dx = 0;
			dz = 0;
			this.getPos().x = (int) tx;
			this.getPos().z = (int) tz;

			return;
		}

		tx = currentPath.getX(pathStep);
		tz = currentPath.getY(pathStep);

		dx = (tx - this.getPos().x / 1);
		dz = (tz - this.getPos().z / 1);

		moveIterations = (int) (Math.sqrt((dx * dx) + (dz * dz)) / SPEED);
		dx = dx / moveIterations;
		dz = dz / moveIterations;

	}

}
