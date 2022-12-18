package com.sparta.spring_hw_memo.controller;

import com.sparta.spring_hw_memo.dto.MemoRequestDto;
import com.sparta.spring_hw_memo.dto.MemoResponseDto;
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

//    @Autowired
//    public MController(MemoService memoService) {
//        this.memoService =  memoService;
//    }

    // 메모 등록하기
    @PostMapping("/memos")
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto requestDto, HttpServletRequest request) {

        return memoService.createMemo(requestDto, request);
    }


    //메모 조회하기
    @GetMapping("/memos")
    public List<MemoResponseDto> getMemos(HttpServletRequest request) {

        return memoService.getMemos(request);
    }

    //메모 업데이트
    @PutMapping("/memos/{id}")
    public Long updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto requestDto, HttpServletRequest request) {
        return memoService.update(id, requestDto, request);
    }

    //메모 삭제
    @DeleteMapping("/memos/{id}")
    public Long deleteMemo(@PathVariable Long id, @RequestBody MemoRequestDto requestDto, HttpServletRequest request) {
        return memoService.deleteMemo(id, requestDto, request);
    }
}

