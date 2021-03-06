package hybridTerrain;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import generation.ColorGenerator;
import generation.PerlinNoise;
import openglObjects.Vao;
import rendering.TerrainRenderer;
import rendering.TerrainShader;
import terrains.Terrain;
import terrains.TerrainGenerator;
import utils.Color;
import utils.MyFile;
import vertexDataStoring.VaoLoader;

public class HybridTerrainGenerator extends TerrainGenerator {

	private static final MyFile VERTEX_SHADER = new MyFile("rendering", "flatTerrainVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("rendering", "flatTerrainFragment.glsl");

	private static final int VERTEX_SIZE_BYTES = 12 + 4 + 4;// position + normal + color

	private final TerrainRenderer renderer;

	public HybridTerrainGenerator(PerlinNoise perlinNoise, ColorGenerator colorGen) {
		super(perlinNoise, colorGen);
		this.renderer = new TerrainRenderer(new TerrainShader(VERTEX_SHADER, FRAGMENT_SHADER), true);
	}

	@Override
	public void cleanUp() {
		renderer.cleanUp();
	}

	@Override
	protected Terrain createTerrain(float[][] heights, Color[][] colors) {
		int vertexCount = calculateVertexCount(heights.length);
		byte[] terrainData = createMeshData(heights, colors, vertexCount);
		int[] indices = IndexGenerator.generateIndexBuffer(heights.length);
		Vao vao = VaoLoader.createVao(terrainData, indices);
		return new Terrain(vao, indices.length, renderer);
	}

	private int calculateVertexCount(int vertexLength) {
		int bottom2Rows = 2 * vertexLength;
		int remainingRowCount = vertexLength - 2;
		int topCount = remainingRowCount * (vertexLength - 1) * 2;
		return topCount + bottom2Rows;
	}

	private byte[] createMeshData(float[][] heights, Color[][] colors, int vertexCount) {
		int byteSize = VERTEX_SIZE_BYTES * vertexCount;
		ByteBuffer buffer = ByteBuffer.allocate(byteSize).order(ByteOrder.nativeOrder());
		GridSquare[] lastRow = new GridSquare[heights.length - 1];
		for (int row = 0; row < heights.length - 1; row++) {
			for (int col = 0; col < heights[row].length - 1; col++) {
				GridSquare square = new GridSquare(row, col, heights, colors);
				square.storeSquareData(buffer);
				if (row == heights.length - 2) {
					lastRow[col] = square;
				}
			}
		}
		for (int i = 0; i < lastRow.length; i++) {
			lastRow[i].storeBottomRowData(buffer);
		}
		return buffer.array();
	}

}
