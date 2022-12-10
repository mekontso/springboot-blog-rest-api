package com.mas.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mas.blog.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
