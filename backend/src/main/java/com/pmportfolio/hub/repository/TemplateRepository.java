package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.Template;
import com.pmportfolio.hub.model.TemplateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long>, JpaSpecificationExecutor<Template> {

    @Query("SELECT t FROM Template t JOIN FETCH t.categories c WHERE c.id = :categoryId")
    List<Template> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT t FROM Template t LEFT JOIN FETCH t.categories WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Template> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT t FROM Template t JOIN FETCH t.categories c WHERE c.id = :categoryId AND (t.name LIKE %:keyword% OR t.description LIKE %:keyword%)")
    List<Template> findByCategoryIdAndKeyword(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Query("SELECT t FROM Template t JOIN FETCH t.categories c WHERE c.id = :categoryId AND (t.name LIKE %:keyword% OR t.description LIKE %:keyword%)")
    List<Template> findByCategoryAndKeyword(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Query("SELECT COUNT(t) FROM Template t JOIN t.categories c WHERE c.id = :categoryId AND (t.name LIKE %:keyword% OR t.description LIKE %:keyword%)")
    long countByCategoryAndKeyword(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Query("SELECT t FROM Template t LEFT JOIN FETCH t.categories")
    List<Template> findAllWithCategories();
}
