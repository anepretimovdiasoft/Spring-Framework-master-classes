package ru.examle.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.examle.edu.entity.Department;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
}
