package at.fhv.blueroute.port.presentation.rest;

import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ports")
public class PortController {

    private final JpaPortRepository portRepository;

    public PortController(JpaPortRepository portRepository) {
        this.portRepository = portRepository;
    }

    @GetMapping
    public List<Port> getAllPorts() {
        return portRepository.findAll();
    }
}