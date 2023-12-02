package com.gae4coon.cloudmaestro.domain.resourceGuide.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "resource")
@Getter
@Setter
public class ResourceEntity {
    @Id
    @Column( name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "imgpath", length = 200)
    private String imgPath;

    @Column(name = "tag", columnDefinition = "json")
    private String tag;

    @Column(name = "guide1")
    private String guide1;

    @Column(name = "guide2")
    private String guide2;
    @Column(name = "guide3")
    private String guide3;

    @Column(name = "guide4")
    private String guide4;
}