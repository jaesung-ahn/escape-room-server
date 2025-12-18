package com.wiiee.server.api.domain.content;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.content.*;
import com.wiiee.server.api.domain.company.CompanyService;
import com.wiiee.server.api.domain.content.review.ReviewRepository;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.recommendation.WbtiRecommendationService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.RankContent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContentService {

    private final WbtiRecommendationService wbtiRecommendationService;
    private final UserService userService;
    private final CompanyService companyService;
    private final ImageService imageService;
    private final ContentRepository contentRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ContentModel createNewContent(ContentPostRequestDTO dto) {
        final var contentBasicInfo = dto.toContentBasicInfo();

        final var contentAdded = companyService.findById(dto.getCompanyId())
                .map(company -> company.addContent(contentBasicInfo))
                .map(contentRepository::save)
                .orElseThrow(NoSuchElementException::new);

        dto.getPriceList().forEach(req -> contentAdded.addPrice(req.getNumber(), req.getValue()));

        return ContentModel.fromContentAndImagesWithCompanySimpleModel(contentAdded,
                imageService.findByIdsIn(contentAdded.getContentBasicInfo().getImageIds()),
                companyService.getCompanySimpleModelByCompany(contentAdded.getCompany()),
                reviewRepository.findReviewAvgAndCountByContent(contentAdded.getId()));
    }

    /**
     * 놀거리 리스트 조회
     */
    @Transactional(readOnly = true)
    public MultipleContentModel getContentsByContentGetRequestDTO(ContentGetRequestDTO dto) {
        final var contentPage = contentRepository.findAllByContentGetRequestDTO(dto, PageRequest.of(dto.getPage() - 1, dto.getSize()));
        return MultipleContentModel.fromContentsAndHasNext(this.getContentSimpleModelsByContents(contentPage.toList()), contentPage);
    }

    /**
     * 놀거리 상세 조회
     */
    @Transactional(readOnly = true)
    public ContentModel getContent(Long id) {
        final var findContent = contentRepository.findById(id).orElseThrow();

        return ContentModel.fromContentAndImagesWithCompanySimpleModel(findContent,
                imageService.findByIdsIn(findContent.getContentBasicInfo().getImageIds()),
                companyService.getCompanySimpleModelByCompany(findContent.getCompany()),
                reviewRepository.findReviewAvgAndCountByContent(findContent.getId()));
    }

    @Transactional(readOnly = true)
    public MultipleContentModel getRecommendContents(Long userId) {
        final var findUser = userService.findById(userId);
        PageRequestDTO pageRequestDTO = new PageRequestDTO();

        if (findUser.getProfile().getWbti() != null) {
            final var list = wbtiRecommendationService.getWbtiRecommendationContents(findUser.getProfile().getWbti().getId());
            Page<Content> contentPage = new PageImpl<>(list, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize()), list.size());
            return MultipleContentModel.fromContentsAndHasNext(this.getContentSimpleModelsByContents(list), contentPage);
        }

        final var list =  new ArrayList<>(contentRepository.findAllByContentGetRequestDTO(ContentGetRequestDTO.builder().build(), PageRequest.of(0, 10)).getContent());
        Collections.shuffle(list);
        Page<Content> contentPage = new PageImpl<>(list, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize()), list.size());
        return MultipleContentModel.fromContentsAndHasNext(this.getContentSimpleModelsByContents(list), contentPage);
    }

    @Transactional(readOnly = true)
    public MultipleContentModel getWbtiRecommendationContentsByUserId(Long userId) {
        final var list = wbtiRecommendationService.getWbtiRecommendationContentsByUserId(userId);
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        Page<Content> contentPage = new PageImpl<>(list, PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize()), list.size());
        return MultipleContentModel.fromContentsAndHasNext(this.getContentSimpleModelsByContents(list), contentPage);
    }

    @Transactional(readOnly = true)
    public Optional<Content> findById(Long id) {
        return contentRepository.findById(id);
    }

    /**
     *  놀거리 기본 모델 리스트 생성
     **/
    @Transactional(readOnly = true)
    public List<ContentSimpleModel> getContentSimpleModelsByContents(List<Content> contents) {
        //TODO: 기본이미지 등록
        return contents.stream().map(content ->
                ContentSimpleModel.fromContentAndImage(content,
                        imageService.getImageById(content.getContentBasicInfo().getRepresentativeImageId())
                        )
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContentModel getContentModelByContent(Content content) {
        return ContentModel.fromContentAndImagesWithCompanySimpleModel(content,
                imageService.findByIdsIn(Optional.ofNullable(content.getContentBasicInfo().getImageIds()).orElse(List.of())),
                companyService.getCompanySimpleModelByCompany(content.getCompany()),
                reviewRepository.findReviewAvgAndCountByContent(content.getId()));
    }

    @Transactional(readOnly = true)
    public ContentSimpleModel getContentSimpleModelByContent(Content content) {
        return ContentSimpleModel.fromContentAndImage(content,
                imageService.getImageById(content.getContentBasicInfo().getRepresentativeImageId())
                );
    }

    @Transactional(readOnly = true)
    public List<ContentSimpleModel> getMainHotContentList() {
        List<RankContent> allMainHotContent = contentRepository.findAllMainHotContent(10);
        return allMainHotContent.stream().map(r_content -> getContentSimpleModelByContent(r_content.getContent()))
                .collect(Collectors.toList());
    }
}