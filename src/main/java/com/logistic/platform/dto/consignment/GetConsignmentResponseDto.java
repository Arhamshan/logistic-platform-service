package com.logistic.platform.dto.consignment;

import com.logistic.platform.dto.item.ConsignmentItemResponseDto;
import com.logistic.platform.vo.ConsignmentVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class GetConsignmentResponseDto {

    private String consignmentId;

    private String status;

    private List<ConsignmentItemResponseDto> items;

    public GetConsignmentResponseDto(ConsignmentVo vo) {
        this.consignmentId = vo.getConsignmentId();
        this.status = vo.getStatus();
        this.items = vo.getItems() != null
                ? vo.getItems().stream()
                .map(item -> new ConsignmentItemResponseDto(
                        item.getItemId(),
                        item.getStatus(),
                        item.getCurrentLocationCode()))
                .collect(Collectors.toList())
                : null;
    }
}