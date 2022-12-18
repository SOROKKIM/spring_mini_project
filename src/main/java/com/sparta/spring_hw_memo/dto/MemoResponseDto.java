package com.sparta.spring_hw_memo.dto;

import com.sparta.spring_hw_memo.entity.Memo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoResponseDto {

    private Long id;
    private String username;
    private String title;
    private String password;
    private String contents;

    public MemoResponseDto(Memo memo) {
        this.id = memo.getId();
        this.username = memo.getUsername();
        this.title = memo.getTitle();
        this.password = memo.getPassword();
        this.contents = memo.getContents();
    }

}
