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

@RequiredArgsConstructor
@Service
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
            Memo memo = memoRepository.saveAndFlush(new Memo(requestDto, user.getId()));

            return new MemoResponseDto (memo);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<MemoResponseDto> getMemos(HttpServletRequest request) throws IllegalArgumentException {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 메모 조회 가능
        if(token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else throw new IllegalArgumentException("Token Error");

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            // 사용자 권한 가져와서 ADMIN 이면 전체 조회, USER 면 본인이 추가한 부분 조회
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);

            List<MemoResponseDto> list = new ArrayList<>();
            List<Memo> memoList;

            if (userRoleEnum == UserRoleEnum.USER) {
                // 사용자 권한이 USER일 경우
                memoList = memoRepository.findAllByUserId(user.getId());
            } else {
                memoList = memoRepository.findAll();
            }

            for (Memo memo : memoList) {
                list.add(new MemoResponseDto(memo));
            }

            return list;

        }else {
            return null;
        }
    }

    @Transactional
    public Long update(Long id, MemoRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

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

    @Transactional
    public Long deleteMemo(Long id, MemoRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 메모 삭제 가능
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

            memoRepository.deleteById(id);

            return id;

        } else {
            return null;
        }
    }
}
