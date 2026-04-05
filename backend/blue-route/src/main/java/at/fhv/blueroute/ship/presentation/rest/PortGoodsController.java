package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.GetPortGoodsService;
import at.fhv.blueroute.ship.presentation.dto.PortGoodResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/port-goods")
public class PortGoodsController {

    private final GetPortGoodsService service;

    public PortGoodsController(GetPortGoodsService service) {
        this.service = service;
    }

    @GetMapping("/{portId}")
    public List<PortGoodResponse> get(@PathVariable Long portId) {
        return service.getGoods(portId);
    }
}