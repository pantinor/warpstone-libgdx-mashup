package org.antinori.dungeon;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Cube extends GeometricObject {

    public float speed;
    public int room_id;

    public Cube(ShaderProgram shader, Mesh mesh, int type, Texture texture) {
        super(shader, mesh, type, texture);
        speed = 0;
    }

    public Cube(ShaderProgram shader, Mesh mesh, double heightValue, int room_id) {
        super(shader, mesh, GL20.GL_TRIANGLES, null);
        this.speed = 0;
        this.room_id = room_id;

        if (heightValue < 0) {
            this.texture = MashupMain.ROCK_TEXTURE;
        } else if (heightValue == 0) {
            if (room_id > 0) {
                this.texture = MashupMain.DIRT_TEXTURE;
            } else {
                this.texture = MashupMain.GRASS_TEXTURE;
            }
        } else if (heightValue == 1) {
            this.texture = MashupMain.ROCK_TEXTURE;
        } else if (heightValue == 2) {
            this.texture = MashupMain.ROCK_TEXTURE;
        } else {
            this.texture = MashupMain.ROCK_TEXTURE;
        }

        if (texture != null) {
            texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
        }
    }

}
