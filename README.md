Learning exercise for libgdx.  I liked the map generation in the Slick2D Warpstone example, and wanted to try the same technique with a libgdx 3D layout.
I borrowed code from warpstone for the map generation and the pathfinding technique.

About the maven repository, you may need to add the sonatype snapshots repository as a mirror in your settings.xml.

About the controls, you can use WASD as well as mouse clicks and drags to pan around.  Use the 1 key to reset a view.

![screenshot of the example](https://raw.github.com/pantinor/warpstone-libgdx-mashup/master/warpstone-libdgx.png)