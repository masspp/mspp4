package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import ninja.mspp.model.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository< Project, Long >,
											QuerydslPredicateExecutor< Project >{

}
