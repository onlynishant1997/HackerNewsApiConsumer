package com.assignment.hackernews.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.assignment.hackernews.dao.StoryRepository;
import com.assignment.hackernews.pojo.Story;
import com.assignment.hackernews.response.CommentResponse;
import com.assignment.hackernews.service.impl.AssignmentServiceImpl;
import com.assignment.hackernews.utility.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AssignmentServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	StoryRepository storyRepository;

	@Mock
	private AssignmentServiceImpl assignmentService;

	@Test
	public void testGetStoryOfLastTenMinutes() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Story> expectedStory = createListOfStories();
		Mockito.when(assignmentService.getStoryOfLastTenMinutes()).thenReturn(createListOfStories());

		Mockito.when(restTemplate.getForEntity(Constants.topStoriesURL + Constants.urlSuffix, String.class))
				.thenReturn(new ResponseEntity<String>(createTopStories(), HttpStatus.OK));
		List<Story> actualStories = assignmentService.getStoryOfLastTenMinutes();
		assertEquals(expectedStory.size(), actualStories.size());
		assertEquals(expectedStory.get(0).getBy(), actualStories.get(0).getBy());
		assertEquals(expectedStory.get(0).getKids(), actualStories.get(0).getKids());
	}

	@Test
	public void testGetPastTopStoriesThatWereServerdPreviously() {
		List<Story> expectedStory = createListOfStories();
		Mockito.when(storyRepository.findAll()).thenReturn(expectedStory);
		Mockito.when(assignmentService.getPastTopStoriesThatWereServerdPreviously()).thenReturn(expectedStory);
		List<Story> actualStories = assignmentService.getPastTopStoriesThatWereServerdPreviously();
		assertEquals(expectedStory.size(), actualStories.size());
		assertEquals(expectedStory.get(0).getBy(), actualStories.get(0).getBy());
		assertEquals(expectedStory.get(0).getKids(), actualStories.get(0).getKids());
	}

	@Test
	public void testExtractCommentsFromStory() {
		String storyId = "1";
		List<CommentResponse> expected = createListOfCommentResponse();
		Mockito.when(assignmentService.extractCommentsFromStory(storyId)).thenReturn(Optional.of(expected));
		List<CommentResponse> actual = assignmentService.extractCommentsFromStory(storyId).get();
		assertEquals(expected.size(), actual.size());
		assertEquals(expected.get(0).getText(), actual.get(0).getText());
	}

	private static List<CommentResponse> createListOfCommentResponse() {
		List<CommentResponse> list = new ArrayList<CommentResponse>();
		list.add(createCommentResponse());
		return list;
	}

	private static CommentResponse createCommentResponse() {
		CommentResponse commentResponse = new CommentResponse();
		commentResponse.setHnAge("11");
		commentResponse.setText("Hello");
		commentResponse.setUserHnHandle("User");
		return commentResponse;
	}

	private static List<Story> createListOfStories() {
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


	private static List<String> createStoryKids() {
		List<String> list = new ArrayList<String>();
		list.add("1234");
		list.add("5678");
		return list;
	}
}
