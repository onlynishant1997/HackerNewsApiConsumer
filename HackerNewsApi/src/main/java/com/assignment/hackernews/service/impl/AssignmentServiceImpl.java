package com.assignment.hackernews.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assignment.hackernews.dao.StoryRepository;
import com.assignment.hackernews.pojo.Comment;
import com.assignment.hackernews.pojo.Story;
import com.assignment.hackernews.pojo.User;
import com.assignment.hackernews.response.CommentResponse;
import com.assignment.hackernews.service.AssignmentService;
import com.assignment.hackernews.utility.Constants;

@Service
public class AssignmentServiceImpl implements AssignmentService {

	// Configurable... Keeping it 240 to get response of last 4 hours, you can make
	// it 10 for getting top story of last 10 minutes
	private static final int minutes_for_records = 240;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	StoryRepository storyRepository;

	/**
	 * Returns the story of last ten minutes. Also saves to the database
	 *
	 * @return the story of last ten minutes
	 */
	@Cacheable(value = "topStoriesCache")
	@Transactional
	public List<Story> getStoryOfLastTenMinutes() {
		String topStories = getTopStories();
		// Converting String response to List
		List<String> listOfStoryIds = Arrays.stream(topStories.substring(1, topStories.length() - 1).split(","))
				.collect(Collectors.toList());
		List<Story> listOfTopStories;
		listOfTopStories = extractStories(listOfStoryIds);
		List<Story> topTenStories = getTopTenStoriesInLastTenMinutesSortedByScore(listOfTopStories);
		storyRepository.saveAll(topTenStories);
		return topTenStories;
	}

	/**
	 * Extract comments from story.
	 *
	 * @param storyId the story id
	 * @return the optional
	 */
	@Cacheable(value = "commentsCache")
	public Optional<List<CommentResponse>> extractCommentsFromStory(String storyId) {

		Story story = getStoryFromId(storyId);

		if (story == null)
			return Optional.empty();

		if (story.getKids() == null)
			return Optional.empty();

		// Get All Comments Ids on the story
		List<String> commentsId = story.getKids();

		// Keep first 10 commentsId and delete rest since the api returns comments in
		// ranked order.
		if (commentsId.size() > 10)
			commentsId.subList(10, commentsId.size()).clear();

		List<Comment> comments;
		comments = fetchComentsFromStories(commentsId);

		// Sort the comment based on the count of child comments.
		sortCommentsBasedOnChildCommentsCount(comments);

		// Convert Comment to the CommentResponse
		List<CommentResponse> response = new ArrayList<CommentResponse>();
		for (Comment comment : comments) {
			CommentResponse commentResponse = convertCommentToCommentResponse(comment);
			response.add(commentResponse);
		}
		return Optional.of(response);
	}

	/**
	 * Gets the past top stories that were serverd previously.
	 *
	 * @return the past top stories that were serverd previously
	 */
	@Cacheable(value = "pastStoriesCache")
	public List<Story> getPastTopStoriesThatWereServerdPreviously() {
		List<Story> pastStories = storyRepository.findAll();
		return pastStories;
	}

