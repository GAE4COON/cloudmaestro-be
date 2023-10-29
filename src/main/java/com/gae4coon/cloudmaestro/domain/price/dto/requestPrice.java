package com.gae4coon.cloudmaestro.domain.price.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class requestPrice {
    private String Platform;
    private String Instance;
    private String LifeCycle;

}