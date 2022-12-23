package com.sparta.spring_hw_memo.dto;

import com.sparta.spring_hw_memo.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private Long boardId;
    private String comment;
    private Long userId;

    public CommentResponseDto(Comment comment){
        this.commentId = comment.getId();
        this.boardId = comment.getMemo().getId();
        this.comment = comment.getComment_content();
        this.userId = comment.getUser().getId();
    }
}
