package com.wiiee.server.api.application.company;

import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.company.CompanyService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Content api")

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/company")
@RestController
public class CompanyRestController {

    private final CompanyService companyService;

    @Operation(summary = "[테스트용] 업체 생성")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<CompanyModel> postCompany(@Validated @RequestBody CompanyPostRequestDTO dto) {
        return ApiResponse.success(companyService.createNewCompany(dto));
    }

    @Operation(summary = "업체 리스트 조회", security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleCompanyModel> getCompanies(@Validated @ModelAttribute CompanyGetRequestDTO dto) {
        return ApiResponse.success(companyService.getCompanies(dto));
    }

    @Operation(summary = "업체 조회", security = { @SecurityRequirement(name = "Authorization") })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<CompanyModel> getCompany(@Parameter(hidden = true) @AuthUser User user,
                                                         @PathVariable("id") Long id) {
        return ApiResponse.success(companyService.getCompany(id));
    }
}