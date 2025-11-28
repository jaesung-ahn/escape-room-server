package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.GatheringForm;
import com.wiiee.server.admin.repository.GatheringRepository;
import com.wiiee.server.common.domain.content.price.ContentPrice;
import com.wiiee.server.common.domain.gathering.Gathering;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class GatheringService {

    @Autowired
    private final GatheringRepository gatheringRepository;

    @Autowired
    ContentService contentService;

    @Autowired
    UserService userService;

    @Transactional(readOnly = true)
    public List<Gathering> findAll() {
        return gatheringRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GatheringForm> findAllForForm() {
        List<GatheringForm> gatheringForms = new ArrayList<>();
        gatheringRepository.findAll().forEach(gathering->{
            GatheringForm gatheringForm = new GatheringForm();
            gatheringForm.setId(gathering.getId());
            gatheringForm.setTitle(gathering.getGatheringInfo().getTitle());
            gatheringForm.setLeader(gathering.getLeader().getProfile().getNickname());

            String companyName = gathering.getContent().getCompany().getBasicInfo().getName();
            String contentName = gathering.getContent().getContentBasicInfo().getName();
            gatheringForm.setCompanyAndContentName(companyName + " / " + contentName);
            gatheringForm.setGatheringStatus(gathering.getGatheringInfo().getGatheringStatus());
            gatheringForm.setCity(gathering.getGatheringInfo().getCity());
            gatheringForm.setCreatedAt(gathering.getCreatedAt());
            gatheringForms.add(gatheringForm);
        });
        return gatheringForms;
    }

    @Transactional(readOnly = true)
    public Optional<Gathering> findById(Long id) {
        return gatheringRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public GatheringForm findByIdForForm(Long id) {
        Optional<Gathering> optGathering = gatheringRepository.findById(id);
        Gathering gathering = optGathering.get();
        GatheringForm gatheringForm = new GatheringForm();
        gatheringForm.setId(gathering.getId());
        gatheringForm.setLeader(gathering.getLeader().getProfile().getNickname());
        gatheringForm.setTitle(gathering.getGatheringInfo().getTitle());
        gatheringForm.setInformation(gathering.getGatheringInfo().getInformation());
        gatheringForm.setGatheringStatus(gathering.getGatheringInfo().getGatheringStatus());

        String companyName = gathering.getContent().getCompany().getBasicInfo().getName();
        String contentName = gathering.getContent().getContentBasicInfo().getName();
        gatheringForm.setCompanyAndContentName(companyName + " / " + contentName);
        gatheringForm.setContentId(gathering.getContent().getId());

        gatheringForm.setState(gathering.getGatheringInfo().getState());
        gatheringForm.setCity(gathering.getGatheringInfo().getCity());
        gatheringForm.setRecruitType(gathering.getGatheringInfo().getRecruitType());
        gatheringForm.setMaxPeople(gathering.getGatheringInfo().getMaxPeople());
        gatheringForm.setAgeGroups(gathering.getGatheringInfo().getAgeGroupCodes());
        gatheringForm.setGenderType(gathering.getGatheringInfo().getGenderType());

        if (gathering.getContent().getContentPrices() != null && gathering.getContent().getContentPrices().size() > 0) {
            Comparator<ContentPrice> comparatorByAge = Comparator.comparingInt(ContentPrice::getPeopleNumber);

            ContentPrice contentPrice = gathering.getContent().getContentPrices().stream().min(comparatorByAge).get();
            gatheringForm.setPeopleNumber(contentPrice.getPeopleNumber());
            gatheringForm.setPrice(contentPrice.getPrice());
        }

        gatheringForm.setIsDateAgreement(gathering.getGatheringInfo().getIsDateAgreement());
        gatheringForm.setHopeDate(gathering.getGatheringInfo().getHopeDate());
        gatheringForm.setKakaoOpenChatUrl(gathering.getGatheringInfo().getKakaoOpenChatUrl());
        gatheringForm.setRealGatherDate(gathering.getGatheringInfo().getRealGatherDate());
        gatheringForm.setHitCount(gathering.getGatheringInfo().getHitCount());
        gatheringForm.setCreatedAt(gathering.getCreatedAt());

        log.debug(String.valueOf("gatheringForm = " + gatheringForm));

        return gatheringForm;
    }

    @Transactional
    public Optional<Gathering> saveGathering(Gathering gathering) {
//        Content content = contentService.findByIdContent(2L);
//        User user = userService.findById(1L);

//        user.addGathering(content,
        return Optional.of(gatheringRepository.save(gathering));
    }

//    @Transactional
//    public Optional<Gathering> saveTestGathering() {
//        Content content = contentService.findByIdContent(3L);
//        User user = userService.findById(1L);


//        GatheringInfo gatheringInfo = new GatheringInfo(
//                "동행 타이틀1",
//                "인포111",
//                State.SEOUL,
//                City.GANGSEOGU_SEOUL,
//                RecruitType.CONFIRM,
//                3,
//                GenderType.ONLY_MAN,
//                false,
//                LocalDate.now(),
//                "http://kakao.com/3892jweo",
//                GatheringStatus.RECRUIT_EXPIRED,
//                content.getContentPrices().get(0)
//        );

//        return Optional.of(gatheringRepository.save(user.addGathering(content, gatheringInfo)));
//    }
}