	/**
	 * Extract stories Creates A Callable task to fetch the story based on the
	 * storyId from the api and executes
	 *
	 * @param listOfStoryIds the list of story ids
	 * @return the list
	 */
	public List<Story> extractStories(List<String> listOfStoryIds) {
		List<Story> listOfStories = new ArrayList<Story>();
		List<Callable<Story>> tasks = new ArrayList<>();
		ExecutorService service;
		for (String storyId : listOfStoryIds) {
			tasks.add(new StoryWorker(storyId));
		}
		try {
			service = Executors.newFixedThreadPool(500);
			List<Future<Story>> futures = service.invokeAll(tasks);
			futures.forEach(future -> {
				try {
					listOfStories.add(future.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return listOfStories;
	}

	/**
	 * Return the top ten stories in last ten minutes sorted by score.
	 *
	 * @param listOfTopStories the list of top stories
	 * @return the top ten stories in last ten minutes sorted by score
	 */
	private List<Story> getTopTenStoriesInLastTenMinutesSortedByScore(List<Story> listOfTopStories) {
		return listOfTopStories.stream().sorted(Comparator.comparing(Story::getTime).reversed()).limit(10)
				.sorted(Comparator.comparing(Story::getScore)).filter(story -> {
					Date myDate = new Date(System.currentTimeMillis());
					Date tenMinutesBefore = new Date(myDate.getTime() - (minutes_for_records * 60 * 1000L));
					Date storyDate = new Date(Long.valueOf(story.getTime()) * 1000L);
					return storyDate.after(tenMinutesBefore);
				}).collect(Collectors.toList());
	}

	/**
	 * Fetch Comments From Stories Creates A Callable task to fetch the Comments
	 * based on the storyId from the api and executes.
	 *
	 * @param storyId the story id
	 * @return the optional
	 */
	private List<Comment> fetchComentsFromStories(List<String> commentsId) {
		List<Comment> comments = new ArrayList<Comment>();
		List<Callable<Comment>> tasks = new ArrayList<>();
		ExecutorService service;
		for (String commentId : commentsId) {
			tasks.add(new CommentWorker(commentId));
		}
		try {
			service = Executors.newFixedThreadPool(30);
			List<Future<Comment>> futures = service.invokeAll(tasks);
			futures.forEach(future -> {
				try {
					comments.add(future.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return comments;
	}

	/**
	 * Sort comments based on child comments count.
	 *
	 * @param comments the comments
	 */
	private void sortCommentsBasedOnChildCommentsCount(List<Comment> comments) {
		comments.sort((Comment comment1, Comment comment2) -> {
			int size1 = 0, size2 = 0;
			if (comment1.getKids() != null)
				size1 = comment1.getKids().size();
			if (comment2.getKids() != null)
				size2 = comment2.getKids().size();
			return size1 - size2;
		});
	}

	/**
	 * Gets the user creation year.
	 *
	 * @param by the by
	 * @return the user creation year
	 */
	private String getUserCreationYear(String by) {
		User user = getUserNyName(by);
		long time = Long.valueOf(user.getCreated());
		Instant instant = Instant.ofEpochMilli(time * 1000l);
		LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		return String.valueOf(date.getYear());
	}

	/**
	 * Convert comment to comment response.
	 *
	 * @param comment the comment
	 * @return the comment response
	 */
	private CommentResponse convertCommentToCommentResponse(Comment comment) {
		CommentResponse commentResponse = new CommentResponse();
		commentResponse.setText(comment.getText());
		commentResponse.setUserHnHandle(comment.getBy());
		commentResponse.setHnAge("The User is since " + getUserCreationYear(comment.getBy()));
		return commentResponse;
	}

	/**
	 * Gets the user ny name.
	 *
	 * @param by the by
	 * @return the user ny name
	 */
	private User getUserNyName(String by) {
		return restTemplate.getForEntity(Constants.userURL + by + Constants.urlSuffix, User.class).getBody();
	}

	/**
	 * Returns the ids of top 500 stories.
	 *
	 * @return the top stories
	 */
	private String getTopStories() {
		return restTemplate.getForEntity(Constants.topStoriesURL + Constants.urlSuffix, String.class).getBody();
	}

	/**
	 * Gets the comment from id.
	 *
	 * @param id the id
	 * @return the comment from id
	 */
	private Comment getCommentFromId(String id) {
		Comment jsonResponse = restTemplate.getForEntity(Constants.storiesURL + id + Constants.urlSuffix, Comment.class)
				.getBody();
		return jsonResponse;
	}

	/**
	 * Gets the story from id.
	 *
	 * @param storyId the story id
	 * @return the story from id
	 */
	private Story getStoryFromId(String storyId) {
		Story story = restTemplate.getForEntity(Constants.storiesURL + storyId + Constants.urlSuffix, Story.class)
				.getBody();
		return story;
	}

	class StoryWorker implements Callable<Story> {
		private String id;

		StoryWorker(String id) {
			this.id = id;
		}

		@Override
		public Story call() throws Exception {
			return getStoryFromId(id);
		}

	}

	class CommentWorker implements Callable<Comment> {
		private String id;

		CommentWorker(String id) {
			this.id = id;
		}

		@Override
		public Comment call() throws Exception {
			return getCommentFromId(id);
		}

	}

}
