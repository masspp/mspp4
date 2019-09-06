package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import ninja.mspp.model.entity.PeakAnnotation;

@Repository
public interface PeakAnnotationRepository extends JpaRepository< PeakAnnotation, Long >,
													QuerydslPredicateExecutor< PeakAnnotation >{

}
