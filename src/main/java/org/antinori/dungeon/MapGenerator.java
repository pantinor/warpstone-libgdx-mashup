package org.antinori.dungeon;

public interface MapGenerator {

    public void generate(int minRooms, int mergeIterations, int corridorCount);

    public double[][] getHeightValues();

    public int[][] getTiles();

    public int getStartX();

    public int getStartY();

}
