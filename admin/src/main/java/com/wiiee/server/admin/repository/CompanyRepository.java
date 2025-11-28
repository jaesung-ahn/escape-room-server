package com.wiiee.server.admin.repository;

import com.wiiee.server.common.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
