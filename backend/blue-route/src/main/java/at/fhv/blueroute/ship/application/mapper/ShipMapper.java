package at.fhv.blueroute.ship.application.mapper;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.stereotype.Component;

@Component
public class ShipMapper {

    public ShipResponse toResponse(Ship ship, double sellPrice) {
        ShipResponse response = new ShipResponse(
                ship.getId(),
                ship.getName(),
                ship.getType().name(),
                ship.getPrice(),
                ship.getSpeed(),
                ship.getOwner().getId(),
                ship.getCondition(),
                ship.getFuelLevel(),
                ship.getCurrentPort(),
                ship.isTraveling(),
                ship.getCargoCapacity()
        );

        response.setSellPrice(sellPrice);

        return response;

    }
}