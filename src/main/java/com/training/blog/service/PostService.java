package com.training.blog.service;

import com.training.blog.dto.Post;
import com.training.blog.dto.PostResponse;
import com.training.blog.dto.PostsPageResponse;
import com.training.blog.exception.PostNotFoundException;
import com.training.blog.repository.CommentRepository;
import com.training.blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FilesService filesService;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, FilesService filesService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.filesService = filesService;
    }

    @Transactional
    public PostResponse create(Post request) {
        return postRepository.create(request.title(), request.text(), request.tags());
    }

    public PostResponse getById(Long id) {
        return Optional.ofNullable(postRepository.findById(id))
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    @Transactional
    public PostResponse update(Long id, String title, String text, List<String> tags) {
        return postRepository.update(id, title, text, tags);
    }

    public PostsPageResponse getPosts(String search, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;

        String[] searchWords = search.split("\\s+");
        List<String> tags = new ArrayList<>();
        StringBuilder textSearch = new StringBuilder();

        for (String word : searchWords) {
            if (word.isEmpty()) {
                continue;
            }
            if (word.startsWith("#")) {
                String tag = word.substring(1);
                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            } else {
                if (!textSearch.isEmpty()) {
                    textSearch.append(" ");
                }
                textSearch.append(word);
            }
        }

        String textQuery = textSearch.toString();

        PostsPageResponse response;
        List<PostResponse> posts = postRepository.findAll(textQuery, tags, offset, pageSize);

        if (!posts.isEmpty()) {
            long total = postRepository.count(textQuery, tags);
            int lastPage = (int) Math.ceil((double) total / pageSize);

            boolean hasPrev = pageNumber > 1;
            boolean hasNext = pageNumber < lastPage;
            response = new PostsPageResponse(posts, hasPrev, hasNext, lastPage);
        } else {
            response = new PostsPageResponse(posts, false, false, 0);
        }

        return response;
    }

    @Transactional
    public Long addLike(Long id) {
        return postRepository.addLike(id);
    }

    @Transactional
    public void delete(Long id) {
        filesService.deletePostImage(id);
        commentRepository.deleteByPostId(id);
        postRepository.delete(id);
    }
}


