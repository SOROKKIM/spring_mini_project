package com.sparta.spring_hw_memo.repository;


import com.sparta.spring_hw_memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findAllByOrderByCreatedAtDesc();
}