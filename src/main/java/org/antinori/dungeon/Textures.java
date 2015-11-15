package org.antinori.dungeon;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Textures {

    private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

    public static Texture get(String path) {
        Texture texture = textures.get(path);
        if (texture == null) {
            texture = new Texture(Gdx.files.classpath(path), true);
            texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
        }
        return texture;
    }

    public static void dispose() {
        for (String key : textures.keySet()) {
            textures.get(key).dispose();
        }
    }
}
