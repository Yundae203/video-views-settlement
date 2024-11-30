package personal.streaming.advertisement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.application.common.enums.Category;
import personal.streaming.application.port.advertisement.AdvertisementService;
import personal.streaming.advertisement.domain.Advertisement;
import personal.streaming.advertisement.repository.AdvertisementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementRandomService implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    private static final Long SECOND_PER_AD = 300L;

    @Override
    @Transactional
    public List<Advertisement> insertAdvertisements(Category category, Long contentLength) {
        int adAmount = (int) (contentLength / SECOND_PER_AD);
        PageRequest pageRequest = PageRequest.of(0, adAmount);
        return advertisementRepository.findRandomEntities(pageRequest)
                .getContent();
    }
}
