package com.cuong.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cuong.jobhunter.domain.Company;
import com.cuong.jobhunter.dto.Meta;
import com.cuong.jobhunter.dto.ResultPaginationDTO;
import com.cuong.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(Company company) {
        Company newCompany = new Company();
        newCompany.setName(company.getName());
        newCompany.setAddress(company.getAddress());
        newCompany.setLogo(company.getLogo());
        newCompany.setDescription(company.getDescription());
        this.companyRepository.save(newCompany);
        return newCompany;
    }

    public ResultPaginationDTO getAllCompany(Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta mt = new Meta();
        mt.setPage(pageCompany.getNumber());
        mt.setPageSize(pageCompany.getSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        resultPaginationDTO.setMeta(mt);
        resultPaginationDTO.setResult(pageCompany.getContent());
        return resultPaginationDTO;
    }

    public Company getCompany(long id) {
        Optional<Company> companyGetted = this.companyRepository.findById(id);
        if (companyGetted.isPresent()) {
            Company companyGet = companyGetted.get();
            return companyGet;
        } else {
            return null;
        }
    }

    public void deleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }

    public Company updateCompany(Company company) {
        Company updatedCompany = new Company();
        updatedCompany.setName(company.getName());
        updatedCompany.setDescription(company.getDescription());
        updatedCompany.setAddress(company.getAddress());
        updatedCompany.setUpdatedAt(company.getUpdatedAt());
        updatedCompany.setCreatedAt(company.getCreatedAt());
        updatedCompany.setCreatedBy(company.getCreatedBy());
        updatedCompany.setUpdatedBy(company.getUpdatedBy());
        return updatedCompany;
    }
}
