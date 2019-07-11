package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import ninja.mspp.model.entity.Chromatogram;

public interface ChromatogramRepository extends JpaRepository< Chromatogram, Integer >,
												  QuerydslPredicateExecutor< Chromatogram >{

}
