package com.assignment.hackernews.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.assignment.hackernews.pojo.Comment;
import com.assignment.hackernews.pojo.Story;
import com.assignment.hackernews.pojo.User;
import com.assignment.hackernews.utility.Constants;

@SpringBootTest
public class AssignmentServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private AssignmentService assignmentService;

	@Test
	void testExtractCommentsFromStory() {
		Story expectedStory = createStory();

		Comment expectedComment = createComment();
		when(assignmentService.getCommentFromId("1234")).thenReturn(expectedComment);

		String id = "2468";
		Mockito.when(
				restTemplate.getForEntity(Constants.storiesURL + id + Constants.urlSuffix, Comment.class).getBody())
				.thenReturn(expectedComment);
		assertEquals(expectedComment, assignmentService.extractCommentsFromStory(expectedStory));

	}

	@Test
	void testGetStoryFromId() {
		Story expectedStory = createStory();
		String storyId = "1";
		Mockito.when(restTemplate.getForEntity(Constants.storiesURL + storyId + Constants.urlSuffix, Story.class))
				.thenReturn(new ResponseEntity<Story>(expectedStory, HttpStatus.OK));
		Story actualStory = assignmentService.getStoryFromId(storyId);
		assertEquals(expectedStory, actualStory);
	}
	
	
	public void testExtractStories() {
		
	}

	@Test
	public void testGetTopStories() {
		String expectedTopStory = createTopStories();
		Mockito.when(restTemplate.getForEntity(Constants.topStoriesURL + Constants.urlSuffix, String.class))
				.thenReturn(new ResponseEntity<String>(expectedTopStory, HttpStatus.OK));
		String actualTopStories = assignmentService.getTopStories();
		assertEquals(expectedTopStory, actualTopStories);

	}
	
	private static List<Story> createListOfStories(){
		List<Story> list = new ArrayList<Story>();
		list.add(createStory());
		return list;
	}

	private static String createTopStories() {
		String stories = "[1,2,3,4,5]";
		return stories;
	}
	private static Story createStory() {
		Story story = new Story();
		story.setBy("StoryAuthor");
		story.setKids(createStoryKids());
		story.setScore(100);
		story.setTime("1590691405739");
		story.setTitle("Story");
		return story;
	}

	private static Comment createComment() {
		Comment comment = new Comment();
		comment.setBy("CommentAuthor");
		comment.setKids(createCommentKids());
		comment.setText("CommentText");
		return comment;
	}

	private static List<String> createCommentKids() {
		List<String> list = new ArrayList<String>();
		list.add("2468");
		list.add("1357");
		return list;
	}

	private static List<String> createStoryKids() {
		List<String> list = new ArrayList<String>();
		list.add("1234");
		list.add("5678");
		return list;
	}
}
