package org.antinori.dungeon;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class UltimaDungeonMap implements MapGenerator {

    public static final int DUNGEON_MAP = 8;

    /**
     * we wont use room ids in these maps
     */
    private int[][] room_ids;

    /**
     * the height value of each tile
     */
    private double[][][] heightValues;

    /**
     * The filename like DESPISE.DNG from ultima 4
     * http://wiki.ultimacodex.com/wiki/Ultima_IV_Internal_Formats#ULT_files
     *
     */
    public UltimaDungeonMap(String fileName) {

        try {

            InputStream is = UltimaDungeonMap.class.getResourceAsStream("/data/" + fileName);
            byte[] bytes = IOUtils.toByteArray(is);

            //The first 512 bytes are the 8x8 maps for each of the 8 levels.
            DungeonTile[][][] dungeonTiles = new DungeonTile[DUNGEON_MAP][DUNGEON_MAP][DUNGEON_MAP];
            room_ids = new int[DUNGEON_MAP][DUNGEON_MAP];
            heightValues = new double[DUNGEON_MAP][DUNGEON_MAP][DUNGEON_MAP];

            int pos = 0;
            for (int i = 0; i < DUNGEON_MAP; i++) {
                for (int y = 0; y < DUNGEON_MAP; y++) {
                    for (int x = 0; x < DUNGEON_MAP; x++) {
                        int index = bytes[pos] & 0xff;
                        pos++;
                        DungeonTile tile = DungeonTile.getTileByValue(index);
                        dungeonTiles[i][x][y] = tile;
                        if (tile == DungeonTile.WALL) {
                            heightValues[i][x][y] = 1;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void generate(int minRooms, int mergeIterations, int corridorCount) {
        //NA
    }

    public double[][] getHeightValues() {
        return heightValues[0]; //return the first level
    }

    public double[][] getHeightValues(int level) {
        return heightValues[level];
    }

    public int[][] getTiles() {
        return room_ids;
    }

    public int getStartX() {
        return 0;
    }

    public int getStartY() {
        return 0;
    }

    public static enum DungeonTile {

        NOTHING(0x00, "Nothing", "blank"),
        LADDER_UP(0x10, "Ladder Up", "up_ladder"),
        LADDER_DOWN(0x20, "Ladder Down", "down_ladder"),
        LADDER_UP_DOWN(0x30, "Ladder Up & Down", "down_ladder"),
        CHEST(0x40, "Treasure Chest", "chest"),
        CEILING_HOLE(0x50, "Ceiling Hole", "rocks"),
        FLOOR_HOLE(0x60, "Floor Hole", "rocks"),
        ORB(0x70, "Magic Orb", "hit_flash"),
        WIND_TRAP(0x80, "Winds/Darknes Trap", "swamp"),
        ROCK_TRAP(0x81, "Falling Rock Trap", "swamp"),
        PIT_TRAP(0x8E, "Pit Trap", "swamp"),
        FOUNTAIN_PLAIN(0x90, "Plain Fountain", "magic_flash"),
        FOUNTAIN_HEAL(0x91, "Healing Fountain", "magic_flash"),
        FOUNTAIN_ACID(0x92, "Acid Fountain", "magic_flash"),
        FOUNTAIN_CURE(0x93, "Cure Fountain", "magic_flash"),
        FOUNTAIN_POISON(0x94, "Poison Fountain", "magic_flash"),
        FIELD_POISON(0xA0, "Poison Field", "poison_field"),
        FIELD_ENERGY(0xA1, "Energy Field", "energy_field"),
        FIELD_FIRE(0xA2, "Fire Field", "fire_field"),
        FIELD_SLEEP(0xA3, "Sleep Field", "sleep_field"),
        ALTAR(0xB0, "Altar", "altar"),
        DOOR(0xC0, "Door", "locked_door"),
        ROOM_1(0xD0, "Dungeon Room 1", "spacer_square"),
        ROOM_2(0xD1, "Dungeon Room 2", "spacer_square"),
        ROOM_3(0xD2, "Dungeon Room 3", "spacer_square"),
        ROOM_4(0xD3, "Dungeon Room 4", "spacer_square"),
        ROOM_5(0xD4, "Dungeon Room 5", "spacer_square"),
        ROOM_6(0xD5, "Dungeon Room 6", "spacer_square"),
        ROOM_7(0xD6, "Dungeon Room 7", "spacer_square"),
        ROOM_8(0xD7, "Dungeon Room 8", "spacer_square"),
        ROOM_9(0xD8, "Dungeon Room 9", "spacer_square"),
        ROOM_10(0xD9, "Dungeon Room 10", "spacer_square"),
        ROOM_11(0xDA, "Dungeon Room 11", "spacer_square"),
        ROOM_12(0xDB, "Dungeon Room 12", "spacer_square"),
        ROOM_13(0xDC, "Dungeon Room 13", "spacer_square"),
        ROOM_14(0xDD, "Dungeon Room 14", "spacer_square"),
        ROOM_15(0xDE, "Dungeon Room 15", "spacer_square"),
        ROOM_16(0xDF, "Dungeon Room 16", "spacer_square"),
        SECRET_DOOR(0xE0, "Secret Door", "secret_door"),
        WALL(0xF0, "Wall ", "stone_wall");

        private int value;
        private String type;
        private String tileName;

        private DungeonTile(int value, String type, String tileName) {
            this.value = value;
            this.type = type;
            this.tileName = tileName;
        }

        public int getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public String getTileName() {
            return tileName;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTileName(String tileName) {
            this.tileName = tileName;
        }

        public static DungeonTile getTileByValue(int val) {
            DungeonTile ret = DungeonTile.NOTHING;
            for (DungeonTile d : DungeonTile.values()) {
                if (val == d.getValue()) {
                    ret = d;
                    break;
                }
            }
            return ret;
        }

    }

}
