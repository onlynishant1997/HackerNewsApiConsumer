package com.assignment.hackernews.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Comment POJO.
 *
 * @author Nishant
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
	String by;
	String text;
	List<String> kids;

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getKids() {
		return kids;
	}

	public void setKids(List<String> kids) {
		this.kids = kids;
	}

	@Override
	public String toString() {
		return "Comment [by=" + by + ", text=" + text + ", kids=" + kids + "]";
	}

}