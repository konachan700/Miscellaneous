package org.foximager.dao;

import com.jneko.jnekouilib.anno.*;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@UIListItem
@UILibDataSource
public class TagCategory implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique = true, nullable = false)
	private long ID;

	@Column(name="categoryName", unique = false, nullable = true, length = 255)
	private String categoryName;

	@Column(name="categoryText", unique = false, nullable = true, length = 255)
	private String categoryText;

	@Column(name="enable", unique = false)
	private boolean enable;

	@UISortIndex(index=-100)
	@UILongField(name="DBID", type=UIFieldType.GETTER, readOnly=1, labelText="DB ID")
	public long getID() {
		return ID;
	}

	@UILongField(name="DBID", type=UIFieldType.SETTER)
	public void setID(long ID) {
		this.ID = ID;
	}

	@UISortIndex(index=1)
	@UIStringField(name="categoryName", type=UIFieldType.GETTER, readOnly=0, maxChars=255, helpText="Enter text here", labelText="Tag group name")
	public String getCategoryName() {
		return this.categoryName;
	}

	@UIStringField(name="categoryName", type=UIFieldType.SETTER)
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@UISortIndex(index=2)
	@UITextArea(name="categoryText", type=UIFieldType.GETTER, readOnly=0, maxChars=4096, helpText="Enter text here", labelText="Any text for tag group")
	public String getCategoryText() {
		return this.categoryText;
	}

	@UITextArea(name="categoryText", type=UIFieldType.SETTER)
	public void setCategoryText(String categoryText) {
		this.categoryText = categoryText;
	}

	@UISortIndex(index=3)
	@UIBooleanField(name="enable", type=UIFieldType.GETTER, readOnly=0, labelText="Group is used")
	public boolean getEnable() {
		return this.enable;
	}

	@UIBooleanField(name="enable", type=UIFieldType.SETTER)
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}

