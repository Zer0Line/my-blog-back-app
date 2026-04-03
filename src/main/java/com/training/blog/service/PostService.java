package com.training.blog.service;

import com.training.blog.dto.PostResponse;
import com.training.blog.dto.PostsPageResponse;
import com.training.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostsPageResponse getPosts(String search, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;

        List<PostResponse> posts = postRepository.findAll(search, offset, pageSize);
        long total = postRepository.count(search);
        int lastPage = (int) Math.ceil((double) total / pageSize);

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        return new PostsPageResponse(posts, hasPrev, hasNext, lastPage);
    }
}
