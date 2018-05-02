package generation;

import utils.Color;
import utils.Maths;

public class ColorGenerator {

	private final float spread;
	private final float halfSpread;

	private final Color[] biomeColours;
	private final float part;

	public ColorGenerator(Color[] biomeColours, float spread) {
		this.biomeColours = biomeColours;
		this.spread = spread;
		this.halfSpread = spread / 2f;
		this.part = 1f / (biomeColours.length - 1);
	}

	public Color[][] generateColors(float[][] heights, float amplitude) {
		Color[][] colours = new Color[heights.length][heights.length];
		for (int z = 0; z < heights.length; z++) {
			for (int x = 0; x < heights[z].length; x++) {
				colours[z][x] = calculateColour(heights[z][x], amplitude);
			}
		}
		return colours;
	}

	private Color calculateColour(float height, float amplitude) {
		float value = (height + amplitude) / (amplitude * 2);
		value = Maths.clamp((value - halfSpread) * (1f / spread), 0f, 0.9999f);
		int firstBiome = (int) Math.floor(value / part);
		float blend = (value - (firstBiome * part)) / part;
		return Color.interpolateColours(biomeColours[firstBiome], biomeColours[firstBiome + 1], blend, null);
	}

}
