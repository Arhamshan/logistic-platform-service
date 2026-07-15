package com.logistic.platform.dto.consignment;

import com.logistic.platform.vo.ConsignmentVo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateConsignmentResponseDto {

    private String consignmentId;

    private String status;

    public UpdateConsignmentResponseDto(ConsignmentVo vo) {
        this.consignmentId = vo.getConsignmentId();
        this.status = vo.getStatus();
    }
}
