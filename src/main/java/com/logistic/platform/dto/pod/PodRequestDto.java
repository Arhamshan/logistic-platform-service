package com.logistic.platform.dto.pod;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PodRequestDto {

    @JsonProperty("receivedBy")
    private String receivedBy;

    @JsonProperty("receivedAt")
    private String receivedAt;

    @JsonProperty("receiverContact")
    private String receiverContact;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("podImage")
    private String podImage;
}