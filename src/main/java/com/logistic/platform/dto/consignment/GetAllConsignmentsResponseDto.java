package com.logistic.platform.dto.consignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllConsignmentsResponseDto {

    private Integer totalConsignments;

    private List<GetAllConsignmentsDto> consignments;

}
