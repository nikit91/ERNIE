package dice.nikit_thesis.model;

import java.util.ArrayList;
import java.util.List;

public class DbpAbstract {

	private String content;
	private String abstractUri;
	private List<EntityMention> mentions;

	public DbpAbstract(String content, String abstractUri) {
		super();
		this.content = content;
		this.abstractUri = abstractUri;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAbstractUri() {
		return abstractUri;
	}
	
	public void addEntityMention(EntityMention mention) {
		if(this.mentions == null) {
			this.mentions = new ArrayList<EntityMention>();
		}
		mentions.add(mention);
	}

	public void setAbstractUri(String abstractUri) {
		this.abstractUri = abstractUri;
	}

	public List<EntityMention> getMentions() {
		return mentions;
	}

	public void setMentions(List<EntityMention> mentions) {
		this.mentions = mentions;
	}

	@Override
	public String toString() {
		return "DbpAbstract [content=" + content + ", abstractUri=" + abstractUri + ", mentions="
				+ mentions + "]";
	}

}
