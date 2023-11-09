package com.gae4coon.cloudmaestro.domain.diagram.repository;

import com.gae4coon.cloudmaestro.domain.diagram.entity.Diagram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagramRepository extends JpaRepository<Diagram, Long> {
}
