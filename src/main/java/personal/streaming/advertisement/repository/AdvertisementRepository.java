package personal.streaming.advertisement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import personal.streaming.advertisement.domain.Advertisement;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query(value = "SELECT a FROM Advertisement a ORDER BY FUNCTION('RAND')")
    Slice<Advertisement> findRandomEntities(Pageable pageable);
}
