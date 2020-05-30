package com.assignment.hackernews.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The User POJO.
 *
 * @author Nishant
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	String created;

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

}
