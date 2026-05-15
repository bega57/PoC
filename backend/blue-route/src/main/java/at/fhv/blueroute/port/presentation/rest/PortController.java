package at.fhv.blueroute.port.presentation.rest;

import at.fhv.blueroute.port.client.PortServiceClient;
import at.fhv.blueroute.port.client.dto.PortResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ports")
public class PortController {

    private final PortServiceClient portServiceClient;

    public PortController(PortServiceClient portServiceClient) {
        this.portServiceClient = portServiceClient;
    }

    @GetMapping
    public List<PortResponse> getAllPorts() {
        return portServiceClient.getAllPorts();
    }
}