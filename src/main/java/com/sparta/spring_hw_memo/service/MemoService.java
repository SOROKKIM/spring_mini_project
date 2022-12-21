package com.sparta.spring_hw_memo.service;

import com.sparta.spring_hw_memo.dto.MemoRequestDto;
import com.sparta.spring_hw_memo.dto.MemoResponseDto;
import com.sparta.spring_hw_memo.entity.Memo;
import com.sparta.spring_hw_memo.entity.User;
import com.sparta.spring_hw_memo.repository.MemoRepository;
import com.sparta.spring_hw_memo.repository.UserRepository;
import com.sparta.spring_hw_memo.entity.UserRoleEnum;
import com.sparta.spring_hw_memo.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        for (int i = 0 ; i < memoList.size() ; i++) {
            MemoResponseDto memoResponseDto = new MemoResponseDto(memoList.get(i));
            memoListResponseDto.add(memoResponseDto);
        }

        return memoListResponseDto;

    }


    //메모 상세 조회
    @Transactional
    public Optional<Memo> getMemo(Long id) {

        return memoRepository.findById(id);
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

            Memo memo = memoRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                    () -> new NullPointerException("해당 메모는 존재하지 않습니다.")
            );

            memo.update(requestDto);

            return memo.getId();

        } else {
            return null;
        }
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
}
