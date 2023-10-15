package com.gae4coon.cloudmaestro.domain.price.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "rds")
public class dbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Name", length = 45)
    private String name;

    @Column(name = "ApiName", length = 45)
    private String apiName;

    @Column(name = "Memory", length = 45)
    private String memory;

    @Column(name = "Storage", length = 45)
    private String storage;

    @Column(name = "vCPUs", length = 45)
    private String vCPU;

    @Column(name = "Network", length = 45)
    private String networkPerformance;

    @Column(name = "PostgreSQL", length = 45)
    private String PostgreSQL;

    @Column(name = "MySQL", length = 45)
    private String MySQL;

    @Column(name = "SQLServerStandard", length = 45)
    private String SQLServer;

    @Column(name = "AuroraPostgresMySQL", length = 45)
    private String AuroraPostgresMySQL;

    @Column(name = "MariaDB", length = 45)
    private String MariaDB;

    @Column(name = "OracleEnterprise", length = 45)
    private String Oracle;
}