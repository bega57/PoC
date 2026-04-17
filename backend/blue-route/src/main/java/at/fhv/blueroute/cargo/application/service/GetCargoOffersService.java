package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.cargo.presentation.dto.CargoOfferDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCargoOffersService {

    private final JpaCargoRepository cargoRepository;

    public GetCargoOffersService(JpaCargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public List<CargoOfferDto> execute() {
        return cargoRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private CargoOfferDto toDto(Cargo cargo) {
        return new CargoOfferDto(
                cargo.getId(),
                "Cargo " + cargo.getId(),
                cargo.getOriginPort() != null ? cargo.getOriginPort().getName() : "Unknown",
                cargo.getDestinationPort() != null ? cargo.getDestinationPort().getName() : "Unknown",
                cargo.getPrice(),
                cargo.getReward(),
                cargo.getRequiredCapacity(),
                cargo.getRequiredTicks(),
                cargo.getFuelConsumption(),
                cargo.getRiskLevel() != null ? cargo.getRiskLevel().name() : "LOW"
        );
    }
}