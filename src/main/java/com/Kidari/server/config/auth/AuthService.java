package com.Kidari.server.config.auth;

import com.Kidari.server.common.response.exception.ErrorCode;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.config.jwt.JwtUtils;
import com.Kidari.server.domain.commit.service.CommitService;
import com.Kidari.server.domain.item.entity.Item;
import com.Kidari.server.domain.item.entity.ItemRepository;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.entity.MemberDto;
import com.Kidari.server.domain.member.entity.MemberRepository;
import com.Kidari.server.domain.member.entity.Role;
import com.Kidari.server.domain.univ.entity.Univ;
import com.Kidari.server.domain.univ.entity.UnivRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtUtils jwtUtils;
    private final HttpServletResponse response;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final UnivRepository univRepository;
    private final CommitService commitService;

    public HttpServletResponse setAuth() {
        
        // SecurityContext에서 가져온 값으로 MemberDto 객체 생성
        MemberDto member = MemberDto.builder()
                .sub(getGitHubSub())
                .login(getGitHubLogin())
                .role(Role.ROLE_MEMBER)
                .build();

        // 최초 로그인 판별
        Optional<Member> optionalMember = memberRepository.findBySub(member.getSub());

        if (optionalMember.isEmpty()) {
            // 신규 가입 유저
            // 회원가입
            optionalMember = Optional.ofNullable(signUp(member).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)));
        }

        String accessToken = jwtUtils.createAccessToken(member);
        String refreshToken = jwtUtils.createRefreshToken(member);

        ResponseCookie accessTokenCookie = jwtUtils.createResponseCookie("accessToken", accessToken);
        ResponseCookie refreshTokenCookie = jwtUtils.createResponseCookie("refreshToken", refreshToken);

        response.setHeader("Set-Cookie", accessTokenCookie.toString());
        response.setHeader("Set-Cookie", refreshTokenCookie.toString());

        return response;
    }

    private Optional<Member> signUp(MemberDto member) {
        log.info("회원가입 함수 진입");
        log.info("memberDto로 넘어온 값 -> {}", member.getSub());
        
        // item 객체 생성 및 저장
        Item item = Item.builder().build(); // Default 생성 걸어두었음
        itemRepository.save(item);
        log.info("item 객체 생성 및 저장 완료");
        log.info("Item: id-" + item.getId() + ", snowId-" + item.getSnowId() + ", hatId-" + item.getHatId()
                + ", decoId-" + item.getDecoId());
        
        // univ 객체 생성 및 저장
        Optional<Univ> defaultUnivOptional = univRepository.findByUnivName("none"); // 컬럼에 unique 걸려있음
        Univ defaultUniv = defaultUnivOptional.orElseGet(() -> {
            Univ newUniv = Univ.builder()
                    .univName("none")
                    .belonged(0L)
                    .totalHeight(0L)
                    .build();
            log.info("default에 해당하는 none 대학이 없어서 생성했음 -> {}", newUniv.getUnivName());
            return newUniv;
        });
        univRepository.save(defaultUniv);
        log.info("univ 객체 생성 및 저장 완료");
        log.info("Univ: id-" + defaultUniv.getId() + ", name-" + defaultUniv.getUnivName()
                + ", belonged-" + defaultUniv.getBelonged() + ", totalHeight-" + defaultUniv.getTotalHeight());


        // member 객체 생성 및 저장
        Member newMember = Member.builder()
                .sub(member.getSub())
                .login(member.getLogin())
                .role(Role.ROLE_MEMBER)
                .univ(defaultUniv)
                .item(item)
                .build();
        memberRepository.save(newMember);
        log.info("Member 객체 생성 및 저장 완료");
        log.info("Member: uid-" + newMember.getUid() + ", gitHubId-" + newMember.getSub()
                + ", login-" + newMember.getLogin() + ", snowflake-" + newMember.getSnowflake()
                + ", snowmanHeight-" + newMember.getSnowmanHeight() + ", attacking-" + newMember.getAttacking()
                + ", damage-" + newMember.getDamage() + ", role-" + newMember.getRole()
                + ", newAlarm-" + newMember.getNewAlarm() + ", univ-" + newMember.getUniv()
                + ", item-" + newMember.getItem());

        // snowflake 수 업데이트
        commitService.setSnowflake(member);

        return memberRepository.findById(newMember.getUid());
    }

    public String getGitHubSub() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            return (String) oauth2User.getAttribute("sub");
        }
        return null;
    }

    public String getGitHubLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            return (String) oauth2User.getAttribute("login");
        }
        return null;
    }
}
