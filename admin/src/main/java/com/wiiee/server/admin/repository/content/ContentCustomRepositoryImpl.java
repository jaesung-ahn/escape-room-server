package com.wiiee.server.admin.repository.content;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.admin.form.content.ContentForm;
import com.wiiee.server.common.domain.content.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.wiiee.server.common.domain.content.QContent.content;

@RequiredArgsConstructor
@Repository
public class ContentCustomRepositoryImpl implements ContentCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Content> findAllByContentGetListForm(Pageable pageable, ContentForm contentForm) {

        final var resultList = jpaQueryFactory.select(content)
                .from(content)
                .where(eqCompanyId(contentForm.getCompanyId()))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(resultList.getResults(), pageable, resultList.getTotal());
    }

    private BooleanExpression eqCompanyId(Long companyId) {
        if (companyId == null || companyId == 0){
            return null;
        }
        return content.company.id.eq(companyId);
    }
}
