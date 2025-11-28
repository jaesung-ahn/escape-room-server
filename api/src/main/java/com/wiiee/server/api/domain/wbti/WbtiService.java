package com.wiiee.server.api.domain.wbti;

import com.wiiee.server.api.application.exception.CustomException;
import com.wiiee.server.api.application.wbti.WbtiSimpleResponseDTO;
import com.wiiee.server.api.domain.code.StatusCode;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.infrastructure.repository.user.UserCustomRepository;
import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WbtiService {

    private final WbtiRepository wbtiRepository;
    private final ImageService imageService;

    private final UserCustomRepository userCustomRepository;

    @Transactional(readOnly = true)
    public Optional<Wbti> findById(Long wbtiId) {
        return wbtiRepository.findById(wbtiId);
    }

    @Transactional(readOnly = true)
    public List<WbtiSimpleResponseDTO> findAll() {

        List<WbtiSimpleResponseDTO> wbtiList = wbtiRepository.findAll().stream()
                .map(wbti ->
                        WbtiSimpleResponseDTO.fromWbtiSimpleResponseDTO(wbti)
                ).collect(Collectors.toList());
        wbtiList.stream().forEach(wbtidto -> {
                    if (wbtidto.getImgId() != null) {
                        wbtidto.setImgUrl(
                                imageService.getImageById(wbtidto.getImgId()).getUrl()
                        );
                    }
                }
        );

        return wbtiList;
    }

    @Transactional
    @Modifying
    public void saveWbti(Long userId, Long wbtiId) {
        Wbti wbti = wbtiRepository.findById(wbtiId).orElseThrow(
                () -> new CustomException(StatusCode.ERROR_NO_EXIST_ZAMFIT_TEXT_CODE,
                        StatusCode.ERROR_NO_EXIST_ZAMFIT_TEXT_MSG, null)
        );
        userCustomRepository.saveWbti(userId, wbti);
    }
}
