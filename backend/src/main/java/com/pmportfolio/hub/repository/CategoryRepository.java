package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.SysCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<SysCategory, Long> {
}
