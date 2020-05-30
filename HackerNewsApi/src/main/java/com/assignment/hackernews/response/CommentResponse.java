/*
 * 
 */
package com.assignment.hackernews.response;

/**
 * CommentResponse.
 *
 * @author Nishant
 */
public class CommentResponse {
	String text;
	String userHnHandle;
	String hnAge;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUserHnHandle() {
		return userHnHandle;
	}

	public void setUserHnHandle(String userHnHandle) {
		this.userHnHandle = userHnHandle;
	}

	public String getHnAge() {
		return hnAge;
	}

	public void setHnAge(String hnAge) {
		this.hnAge = hnAge;
	}

}
