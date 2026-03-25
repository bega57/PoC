package at.fhv.blueroute.ship.application.mapper;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.stereotype.Component;

@Component
public class ShipMapper {

    public ShipResponse toResponse(Ship ship) {
        return new ShipResponse(
                ship.getId(),
                ship.getName(),
                ship.getType().name(),
                ship.getPrice(),
                ship.getCapacity(),
                ship.getSpeed(),
                ship.getOwner().getId()
        );
    }
}