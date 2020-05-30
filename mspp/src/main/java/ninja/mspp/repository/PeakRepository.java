/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import ninja.mspp.model.entity.Peak;


/**
 *
 * @author masaki
 */
@Repository
public interface PeakRepository extends JpaRepository<Peak, Integer>,
    QuerydslPredicateExecutor<Peak>{
}
