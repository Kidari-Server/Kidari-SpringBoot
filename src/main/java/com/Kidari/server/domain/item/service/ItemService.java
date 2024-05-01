package com.Kidari.server.domain.item.service;

import com.Kidari.server.common.response.ApiResponse;
import com.Kidari.server.common.response.exception.ErrorCode;
import com.Kidari.server.common.response.exception.ItemException;
import com.Kidari.server.common.validation.ValidationService;
import com.Kidari.server.config.auth.AuthUtils;
import com.Kidari.server.domain.item.dto.ItemDto;
import com.Kidari.server.domain.item.entity.Item;
import com.Kidari.server.domain.item.entity.ItemRepository;
import com.Kidari.server.domain.member.entity.Member;
import com.Kidari.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final AuthUtils authUtils;
    private final MemberService memberService;
    private final ItemRepository itemRepository;

    // 현재 장착중인 아이템 확인
    public ApiResponse<?> getCurrentItem(){
        Member member = authUtils.getMember();
        return ApiResponse.success(new ItemDto(member.getItem()));
    }

    // 아이템 변경
    public ApiResponse<?> patchNewItem(ItemDto reqItemDto) {
        try {
            Member member = authUtils.getMember();
            Item updatedItem = ItemDto.toEntity(reqItemDto, member.getItem());
            itemRepository.save(updatedItem); // 아이템 변경사항 저장
            return ApiResponse.success(updatedItem);
        } catch (ItemException e) {
            return ApiResponse.failure(e.getErrorCode()); //해당 아이디의 아이템 존재 x
        }
    }
}
