package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.content.ContentForm;
import com.wiiee.server.admin.form.content.ContentListForm;
import com.wiiee.server.admin.repository.ContentPriceRepository;
import com.wiiee.server.admin.repository.content.ContentCustomRepositoryImpl;
import com.wiiee.server.admin.repository.content.ContentRepository;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import com.wiiee.server.common.domain.content.price.ContentPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;

    private final ContentCustomRepositoryImpl contentCustomRepository;

    private final ContentPriceRepository contentPriceRepository;

    private final ImageService imageService;

    private final CompanyService companyService;

    @Transactional(readOnly = true)
    public List<ContentListForm> findAllContentList(ContentForm contentForm) {
        Page<Content> contentPages = contentCustomRepository.findAllByContentGetListForm(
                PageRequest.of(1, 1), contentForm);
        return contentPages.toList().stream().map(content -> ContentListForm.fromContentListForm(content))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContentForm> findAllSimpleContentForm() {
        return contentRepository.findAll().stream()
                .map(content -> ContentForm.fromContentSimpleForm(content))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContentForm> findAllByCompanyIdContentForm() {
        return contentRepository.findAll().stream()
                .map(content -> ContentForm.fromContentSimpleForm(content))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Content findByIdContent(Long contentId) {
        Optional<Content> optionalCompany = contentRepository.findById(contentId);

        return optionalCompany.get();
    }

    @Transactional(readOnly = true)
    public ContentForm findById(Long contentId) {
        Optional<Content> optionalCompany = contentRepository.findById(contentId);

        Content content = optionalCompany.get();

        ContentForm contentForm = new ContentForm();
        contentForm.setId(content.getId());
        contentForm.setCompanyName(content.getCompany().getBasicInfo().getName());
        contentForm.setCompanyId(content.getCompany().getId());

        contentForm.setName(content.getContentBasicInfo().getName());
        contentForm.setOperated(content.getContentBasicInfo().getIsOperated());
        contentForm.setDisplayNew(content.getContentBasicInfo().getIsNew());
        if (content.getContentBasicInfo().getNewDisplayExpirationDate() != null) {
            contentForm.setNewDisplayExpirationDate(content.getContentBasicInfo().getNewDisplayExpirationDate().toString());
        }

        contentForm.setGenreCode(content.getContentBasicInfo().getGenre().getCode());
        contentForm.setMinPeople(content.getContentBasicInfo().getMinPeople());
        contentForm.setMaxPeople(content.getContentBasicInfo().getMaxPeople());
        contentForm.setDifficultyCode(content.getContentBasicInfo().getDifficulty().getCode());
        contentForm.setActivityLevelCode(content.getContentBasicInfo().getActivityLevel().getCode());
        contentForm.setEscapeTypeCode(content.getContentBasicInfo().getEscapeType().getCode());
        contentForm.setNoEscapeType(content.getContentBasicInfo().getIsNoEscapeType());
        contentForm.setPlayTime(content.getContentBasicInfo().getPlayTime());
        contentForm.setInformation(content.getContentBasicInfo().getInformation());

        List<Long> imageIds = content.getContentBasicInfo().getImageIds();
        List<ContentForm.ContentImageForm> contentImageForms = null;
        log.debug(String.valueOf("contentImageForms = " + contentImageForms));
        if (imageIds != null) {
            contentImageForms = new ArrayList<>();

            int i = 0;
            for (Long id :imageIds){
                boolean isRepImg = false;
                if (i == 0) isRepImg = true;

                log.debug(String.valueOf("imageId = " + id));

                Image contentImage = imageService.getImageById(id);
                log.debug(String.valueOf("contentImage = " + contentImage));
                System.out.println("contentImage = " + contentImage.getUrl());
                contentImageForms.add(new ContentForm.ContentImageForm(
                        contentImage.getUrl(),
                        contentImage.getId(),
                        isRepImg
                ));
                i++;
            }
        }

        contentForm.setContentImages(contentImageForms);

        List<ContentForm.ContentPriceForm> contentPriceForms = null;
        List<ContentPrice> contentPrices = content.getContentPrices();

        log.debug(String.valueOf("contentPriceForms = " + contentPriceForms));
        log.debug(String.valueOf("contentPrices = " + contentPrices));

        if (contentPrices != null && contentPrices.size() > 0) {
            contentPriceForms = new ArrayList<>();
            log.debug(String.valueOf("contentPriceForms2 = " + contentPriceForms));
            for (ContentPrice contentPrice: contentPrices) {
                contentPriceForms.add(new ContentForm.ContentPriceForm(
                        contentPrice.getId(),
                        contentPrice.getPeopleNumber(),
                        contentPrice.getPrice()
                ));
            }
        }

        contentForm.setContentPrices(contentPriceForms);

        return contentForm;
    }

    @Transactional(readOnly = true)
    public Content updateContent(Long contentId) {
        Optional<Content> optionalCompany = contentRepository.findById(contentId);

        Content content = optionalCompany.get();
        return content;
    }

    @Transactional
    public Content updateContent(Long contentId, List<ContentForm.ContentPriceForm> contentPriceForms) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(NoSuchElementException::new);
        // price는 기존 price id가 있으면 해당 부분 update price
        // 없으면 새로 add price
        // 삭제는 별도 호출에서 화면상에서 바로 삭제 처리
        for (ContentForm.ContentPriceForm contentPriceForm: contentPriceForms) {
            var contentPriceId = contentPriceForm.getContentPriceId();
            if (contentPriceId == null) {
                content.addPrice(contentPriceForm.getPeopleNumber(),
                        contentPriceForm.getPrice());
            }
            else if (contentPriceId > 0 ) {
                System.out.println("contentPriceId = " + contentPriceId + " "+ contentPriceForm.getPeopleNumber());
                updatePrice(contentPriceId, contentPriceForm.getPeopleNumber(),
                        contentPriceForm.getPrice());
            }
        }
        return content;
    }

    @Transactional
    public void updatePrice(Long contentPriceId, Integer peopleNumber, Integer price) {
        ContentPrice contentPrice = contentPriceRepository.findById(contentPriceId)
                .orElseThrow(NoSuchElementException::new);

        contentPrice.updateContentPrice(peopleNumber, price);
        contentPriceRepository.save(contentPrice);
    }

    @Transactional
    public Content createContent(Long companyId, ContentBasicInfo contentBasicInfo, List<ContentForm.ContentPriceForm> contentPriceForms) {

        final var contentAdded = companyService.findById(companyId)
                .map(company -> company.addContent(contentBasicInfo))
                .map(contentRepository::save)
                .orElseThrow(NoSuchElementException::new);

        for (ContentForm.ContentPriceForm contentPriceForm: contentPriceForms) {
            contentAdded.addPrice(contentPriceForm.getPeopleNumber(),
                    contentPriceForm.getPrice());
        }
        return contentAdded;
    }

    @Transactional
    public void addContentPrice(Content content, List<ContentForm.ContentPriceForm> contentPriceForms) {
            for (ContentForm.ContentPriceForm contentPriceForm: contentPriceForms) {
                content.addPrice(contentPriceForm.getPeopleNumber(),
                        contentPriceForm.getPrice());
            }
    }

    @Transactional
    public Optional<Content> saveContent(Content content) {

        return Optional.of(contentRepository.save(content));
    }

    @Transactional
    public void deleteContentPrice(Long contentPriceId) {
        contentPriceRepository.deleteById(contentPriceId);
    }
}
