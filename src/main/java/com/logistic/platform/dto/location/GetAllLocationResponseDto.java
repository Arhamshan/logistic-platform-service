package com.logistic.platform.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllLocationResponseDto {
    private Long id;

    private String name;

    private String locationCode;

    private String country;

    private String city;

    private String type;

    private String latitude;

    private String longitude;

}
