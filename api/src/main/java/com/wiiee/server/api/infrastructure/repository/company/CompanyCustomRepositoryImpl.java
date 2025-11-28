package com.wiiee.server.api.infrastructure.repository.company;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.company.CompanyGetRequestDTO;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.company.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static com.wiiee.server.common.domain.company.QCompany.company;


@RequiredArgsConstructor
@Repository
public class CompanyCustomRepositoryImpl implements CompanyCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Company> findAllByCompanyGetRequestDTO(CompanyGetRequestDTO dto, Pageable pageable) {
        final var list = jpaQueryFactory.selectFrom(company)
                .where(
                        nameContains(dto.getName()),
                        stateEq(dto.getStateCode()),
                        cityEq(dto.getCityCode())
                )
                .orderBy(company.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(list.getResults(), pageable, list.getTotal());
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? company.basicInfo.name.contains(name) : null;
    }

    private BooleanExpression stateEq(Integer stateCode) {
        return stateCode != null ? company.basicInfo.state.eq(State.valueOf(stateCode)) : null;
    }

    private BooleanExpression cityEq(Integer cityCode) {
        return cityCode != null ? company.basicInfo.city.eq(City.valueOf(cityCode)) : null;
    }

}