package main;

import generation.ColorGenerator;
import generation.PerlinNoise;
import hybridTerrain.HybridTerrainGenerator;
import rendering.Light;
import rendering.RenderEngine;
import terrains.Terrain;
import terrains.TerrainGenerator;
import water.WaterGenerator;
import water.WaterTile;

public class StevensWaterSim {

	public static void main(String[] args) {
		//init engine and scene objects
		RenderEngine engine = new RenderEngine(Configs.FPS_CAP, Configs.WIDTH, Configs.HEIGHT);
		Camera camera = new Camera();
		Light light = new Light(Configs.LIGHT_POS, Configs.LIGHT_COL, Configs.LIGHT_BIAS);

		//init terrain
		PerlinNoise noise = new PerlinNoise(Configs.OCTAVES, Configs.AMPLITUDE, Configs.ROUGHNESS);
		ColorGenerator colorGen = new ColorGenerator(Configs.TERRAIN_COLS, Configs.color_SPREAD);
		TerrainGenerator terrainGenerator = new HybridTerrainGenerator(noise, colorGen);
		Terrain terrain = terrainGenerator.generateTerrain(Configs.WORLD_SIZE);
		
		WaterTile water = WaterGenerator.generate(Configs.WORLD_SIZE, Configs.WATER_HEIGHT);
		
		while (!engine.getWindow().isCloseRequested()) {
			camera.move();
			engine.render(terrain, water, camera, light);
		}

		water.delete();
		terrainGenerator.cleanUp();
		terrain.delete();

		engine.close();

	}

}
