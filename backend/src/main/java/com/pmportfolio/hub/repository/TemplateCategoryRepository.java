package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.TemplateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TemplateCategoryRepository extends JpaRepository<TemplateCategory, Long> {

    @Query("SELECT c FROM TemplateCategory c LEFT JOIN c.templates t GROUP BY c.id ORDER BY COUNT(t) DESC")
    java.util.List<TemplateCategory> findAllWithTemplateCount();
}
