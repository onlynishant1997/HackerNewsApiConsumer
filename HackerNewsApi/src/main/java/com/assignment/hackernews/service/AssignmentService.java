package com.assignment.hackernews.service;

import java.util.List;
import java.util.Optional;

import com.assignment.hackernews.pojo.Story;
import com.assignment.hackernews.response.CommentResponse;

/**
 * The Interface AssignmentService.
 *
 * @author Nishant
 */
public interface AssignmentService {
	
	/**
	 * Gets the story of last ten minutes.
	 *
	 * @return the story of last ten minutes
	 */
	public List<Story> getStoryOfLastTenMinutes();

	/**
	 * Extract comments from story.
	 *
	 * @param storyId the story id
	 * @return the optional
	 */
	public Optional<List<CommentResponse>> extractCommentsFromStory(String storyId);

	/**
	 * Gets the past top stories that were serverd previously.
	 *
	 * @return the past top stories that were serverd previously
	 */
	public List<Story> getPastTopStoriesThatWereServerdPreviously();
}
