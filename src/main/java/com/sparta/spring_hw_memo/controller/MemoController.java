package com.sparta.spring_hw_memo.controller;

import com.sparta.spring_hw_memo.dto.*;
import com.sparta.spring_hw_memo.entity.Memo;
import com.sparta.spring_hw_memo.service.MemoService;
import com.sparta.spring_hw_memo.repository.MemoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;
    private final MemoRepository memoRepository;

    // 메모 등록하기
    @ResponseBody
    @PostMapping("/memos")
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto requestDto, HttpServletRequest request) {

        return memoService.createMemo(requestDto, request);
    }


    //메모 조회하기
    @GetMapping("/memos")
    @ResponseBody
    public List<MemoResponseDto> getMemos() {

        return memoService.getMemos();
    }

    //메모 상세 조회
    @GetMapping("/memos/detail")
    @ResponseBody
    public MemoResponseDto getMemo(@RequestParam Long id) {

        return memoService.getMemo(id);
    }

    //메모 수정
    @PutMapping("/memos/{id}")
    @ResponseBody
    public Long updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto requestDto, HttpServletRequest request) {
        return memoService.update(id, requestDto, request);
    }

    //메모 삭제
    @DeleteMapping("/memos/{id}")
    @ResponseBody
    public Long deleteMemo(@PathVariable Long id, HttpServletRequest request) {
        return memoService.deleteMemo(id, request);
    }


    // 댓글 작성
    @PostMapping("/comment/{id}")
    public CommentResponseDto createComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest request) {
        return memoService.createComment(id, requestDto, request);
    }

    // 댓글 수정
    @PutMapping("/comment/{comment_id}")
    public CommentResponseDto updateComment(@PathVariable Long comment_id, @RequestBody CommentRequestDto requestDto, HttpServletRequest request) {
        return memoService.updateComment(comment_id, requestDto, request);
    }


    // 댓글 삭제
    @DeleteMapping("/comment/{comment_id}")
    public ResponseDto deleteComment(@PathVariable Long comment_id, HttpServletRequest request) {
        return memoService.deleteComment(comment_id, request);
    }
}

