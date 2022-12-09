package com.sparta.spring_hw_memo.controller;


import com.sparta.spring_hw_memo.dto.MemoRequestDto;
import com.sparta.spring_hw_memo.entity.Memo;
import com.sparta.spring_hw_memo.service.MemoService;
import com.sparta.spring_hw_memo.repository.MemoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;
    private final MemoRepository memoRepository;

    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("index"); //templates에 있는 index.html 파일 반환해주는 기능
    }

    @PostMapping("/api/memos")
    public Memo createMemo(@RequestBody MemoRequestDto requestDto) {
        Memo memo = new Memo(requestDto);
        return memoRepository.save(memo);
    }

    @GetMapping("/api/memos")
    public List<Memo> getMemos() {
        return memoRepository.findAllByOrderByCreatedAtDesc();
    }
    @GetMapping("/memos/{id}")
    public Optional<Memo> getMemo(@PathVariable Long id){
        return memoRepository.findById(id);
    }

    @PutMapping("/api/memos/{id}")
    public Long updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto requestDto) {
        Optional<Memo> memo = memoRepository.findById(id);
        if (memo.isPresent()) {
            System.out.println("memo.get().getPassword() : " + memo.get().getPassword() + " requestDto.getPassword() : " + requestDto.getPassword());
            if (memo.get().getPassword().equals(requestDto.getPassword())) {
                memoService.update(id, requestDto);
            } else {
                System.out.println("비밀번호 오류");
                return 0L;
            }
        }
        return id;
    }


    @DeleteMapping("/api/memos/{id}")
    public Long deleteMemo(@PathVariable Long id, @RequestBody MemoRequestDto requestDto) {
        Optional <Memo> memo = memoRepository.findById(id);
        if (memo.isPresent()) {
            System.out.println("memo.get().getPassword() : " + memo.get().getPassword() + " requestDto.getPassword() : " + requestDto.getPassword());
            if (memo.get().getPassword().equals(requestDto.getPassword())) {
                memoRepository.deleteById(id);
            } else {
                System.out.println("비밀번호 오류");
                return 0L;
            }
        }
        return id;
    }
}

