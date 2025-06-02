package com.cuong.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cuong.jobhunter.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long>{
    Company save(Company company);
}
