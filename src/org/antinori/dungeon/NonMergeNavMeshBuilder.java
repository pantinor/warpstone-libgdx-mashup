package org.antinori.dungeon;

import java.util.ArrayList;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.newdawn.slick.util.pathfinding.navmesh.NavMesh;
import org.newdawn.slick.util.pathfinding.navmesh.NavMeshBuilder;
import org.newdawn.slick.util.pathfinding.navmesh.Space;

/**
 * Skips merging spaces routine in the base class so links make more sense.
 * 
 * @author Paul
 *
 */
public class NonMergeNavMeshBuilder extends NavMeshBuilder {

	@Override
	public NavMesh build(TileBasedMap map, boolean tileBased) {

		ArrayList spaces = new ArrayList();

		for (int x = 0; x < map.getWidthInTiles(); x++) {
			for (int y = 0; y < map.getHeightInTiles(); y++) {
				if (!map.blocked(this, x, y)) {
					spaces.add(new Space(x, y, 1, 1));
				}
			}
		}

		linkSpaces(spaces);

		return new NavMesh(spaces);
	}

	//this is private in base class :(
	void linkSpaces(ArrayList spaces) {
		for (int source = 0; source < spaces.size(); source++) {
			Space a = (Space) spaces.get(source);

			for (int target = source + 1; target < spaces.size(); target++) {
				Space b = (Space) spaces.get(target);

				if (a.hasJoinedEdge(b)) {
					a.link(b);
					b.link(a);
				}
			}
		}
	}

}
