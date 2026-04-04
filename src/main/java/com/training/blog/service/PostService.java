package com.training.blog.service;

import com.training.blog.dto.PostResponse;
import com.training.blog.dto.PostsPageResponse;
import com.training.blog.model.Post;
import com.training.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponse create(Post request) {
        return postRepository.create(request.title(), request.text(), request.tags());
    }

    public PostResponse getById(Long id) {
        return postRepository.findById(id);
    }

    public PostResponse update(Long id, String title, String text, List<String> tags) {
        return postRepository.update(id, title, text, tags);
    }

    public PostsPageResponse getPosts(String search, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        PostsPageResponse response;
        List<PostResponse> posts = postRepository.findAll(search, offset, pageSize);

        if (!posts.isEmpty()) {
            long total = postRepository.count(search);
            int lastPage = (int) Math.ceil((double) total / pageSize);

            boolean hasPrev = pageNumber > 1;
            boolean hasNext = pageNumber < lastPage;
            response = new PostsPageResponse(posts, hasPrev, hasNext, lastPage);
        } else {
            response = new PostsPageResponse(posts, false, false, 0);
        }

        return response;
    }
}
