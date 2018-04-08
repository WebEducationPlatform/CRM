package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table
public class EmailTemplate {
	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true)
	private String name;

	@Lob
	private String templateText;

	public EmailTemplate() {
	}

	public EmailTemplate(String name, String templateText) {
		this.name = name;
		this.templateText = templateText;
	}

	public Long getId() {

		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplateText() {
		return templateText;
	}

	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EmailTemplate)) return false;

		EmailTemplate that = (EmailTemplate) o;

		if (!id.equals(that.id)) return false;
		if (!name.equals(that.name)) return false;
		return templateText.equals(that.templateText);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + templateText.hashCode();
		return result;
	}
}
