package com.mas.blog.service;

import com.mas.blog.payload.PostDto;
import com.mas.blog.payload.PostResponse;


public interface PostService {
    PostDto createPost(PostDto postDto);

    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);

    PostDto getPostById(Long id);

    PostDto updatePost(PostDto postDto, Long id);

    void deletePost(Long id);
}
