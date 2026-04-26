package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.domain.model.UsedShipOffer;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaUsedShipOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUsedShipOffersService {

    private final JpaUsedShipOfferRepository repository;

    public GetUsedShipOffersService(JpaUsedShipOfferRepository repository) {
        this.repository = repository;
    }

    public List<UsedShipOffer> execute(String sessionCode) {
        return repository.findBySession_SessionCode(sessionCode);
    }
}