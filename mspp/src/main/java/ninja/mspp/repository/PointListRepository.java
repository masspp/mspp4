package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import ninja.mspp.model.entity.PointList;

@Repository
public interface PointListRepository  extends JpaRepository< PointList, Long >,
												QuerydslPredicateExecutor< PointList >{

}
