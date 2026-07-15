package com.logistic.platform.dto.pod;

import com.logistic.common.entity.Pod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PodResponseDto {

    private Long id;
    private String receivedBy;
    private String receiverContact;
    private String podPath;
    private String deliveredBy;

    public PodResponseDto(Pod pod) {
        this.id = pod.getId();
        this.receivedBy      = pod.getReceivedBy();
        this.receiverContact = pod.getReceiverContact();
        this.podPath         = pod.getPodPath();
        this.deliveredBy     = pod.getDeliveredBy();
    }
}