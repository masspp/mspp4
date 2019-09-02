package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import ninja.mspp.model.entity.Spectrum;

public interface SpectrumRepository extends JpaRepository< Spectrum, Long >,
											  QuerydslPredicateExecutor< Spectrum >{

}
