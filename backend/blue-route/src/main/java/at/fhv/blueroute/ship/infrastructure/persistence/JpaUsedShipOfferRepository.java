package at.fhv.blueroute.ship.infrastructure.persistence;

import at.fhv.blueroute.ship.domain.model.UsedShipOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUsedShipOfferRepository extends JpaRepository<UsedShipOffer, Long> {

    List<UsedShipOffer> findBySession_SessionCode(String sessionCode);
}