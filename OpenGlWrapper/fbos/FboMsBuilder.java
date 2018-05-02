package fbos;

/**
 * Class used for generating multisampled FBOs.
 * 
 * @author Karl
 *
 */
public class FboMsBuilder {

	private final FboBuilder fboBuilder;

	protected FboMsBuilder(FboBuilder fboBuilder) {
		this.fboBuilder = fboBuilder;
	}

	/**
	 * Allows the attachment of render buffers as color buffers for the FBO.
	 * 
	 * @param index
	 *            - The index of the color buffer that the attachment should be
	 *            attached to.
	 * @param attachment
	 *            - The coloiur attachment.
	 * @return
	 */
	public FboMsBuilder addcolorAttachment(int index, RenderBufferAttachment attachment) {
		fboBuilder.addcolorAttachment(index, attachment);
		return this;
	}

	public FboMsBuilder addDepthAttachment(RenderBufferAttachment attachment) {
		fboBuilder.addDepthAttachment(attachment);
		return this;
	}

	public Fbo init() {
		return fboBuilder.init();
	}

}
