package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.WbtiDetailForm;
import com.wiiee.server.admin.form.WbtiListForm;
import com.wiiee.server.admin.repository.WbtiRepository;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WbtiService {

    private final WbtiRepository wbtiRepository;

    private final ModelMapper modelMapper;

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public List<WbtiListForm> findAllForForm() {

        return wbtiRepository.findAll().stream().map(
                wbti -> modelMapper.map(wbti, WbtiListForm.class)
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WbtiDetailForm findByIdForForm(Long wbtiId) {
        Wbti wbti = wbtiRepository.findById(wbtiId).get();
        WbtiDetailForm wbtiDetailForm = WbtiDetailForm.fromWbtiSimpleForm(wbti);

        if (wbti.getWbtiImageId() != null) {
            Image image = imageService.getImageById(wbtiDetailForm.getWbtiImageId());
            wbtiDetailForm.setWbtiImageUrl(image.getUrl());
        }

        return wbtiDetailForm;
    }

    public void updateWbti(WbtiDetailForm wbtiDetailForm) {
        Wbti wbti = wbtiRepository.findById(wbtiDetailForm.getId()).get();

        List<Wbti> wbtiPartners = wbtiDetailForm.getWbtiPartnerList().stream().map(
                wbtiId -> wbtiRepository.findById(Long.valueOf(wbtiId)).get()
        ).collect(Collectors.toList());
        wbti.updateWbti(wbtiDetailForm.getName(),
                wbtiDetailForm.getWbtiImageId(),
                wbtiDetailForm.getTags(),
                wbtiDetailForm.getDescriptions(),
                wbtiPartners);

        wbtiRepository.save(wbti);
    }
}
