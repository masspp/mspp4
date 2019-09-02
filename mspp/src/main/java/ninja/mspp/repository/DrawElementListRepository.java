package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import ninja.mspp.model.entity.DrawElementList;

public interface DrawElementListRepository extends JpaRepository< DrawElementList, Long >,
													 QuerydslPredicateExecutor< DrawElementList >{

}
