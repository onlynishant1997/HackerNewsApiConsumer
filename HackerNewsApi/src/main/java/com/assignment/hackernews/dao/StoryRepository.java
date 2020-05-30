package com.assignment.hackernews.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignment.hackernews.pojo.Story;

/**
 * Story Repository.
 *
 * @author Nishant
 */
@Repository
public interface StoryRepository extends JpaRepository<Story, String>{

}
