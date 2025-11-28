package com.wiiee.server.api.domain.company;

import com.wiiee.server.api.application.company.*;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.company.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final UserService userService;
    private final ImageService imageService;

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyModel createNewCompany(CompanyPostRequestDTO dto) {
        final var companyToAdd = userService.findAdminById(dto.getAdminId()).addCompany(
                dto.toCompanyBasicInfo(),
                dto.toCompanyBusinessInfo());

        Company companyAdded = companyRepository.save(companyToAdd);

        return CompanyModel.fromCompanyAndImages(companyAdded, imageService.findByIdsIn(companyAdded.getBasicInfo().getImageIds()));
    }

    @Transactional(readOnly = true)
    public MultipleCompanyModel getCompanies(CompanyGetRequestDTO dto) {
        final var list = companyRepository.findAllByCompanyGetRequestDTO(dto, PageRequest.of(dto.getPage() - 1, dto.getSize()));
        return MultipleCompanyModel.fromCompaniesAndHasNext(getCompanySimpleModelsByCompanies(list.getContent()), list.hasNext());
    }

    @Transactional(readOnly = true)
    public CompanyModel getCompany(Long id) {
        final var findCompany = companyRepository.findById(id).orElseThrow();
        return CompanyModel.fromCompanyAndImages(findCompany, imageService.findByIdsIn(findCompany.getBasicInfo().getImageIds()));
    }

    @Transactional(readOnly = true)
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    /**
     * 변환
     **/
    @Transactional(readOnly = true)
    public List<CompanySimpleModel> getCompanySimpleModelsByCompanies(List<Company> companies) {
        return companies.stream().map(company ->
                CompanySimpleModel.fromCompanyAndImage(company,
                        imageService.getImageById(company.getBasicInfo().getImageIds().stream().findFirst().orElse(0L)))).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanySimpleModel getCompanySimpleModelByCompany(Company company) {
        return CompanySimpleModel.fromCompanyAndImage(company, imageService.getImageById(company.getBasicInfo().getImageIds().stream().findFirst().orElse(0L)));
    }
}