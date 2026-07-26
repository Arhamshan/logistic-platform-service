package com.logistic.platform.dto.location;

import com.logistic.platform.dto.consignment.GetAllConsignmentsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllLocationsResponseDto {

    private Integer totalLocations;

    private List<GetAllLocationDto> locations;

}
