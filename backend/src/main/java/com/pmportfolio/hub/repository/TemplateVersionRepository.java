package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.TemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {

    @Query("SELECT v FROM TemplateVersion v WHERE v.template.id = :templateId ORDER BY v.createdAt DESC")
    List<TemplateVersion> findByTemplateIdOrderByCreatedAtDesc(@Param("templateId") Long templateId);

    void deleteByTemplateId(Long templateId);
}
