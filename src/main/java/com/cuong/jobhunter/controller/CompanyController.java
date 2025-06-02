package com.cuong.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cuong.jobhunter.domain.Company;
import com.cuong.jobhunter.dto.ResultPaginationDTO;
import com.cuong.jobhunter.service.CompanyService;

import jakarta.validation.Valid;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company newCompany = this.companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(@RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : null;
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : null;
        Pageable pageable = PageRequest.of(Integer.parseInt(sCurrent) - 1, Integer.parseInt(sPageSize));// phải connvert
                                                                                                        // sang int vì 2
                                                                                                        // thamm số này
                                                                                                        // đang là
                                                                                                        // string
        // return ResponseEntity.ok().body(companies);
        return ResponseEntity.ok().body(this.companyService.getAllCompany(pageable));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanywithId(@PathVariable long id) {
        Company company = this.companyService.getCompany(id);
        return ResponseEntity.ok().body(company);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable long id, @RequestBody Company company) {
        Company updatecompany = this.companyService.updateCompany(company);
        return ResponseEntity.ok().body(updatecompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().body("Xoa thanh cong!!");
    }
}
