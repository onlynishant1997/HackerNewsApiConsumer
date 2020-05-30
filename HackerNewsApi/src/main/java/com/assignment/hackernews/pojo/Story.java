
package com.assignment.hackernews.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * The Story Entity.
 *
 * @author Nishant
 */
@Entity
@Table(name = "STORY")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Story {
	@JsonProperty(access = Access.WRITE_ONLY)
	@Id
	String id;
	@Column(name = "TITLE")
	String title;
	@Column(name = "SCORE")
	int score;
	@Column(name = "TIME_IN_MILLI_SECONDS")
	String time;
	@Column(name = "SUBMITTED_BY")
	String by;
	@Column(name = "URL")
	String url;

	@JsonProperty(access = Access.WRITE_ONLY)
	@ElementCollection
	List<String> kids;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public List<String> getKids() {
		return kids;
	}

	public void setKids(List<String> kids) {
		this.kids = kids;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Story [id=" + id + ", title=" + title + ", score=" + score + ", time=" + time + ", by=" + by + ", url="
				+ url + ", kids=" + kids + "]";
	}

}
