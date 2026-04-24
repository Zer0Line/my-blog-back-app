package com.training.blog.mapper;

import com.training.blog.domain.Post;
import com.training.blog.dto.PostCreateRequest;
import com.training.blog.dto.PostResponse;
import com.training.blog.dto.UpdatePostRequest;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public Post toPost(PostCreateRequest postRequest) {
        Post post = new Post();
        post.setTitle(postRequest.title());
        post.setText(postRequest.text());
        post.setTags(postRequest.tags());
        return post;
    }

    public PostResponse toPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getText(),
                post.getTags(),
                post.getLikesCount(),
                post.getCommentsCount()
        );
    }

    public Post toPost(UpdatePostRequest updatePostRequest) {
        Post post = new Post();
        post.setTitle(updatePostRequest.title());
        post.setText(updatePostRequest.text());
        post.setTags(updatePostRequest.tags());
        return post;
    }
}
