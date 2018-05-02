package rendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import utils.Color;

public class Light {

	private Vector3f direction;
	private Color color;
	private Vector2f lightBias;// how much ambient light and how much diffuse
								// light

	public Light(Vector3f direction, Color color, Vector2f lightBias) {
		this.direction = direction;
		this.direction.normalise();
		this.color = color;
		this.lightBias = lightBias;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public Color getColor() {
		return color;
	}

	public Vector2f getLightBias() {
		return lightBias;
	}

}
