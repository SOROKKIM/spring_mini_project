package com.sparta.spring_hw_memo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.spring_hw_memo.entity.Memo;
import com.sparta.spring_hw_memo.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemoResponseDto {

    private Long id;
    private String username;
    private String title;
//    private String password;
    private String contents;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    private List<CommentResponseDto> comments;

    public MemoResponseDto(Memo memo) {
        this.id = memo.getId();
        this.username = memo.getUsername();
        this.title = memo.getTitle();
        this.contents = memo.getContents();
        this.createdAt = memo.getCreatedAt();
        this.modifiedAt = memo.getModifiedAt();
        this.comments = new ArrayList<>();

    }

}
