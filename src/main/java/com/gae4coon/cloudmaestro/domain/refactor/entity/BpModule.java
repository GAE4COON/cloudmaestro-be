package com.gae4coon.cloudmaestro.domain.refactor.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BP_module")
@Data
public class BpModule {

    @Id
    @Column( name = "id", length = 30)
    private String id;

    @Lob
    @Column(columnDefinition = "json", name = "json_data")
    private String jsonData;
}
