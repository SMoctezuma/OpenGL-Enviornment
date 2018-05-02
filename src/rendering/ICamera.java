package rendering;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public interface ICamera {

	public Matrix4f getViewMatrix();

	public Vector3f getPosition();

	public Matrix4f getProjectionMatrix();

	public Matrix4f getProjectionViewMatrix();

	public float getNearPlane();

	public float getFarPlane();

	public void reflect();

}
