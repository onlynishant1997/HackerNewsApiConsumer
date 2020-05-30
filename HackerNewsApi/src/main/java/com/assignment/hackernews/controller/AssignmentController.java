package com.assignment.hackernews.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.hackernews.pojo.Story;
import com.assignment.hackernews.response.CommentResponse;
import com.assignment.hackernews.service.AssignmentService;

/**
 * The Class AssignmentController.
 *
 * @author Nishant
 */
@RestController
public class AssignmentController {

	@Autowired
	private AssignmentService storiesService;

	/**
	 * Gets the top ten stories.
	 *
	 * @return the top ten stories
	 */
	@GetMapping("/top-stories")
	public ResponseEntity<List<Story>> getTopTenStories() {
		return ResponseEntity.ok(storiesService.getStoryOfLastTenMinutes());
	}

	/**
	 * Gets the comments on story.
	 *
	 * @param storyId the story id
	 * @return the comments on story
	 */
	@GetMapping("/comments/{storyId}")
	public ResponseEntity<List<CommentResponse>> getCommentsOnStory(@PathVariable String storyId) {
		return ResponseEntity.of(storiesService.extractCommentsFromStory(storyId));
	}

	/**
	 * Past top stories that were serverd previously.
	 *
	 * @return the response entity
	 */
	@GetMapping("/past-stories")
	public ResponseEntity<List<Story>> pastTopStoriesThatWereServerdPreviously() {
		List<Story> pastStories = storiesService.getPastTopStoriesThatWereServerdPreviously();
		if (pastStories == null || pastStories.size() == 0) {
			return new ResponseEntity<List<Story>>(HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok(pastStories);
	}

}
