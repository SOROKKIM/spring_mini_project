package com.sparta.spring_hw_memo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.spring_hw_memo.entity.Comment;

import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Long> {
    List<Comment> findAllByOrderByModifiedAtDesc();
    List<Comment> findAllByMemo_IdOrderByModifiedAtDesc(Long memo_id);
}
