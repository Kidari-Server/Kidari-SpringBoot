package com.Kidari.server.config.auth;

import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.entity.MemberRepository;
import com.Kidari.server.domain.member.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthUtils {

    // 현재 요청된 사용자 정보가 필요한 경우, AuthUtils.clss의 함수를 호출하면 됨

    private final MemberRepository memberRepository;

    public Member getMember() {
        log.info("AuthUtils - getMember 함수 진입");
        log.info("깃허브 계정 식별용 고유 sub -> {}", getCurrentMemberGitHubSub());
        return memberRepository.findBySub(getCurrentMemberGitHubSub()).get();
    }

    public Authentication getAuthentication() {
        log.info("AuthUtils - getAuthentication 함수 진입");
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    public Object getPrincipal() {
        log.info("AuthUtils - getPrincipal 함수 진입");
        // 현재 사용자의 principal 가져오기
        return getAuthentication().getPrincipal();
    }

    public String getCurrentMemberGitHubSub() {
        log.info("AuthUtils - getCurrentMemberGitHubSub 함수 진입");
        Object principalObject = getPrincipal();

        // principal이 UserDetails 인스턴스인지 확인
        if (principalObject instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principalObject;

            // UserDetails 인스턴스에서 사용자 식별값(sub) 획득
            // 사용자 식별값(sub)은 UserDetails의 username에 저장되어 있음
            return userDetails.getUsername();
        }
        return null;
    }

    public String getCurrentMemberGitHubLogin() {
        log.info("AuthUtils - getCurrentMemberGitHubLogin 함수 진입");
        Object principalObject = getPrincipal();

        log.info("principal이 UserDetails 인스턴스인지 확인");
        // principal이 OAuth2User 인스턴스인지 확인
        if (principalObject instanceof UserDetails) {
            log.info("성공");
            UserDetails userDetails = (UserDetails) principalObject;

            // 사용자 닉네임 획득
            // UserDetails 인스턴스에서 사용자 닉네임(login) 획득
            // 사용자 닉네임(login)은 UserDetails의 password에 저장되어 있음
            return userDetails.getPassword();
        }
        return null;
    }

    public Role getCurrentUserRole() {
        log.info("AuthUtils - getCurrentUserRole 함수 진입");
        Object principalObject = getPrincipal();

        log.info("principal이 UserDetails 인스턴스인지 확인");
        // principal이 UserDetails 인스턴스인지 확인
        if (principalObject instanceof UserDetails) {
            log.info("성공");
            UserDetails userDetails = (UserDetails) principalObject;

            // UserDetails에서 권한 목록 가져오기
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            GrantedAuthority firstAuthority = authorities.iterator().next();
            String authorityString = firstAuthority.getAuthority();

            // UserDetails 인스턴스에서 Role String 획득
            return Role.valueOf(authorityString);
        }

        return null;
    }
}
