package com.sparta.spring_hw_memo.entity;

import com.sparta.spring_hw_memo.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment_content;

    @ManyToOne
    @JoinColumn(name = "memo_id")
    private Memo memo;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;



    public Comment(Memo memo, CommentRequestDto requestDto, User user)
    {
        this.memo = memo;
        this.comment_content = requestDto.getComment();
        this.user = user;
    }

    public void update(CommentRequestDto requestDto) {
        this.comment_content = requestDto.getComment();
    }
}