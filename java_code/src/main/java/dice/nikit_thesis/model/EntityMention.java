package dice.nikit_thesis.model;

public class EntityMention {
	private String entityUri;
	private int entityId;
	private String surfaceForm;
	private int startPos;
	private int endPos;

	public EntityMention(String entityUri, int entityId, String surfaceForm, int startPos, int endPos) {
		super();
		this.entityUri = entityUri;
		this.entityId = entityId;
		this.surfaceForm = surfaceForm;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public String getEntityUri() {
		return entityUri;
	}

	public void setEntityUri(String entityUri) {
		this.entityUri = entityUri;
	}

	public String getSurfaceForm() {
		return surfaceForm;
	}

	public void setSurfaceForm(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	@Override
	public String toString() {
		return "EntityMention [entityUri=" + entityUri + ", entityId=" + entityId + ", surfaceForm=" + surfaceForm
				+ ", startPos=" + startPos + ", endPos=" + endPos + "]";
	}

}
