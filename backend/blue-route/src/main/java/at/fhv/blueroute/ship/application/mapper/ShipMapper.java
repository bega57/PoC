package at.fhv.blueroute.ship.application.mapper;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.stereotype.Component;

@Component
public class ShipMapper {

    public ShipResponse toResponse(Ship ship, double sellPrice, double refuelCost) {

        ShipResponse response = new ShipResponse(
                ship.getId(),
                ship.getName(),
                ship.getType().name(),
                ship.getPrice(),
                ship.getSpeed(),
                ship.getOwner().getId(),
                (int) Math.round(ship.getCondition()),
                (int) Math.round(ship.getFuelLevel()),
                ship.getCurrentPort(),
                ship.isTraveling(),
                ship.getCargoCapacity()
        );

        response.setUsedCapacity(ship.getUsedCapacity());
        response.setSellPrice(sellPrice);
        response.setRefuelCost(refuelCost);

        return response;
    }
}