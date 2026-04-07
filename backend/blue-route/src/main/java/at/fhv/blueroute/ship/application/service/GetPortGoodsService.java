package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.domain.model.Good;
import at.fhv.blueroute.ship.domain.model.PortGood;
import at.fhv.blueroute.ship.infrastructure.persistence.GoodRepository;
import at.fhv.blueroute.ship.infrastructure.persistence.PortGoodRepository;
import at.fhv.blueroute.ship.presentation.dto.PortGoodResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPortGoodsService {

    private final PortGoodRepository portGoodRepository;
    private final GoodRepository goodRepository;

    public GetPortGoodsService(PortGoodRepository portGoodRepository,
                               GoodRepository goodRepository) {
        this.portGoodRepository = portGoodRepository;
        this.goodRepository = goodRepository;
    }

    public List<PortGoodResponse> getGoods(Long portId) {
        return portGoodRepository.findByPortId(portId)
                .stream()
                .map(pg -> {
                    Good g = goodRepository.findById(pg.getGoodId()).orElseThrow();

                    return new PortGoodResponse(
                            g.getId(),
                            g.getName(),
                            g.getWeight(),
                            pg.getBuyPrice(),
                            pg.getSellPrice(),
                            pg.getStock()
                    );
                })
                .toList();
    }
}