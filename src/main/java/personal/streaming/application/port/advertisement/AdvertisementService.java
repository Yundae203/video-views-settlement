package personal.streaming.application.port.advertisement;

import personal.streaming.application.common.enums.Category;
import personal.streaming.advertisement.domain.Advertisement;

import java.util.List;

public interface AdvertisementService {

    List<Advertisement> insertAdvertisements(Category category, Long contentLength);

}
