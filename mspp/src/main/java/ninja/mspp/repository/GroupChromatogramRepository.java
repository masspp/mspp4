package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import ninja.mspp.model.entity.GroupChromatogram;

@Repository
public interface GroupChromatogramRepository extends JpaRepository< GroupChromatogram, Long >,
													   QuerydslPredicateExecutor< GroupChromatogram > {

}
