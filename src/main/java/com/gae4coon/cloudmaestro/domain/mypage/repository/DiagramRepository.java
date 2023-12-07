package com.gae4coon.cloudmaestro.domain.mypage.repository;

import com.gae4coon.cloudmaestro.domain.mypage.entity.Diagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiagramRepository extends JpaRepository<Diagram, Long> {
    String findDiagramFileByDiagramId(Long diagramId);
    Diagram findByDiagramId(Long diagramId);

    @Query("SELECT n FROM Diagram n WHERE n.userId.userId = :userId")
    List<Diagram> findByUserId(@Param("userId") String userId);

    Diagram findByDiagramFile(String fileName);
}
