package com.Kidari.server.common.validation;

import com.Kidari.server.common.response.exception.ErrorCode;
import com.Kidari.server.common.response.exception.ItemException;
import com.Kidari.server.common.response.exception.MemberException;
import com.Kidari.server.common.response.exception.UnivException;
import com.Kidari.server.domain.item.entity.Item;
import com.Kidari.server.domain.item.entity.ItemRepository;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.entity.MemberRepository;
import com.Kidari.server.domain.univ.entity.Univ;
import com.Kidari.server.domain.univ.entity.UnivRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ValidationService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final UnivRepository univRepository;

    public Member valMember(String login) {
        return memberRepository.findByLogin(login)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member valMember(UUID uid) {
        return memberRepository.findByUid(uid)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
    /*
    public List<Member> valMemberList(String nickname) {
        List<Member> memberList = memberRepository.findAllByNickname(nickname);
        if (memberList == null || memberList.isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return memberList;
    }*/

    public Item valItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));
    }

    public Univ valUniv(Long univId) {
        return univRepository.findById(univId)
                .orElseThrow(() -> new UnivException(ErrorCode.UNIV_NOT_FOUND));
    }

    public Univ valUniv(String univName) { // 예외를 발생시키지 않음에 유의
        return univRepository.findByUnivName(univName)
                .orElse(null);
    }

}
