package com.sparta.spring_hw_memo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemoRequestDto {
    private String username;
    private String title;
    private String password;
    private String contents;
}
