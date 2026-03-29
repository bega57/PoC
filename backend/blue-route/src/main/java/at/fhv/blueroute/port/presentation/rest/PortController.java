package at.fhv.blueroute.port.presentation.rest;

import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ports")
@CrossOrigin
public class PortController {

    private final JpaPortRepository repo;

    public PortController(JpaPortRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Port> getAll() {
        return repo.findAll();
    }
}