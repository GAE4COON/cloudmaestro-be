package com.gae4coon.cloudmaestro.domain.refactor.repository;

import com.gae4coon.cloudmaestro.domain.refactor.entity.BpModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<BpModule, String> {
    BpModule findBpModuleById(String id);
}