package org.antinori.dungeon;

public abstract class Level
{
	public byte[][] map;
	public int width;
	public int height;
	public int zDepth;
	public float xPos; //top left x coordinate of the level
	public float yPos; //top left y coordinate of the level
	
	public Level(float xPos, float yPos, int width, int height, int zDepth)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.zDepth = zDepth;
		this.width = width;
		this.height = height;
		
		this.map = new byte[width][height];
	}

	/**
	 * get width of level
	 * @return
	 */
	public int getWidth() 
	{
		return width;
	}

	/**
	 * get height if level
	 * @return
	 */
	public int getHeight() 
	{
		return height;
	}
	
	/**
	 * get top left x coordinate of the level
	 * @return
	 */
	public float getX() 
	{
		return xPos;
	}

	/**
	 * get top left y coordinate of the level
	 * @return
	 */
	public float getY() {
		return yPos;
	}
}
