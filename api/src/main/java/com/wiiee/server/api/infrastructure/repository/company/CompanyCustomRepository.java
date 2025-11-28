package com.wiiee.server.api.infrastructure.repository.company;

import com.wiiee.server.api.application.company.CompanyGetRequestDTO;
import com.wiiee.server.common.domain.company.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyCustomRepository {

    Page<Company> findAllByCompanyGetRequestDTO(CompanyGetRequestDTO dto, Pageable pageable);

}
