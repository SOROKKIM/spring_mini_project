package com.sparta.spring_hw_memo.service;

import com.sparta.spring_hw_memo.dto.*;
import com.sparta.spring_hw_memo.entity.Memo;
import com.sparta.spring_hw_memo.entity.User;
import com.sparta.spring_hw_memo.repository.MemoRepository;
import com.sparta.spring_hw_memo.repository.UserRepository;
import com.sparta.spring_hw_memo.entity.UserRoleEnum;
import com.sparta.spring_hw_memo.entity.Comment;
import com.sparta.spring_hw_memo.repository.CommentRepository;
import com.sparta.spring_hw_memo.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service  //DB 또는 Controller 를 통해서 전달받은 데이터를 가지고 DB나 entity + entity에 있는 행위(update ㅁ)들을 시킴.
public class MemoService {
    private final MemoRepository memoRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    //메모 생성하기
    @Transactional
    public MemoResponseDto createMemo(MemoRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 메모 추가 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            // 요청받은 DTO 로 DB에 저장할 객체 만들기
            Memo memo = memoRepository.saveAndFlush(new Memo(requestDto, user));

            return new MemoResponseDto(memo);
        } else {
            return null;
        }
    }

    //메모 조회하기
    @Transactional(readOnly = true)
    public List<MemoResponseDto> getMemos() throws IllegalArgumentException {

//        Claims claims = null;
//
//        // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
//        User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
//                () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
//        );
//
//        // 사용자 권한 가져와서 ADMIN 이면 전체 조회, USER 면 본인이 추가한 부분 조회
//        UserRoleEnum userRoleEnum = user.getRole();
//        System.out.println("role = " + userRoleEnum);
//
        List<MemoResponseDto> memoListResponseDto = new ArrayList<>();
        List<Memo> memoList = memoRepository.findAllByOrderByCreatedAtDesc();
//
//        if (userRoleEnum == UserRoleEnum.USER) {
//            // 사용자 권한이 USER일 경우
//            memoList = memoRepository.findAllByUserId(user.getId());
//        } else {
//            memoList = memoRepository.findAll();
//        }

        for (int i = 0; i < memoList.size(); i++) {
            MemoResponseDto memoResponseDto = new MemoResponseDto(memoList.get(i));
            memoListResponseDto.add(memoResponseDto);
        }

        return memoListResponseDto;

    }


    //메모 상세 조회
    @Transactional(readOnly = true)
    public MemoResponseDto getMemo(Long id) {

        Memo memo = checkMemo(id);
        System.out.println("board = " + memo);

        MemoResponseDto memoResponseDto = new MemoResponseDto(memo);

        List<Comment> commentList = commentRepository.findAllByMemo_IdOrderByModifiedAtDesc(id);

        for (Comment comment : commentList) {
            memoResponseDto.getComments().add(new CommentResponseDto(comment));
        }
        return memoResponseDto ;

    }


    @Transactional
    public Long update(Long id, MemoRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;
//        Optional<Memo> memo = memoRepository.findById(id);

        // 토큰이 있는 경우에만 메모 업데이트 가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            UserRoleEnum role = user.getRole();
            System.out.println("role = " + role);

            System.out.println("claims.getSubject() : " + claims.getSubject());

            Memo memo1 = memoRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 아이디입니다.")
            );
            //            Post post = checkPost(id);
            if (memo1.getUsername().equals(user.getUsername()) || role == UserRoleEnum.ADMIN) {
                memo1.update(requestDto);
            } else {
                System.out.println("게시글 삭제 권한이 없습니다.");
                return 0L;
            }


        } else {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        return id;
    }

    //
    @Transactional
    public Long deleteMemo(Long id, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;


        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            UserRoleEnum role = user.getRole();

            Memo memo = checkMemo(id);
//            if (post.getUsername().equals(claims.getSubject()) || role == UserRoleEnum.ADMIN) {
//                postRepository.deleteById(id);
//            }
//            return new ResponseDto("게시물 삭제 성공", HttpStatus.OK.value());
            if (memo.getUsername().equals(claims.getSubject()) || role == UserRoleEnum.ADMIN) {
                memoRepository.deleteById(id);
            } else {
                System.out.println("게시글 삭제 권한이 없습니다.");
                return 0L;
            }
//            return new ResponseDto("게시물 삭제 성공", HttpStatus.OK.value());

        } else {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }
        // 메모 삭제하기
        return id;
    }

    private Memo checkMemo(Long id) {
        Memo memo = memoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("게시물이 존재하지 않습니다"));
        return memo;
    }

    //**댓글 작성**
    public CommentResponseDto createComment(@PathVariable Long id, @RequestBody CommentRequestDto
            requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            System.out.println("username = " + user.getUsername());


            Memo memo = checkMemo(id);
            Comment commentMemo = new Comment(memo, requestDto, user);
            commentRepository.saveAndFlush(commentMemo);

//                return new BoardCommentResponseDto(commentPost, board);

            return new CommentResponseDto(commentMemo);
        }

        throw new IllegalArgumentException("토큰이 없습니다");


    }


    //**댓글 수정**
    @Transactional
    public CommentResponseDto updateComment(Long comment_id, CommentRequestDto requestDto, HttpServletRequest
            request) {

        String token = jwtUtil.resolveToken(request); //token에 bearer부분 떼고 담기(토큰 유효 검사위함)
        Claims claims;   //토큰 안에 있는 user 정보 담기 위함

        // 토큰이 있는 경우에만 게시글 수정
        if (token != null) {
            if (jwtUtil.validateToken(token)) {       //토큰 유효한지 검증

                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보(body에 있는) 가져오기
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회("sub"부분에 있는 username을 가지고옴)
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            UserRoleEnum role = user.getRole();
            System.out.println("role = " + role);

            System.out.println("claims.getSubject() : " + claims.getSubject());


            Comment comment = checkComment(comment_id);
            if (comment.getUser().getUsername().equals(user.getUsername()) || role == UserRoleEnum.ADMIN) {
                comment.update(requestDto);
            }

//                Board board = checkBoard(comment.getBoard().getId());
            return new CommentResponseDto(comment);

        } else {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

    }

    //**댓글 삭제**
    @Transactional
    public ResponseDto deleteComment(Long comment_id, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;


        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            UserRoleEnum role = user.getRole();

            Comment comment = checkComment(comment_id);
            if (comment.getUser().getUsername().equals(claims.getSubject()) || role == UserRoleEnum.ADMIN) {
                commentRepository.deleteById(comment_id);
            }
            return new ResponseDto("댓글 삭제 성공", HttpStatus.OK.value());

        } else {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }


    }

    // **선택한 댓글 존재 확인&담기**
    private Comment checkComment(Long comment_id) {
        Comment comment = commentRepository.findById(comment_id).orElseThrow(
                () -> new RuntimeException("댓글이 존재하지 않습니다"));
        return comment;
    }
}

