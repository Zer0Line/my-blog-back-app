package com.training.blog.mapper;

import com.training.blog.domain.Comment;
import com.training.blog.dto.CommentCreateRequest;
import com.training.blog.dto.CommentResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponse toCommentResponse(Comment comment) {
        CommentResponse cr = new CommentResponse();
        cr.setId(comment.getId());
        cr.setText(comment.getText());
        cr.setPostId(cr.getPostId());
        return cr;
    }

    public Comment toComment(CommentCreateRequest requet) {
        Comment cmt = new Comment();
        cmt.setText(requet.text());
        return cmt;
    }
}
