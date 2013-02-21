package com.me.mygdxgame.lights;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class PointLight implements Comparable, Serializable {

	private static final long serialVersionUID = 5237715137220677938L;

	final public String UID;

	final public Vector3 position;
	public Color color;
	public float attenuation;
	public float power;

	protected int priority;

	public static final int PRIORITY_DISCRETE_STEPS = 256;

	private final Vector3 tmpVec = new Vector3();

	public transient Mesh area;
	public transient float radius;

	public PointLight(Vector3 position, Color colour, float attentuation, float power) {
		UID = this.toString() + this.hashCode() + System.currentTimeMillis() + System.nanoTime();
		this.position = position;
		this.color = colour;
		this.attenuation = attentuation;
		this.power = power;

		computeMesh();
	}

	public void computeMesh() {
		Vector3 intensity = getColourRGB().mul(power);
		float dist = 1;
		while (intensity.len2() > 0.5f) {
			intensity = getColourRGB().mul(power).div((attenuation + (attenuation / 5) * dist) * dist);
			dist++;
		}

		dist *= 2;

		if (area != null)
			area.dispose();
		area = Shapes.genIcosahedronMesh(dist, dist);
		area.setVertices(Shapes.genIcosahedronVertices(dist, dist));

		radius = dist;
	}

	public void positionAbsolutely(float x, float y, float z) {
		positionAbsolutely(tmpVec.set(x, y, z));
	}

	public void positionAbsolutely(Vector3 pos) {
		position.set(pos);
	}

	@Override
	public int compareTo(Object other) {
		return this.priority - ((PointLight) other).priority;
	}

	public Vector3 getColourRGB() {
		return new Vector3(color.r, color.g, color.b);
	}

	public void fixReferences() {
		computeMesh();
	}

	public void bind(ShaderProgram shader) {
		shader.setUniformf("u_model", position);
		shader.setUniformf("u_colour", color.r, color.g, color.b);
		shader.setUniformf("u_attenuation", attenuation);
		shader.setUniformf("u_power", power);
	}

}
