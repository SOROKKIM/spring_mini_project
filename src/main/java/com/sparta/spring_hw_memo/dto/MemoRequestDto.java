package com.sparta.spring_hw_memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemoRequestDto {
    private Long id;
    private String username; //메모 작성자 이름
    private String title; // 메모 제목
//    private String password; //메모 비밀번호
    private String contents; // 메모 내용

}
