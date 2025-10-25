package com.TP1.API.v1.modules.task.repository;

import com.TP1.API.v1.modules.task.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("SELECT t FROM Task t WHERE t.title LIKE %:content% OR t.description LIKE %:content%")
    Page<Task> findAllByTitleContainingOrDescriptionContaining(Pageable pageable, String content);
}
