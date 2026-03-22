package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.PmProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<PmProject, Long> {
    @Query("SELECT p FROM PmProject p ORDER BY p.sort ASC")
    List<PmProject> findAllByOrderBySortAsc();
    
    List<PmProject> findByTitleContainingOrDescriptionContainingOrderBySortAsc(String title, String description);
}