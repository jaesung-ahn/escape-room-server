package com.wiiee.server.api.domain.company;

import com.wiiee.server.api.application.company.CompanyGetRequestDTO;
import com.wiiee.server.api.infrastructure.repository.company.CompanyCustomRepository;
import com.wiiee.server.common.domain.company.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyCustomRepository {

    @Override
    Page<Company> findAllByCompanyGetRequestDTO(CompanyGetRequestDTO dto, Pageable pageable);

}
