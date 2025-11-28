package com.wiiee.server.admin.service;

import com.wiiee.server.admin.repository.CompanyRepository;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.company.Bank;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.company.CompanyBusinessInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

//    @Autowired
//    public CompanyService(CompanyRepository companyRepository) {
//        this.companyRepository = companyRepository;
//    }

    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    @Transactional
    public Optional<Company> saveTestCompany(AdminUser adminUser) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        List<Long> list2 = new ArrayList<>();
        list.add(1123);

        CompanyBasicInfo basicInfo = new CompanyBasicInfo(
                "test com name",
                State.valueOf(State.SEOUL.getCode()),
                City.valueOf(City.GANGDONGGU.getCode()),
                "경기도 구리시",
                "인창동",
                "공지사항은 다음내용 1121",
                "021-263-1728",
                "https://naver.com",
                true,
                list,
                true,
                list2
        );
        CompanyBusinessInfo businessInfo = new CompanyBusinessInfo(
                1124L,
                "02-197-23876",
                "김수관",
                "02-1978-1274",
                "02-1978-1274",
                Bank.valueOf(Bank.KBANK.getCode()),
                "072-8346-187263"
        );
        Optional<Company> company = Optional.of(
                companyRepository.save(new Company(adminUser, basicInfo, businessInfo))
        );
//        Optional<Company> company = Optional.of(companyRepository.save(Company.of(
//                ,
//                "https://pds.skyedaily.com/top_image/201708/64397_p.jpg",
//
//                "https://beautifulfund.org/bf/files/common/img/business_license.jpg",
//                "경기도 구리시",
//                "인창동 487-1",
//                City.GANGDONGGU,
//                true,
//                true,
//                true,
//                true,
//                true,
//                true,
//                true,
//                false,
//                "02-1978-1273",
//                "https://naver.com",
//                "공지사항은 다음내용 1121",
//                "김수관",
//                "02-1978-1274",
//                "02-1978-1275",
//                Bank.IBKBANK,
//                "072-8346-187263",
//                true
//        )));

        return company;
    }

    @Transactional
    public Optional<Company> updateCompany(Optional<Company> company) {
        companyRepository.save(company.get());
        return company;
    }

    @Transactional
    public Company saveCompany(Company company) {
        companyRepository.save(company);
        return company;
    }
}
