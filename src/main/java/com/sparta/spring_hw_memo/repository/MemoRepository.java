package com.sparta.spring_hw_memo.repository;


import com.sparta.spring_hw_memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {
//    List<Memo> findAllByOrderByCreatedAtDesc();

    Optional<Memo> findByIdAndUserId(Long id, Long userId);

    List<Memo> findAllByUserId(Long userId);
}