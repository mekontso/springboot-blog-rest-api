package com.mas.blog.service.impl;

import com.mas.blog.entity.Comment;
import com.mas.blog.entity.Post;
import com.mas.blog.exception.BlogAPIException;
import com.mas.blog.exception.ResourceNotFoundException;
import com.mas.blog.payload.CommentDto;
import com.mas.blog.repository.CommentRepository;
import com.mas.blog.repository.PostRepository;
import com.mas.blog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final ModelMapper mapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Comment comment = mapToEntity(commentDto);
        // retrieve post
        Post post = getPostById(postId);
        // update post
        comment.setPost(post);
        // save DB
        comment = commentRepository.save(comment);
        return mapToDto(comment);
    }


    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        // retrieve posy by id
        Post post = getPostById(postId);
        // retrieve comment by id
        Comment comment = getCommentById(commentId);
        checkCommentBelongsToPost(post, comment);
        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        // retrieve posy by id
        Post post = getPostById(postId);
        // retrieve comment by id
        Comment comment = getCommentById(commentId);
        checkCommentBelongsToPost(post, comment);
        // update data fields
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        // update convert and return
        return mapToDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        // retrieve posy by id
        Post post = getPostById(postId);
        // retrieve comment by id
        Comment comment = getCommentById(commentId);
        // check
        checkCommentBelongsToPost(post, comment);
        // delete
        commentRepository.delete(comment);
    }

    private CommentDto mapToDto(Comment comment) {
        return mapper.map(comment, CommentDto.class);
        //       CommentDto commentDto = mapper.map(comment,CommentDto.class);
//        commentDto.setId(comment.getId());
//        commentDto.setName(comment.getName());
//        commentDto.setEmail(comment.getEmail());
//        commentDto.setBody(comment.getBody());
        //return commentDto;
    }

    private Comment mapToEntity(CommentDto commentDto) {
        return mapper.map(commentDto, Comment.class);
//        Comment comment = new Comment();
//        comment.setId(commentDto.getId());
//        comment.setName(commentDto.getName());
//        comment.setEmail(commentDto.getEmail());
//        comment.setBody(commentDto.getBody());
//        return comment;
    }

    private Post getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        return post;
    }

    private Comment getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "Id", commentId));
        return comment;
    }

    private void checkCommentBelongsToPost(Post post, Comment comment) {
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }
    }
}
