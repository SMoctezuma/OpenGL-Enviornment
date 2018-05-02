package fbos;

import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Represents an Frame Buffer Object. Holds the ID of the object, and the
 * width/height of the FBO.
 * 
 * @author Karl
 *
 */
public class Fbo {

	private final int fboId;
	private final int width;
	private final int height;

	private final Map<Integer, Attachment> colorAttachments;
	private final Attachment depthAttachment;

	protected Fbo(int fboId, int width, int height, Map<Integer, Attachment> attachments, Attachment depthAttachment) {
		this.fboId = fboId;
		this.width = width;
		this.height = height;
		this.colorAttachments = attachments;
		this.depthAttachment = depthAttachment;
	}

	/**
	 * Copy the contents of a color attachment of this FBO to the screen.
	 * 
	 * @param colorIndex
	 *            - The index of the color buffer that should be blitted.
	 */
	public void blitToScreen(int colorIndex) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fboId);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + colorIndex);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Display.getWidth(), Display.getHeight(),
				GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	/**
	 * Copy the contents of this FBO to another FBO. This can be used to resolve
	 * multisampled FBOs.
	 * 
	 * @param srccolorIndex
	 *            - Index of the color buffer in this (the source) FBO.
	 * @param target
	 *            - The target FBO.
	 * @param targetcolorIndex
	 *            - The index of the target color buffer in the target FBO.
	 */
	public void blitToFbo(int srccolorIndex, Fbo target, int targetcolorIndex) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.fboId);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0 + targetcolorIndex);

		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fboId);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + srccolorIndex);

		int bufferBit = depthAttachment != null && target.depthAttachment != null
				? GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT : GL11.GL_COLOR_BUFFER_BIT;
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, target.width, target.height, bufferBit, GL11.GL_NEAREST);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	/**
	 * Gets the ID of the buffer being used as the color buffer (either a
	 * render buffer or a texture).
	 * 
	 * @param colorIndex
	 *            - The index of the color attachment.
	 * @return The ID of the buffer.
	 */
	public int getcolorBuffer(int colorIndex) {
		return colorAttachments.get(colorIndex).getBufferId();
	}

	/**
	 * @return The ID of the buffer containing the depth buffer (either a render
	 *         buffer or a texture).
	 */
	public int getDepthBuffer() {
		return depthAttachment.getBufferId();
	}

	/**
	 * @return True if this framebuffer has been correctly set up and is
	 *         complete.
	 */
	public boolean isComplete() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		boolean complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		return complete;
	}

	/**
	 * Bind the FBO so that it can be rendered to. Anything rendered while the
	 * FBO is bound will be rendered to the FBO.
	 * 
	 * @param colorIndex
	 *            - The index of the color buffer that should be drawn to.
	 */
	public void bindForRender(int colorIndex) {
		// should add support for binding multiple color attachments
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0 + colorIndex);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fboId);
		GL11.glViewport(0, 0, width, height);
	}

	/**
	 * Switch back to the default frame buffer.
	 */
	public void unbindAfterRender() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Delete the FBO and attachments.
	 */
	public void delete() {
		for (Attachment attachment : colorAttachments.values()) {
			attachment.delete();
		}
		if (depthAttachment != null) {
			depthAttachment.delete();
		}
	}

	/**
	 * Starts the creation of a new FBO (without multisampling)
	 * 
	 * @param width
	 *            - The width in pixels of the FBO.
	 * @param height
	 *            - The height in pixels.
	 * @return The builder object for the FBO.
	 */
	public static FboBuilder newFbo(int width, int height) {
		return new FboBuilder(width, height, 0);
	}

	/**
	 * Starts the creation of a new multisampled FBO.
	 * 
	 * @param width
	 *            - Width in pixels.
	 * @param height
	 *            - Height in pixels.
	 * @param samples
	 *            - Number of samples being used for multisampling.
	 * @return The builder object.
	 */
	public static FboMsBuilder newMultisampledFbo(int width, int height, int samples) {
		return new FboMsBuilder(new FboBuilder(width, height, samples));
	}

}
