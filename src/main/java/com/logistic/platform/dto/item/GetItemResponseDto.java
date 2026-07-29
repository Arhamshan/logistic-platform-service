package com.logistic.platform.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetItemResponseDto {

    private Integer totalItems;

    private List<GetItemDto> items;

}
