package at.fhv.blueroute.voyage.presentation.rest;

import at.fhv.blueroute.voyage.application.service.FinishVoyageService;
import at.fhv.blueroute.voyage.application.service.GetVoyagesService;
import at.fhv.blueroute.voyage.application.service.ProcessVoyageTickService;
import at.fhv.blueroute.voyage.application.service.StartVoyageService;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import at.fhv.blueroute.voyage.presentation.dto.StartVoyageRequest;
import at.fhv.blueroute.voyage.presentation.dto.VoyageResponse;
import at.fhv.blueroute.voyage.presentation.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voyages")
public class VoyageController {

    private final StartVoyageService startVoyageService;
    private final GetVoyagesService getVoyagesService;
    private final JpaVoyageRepository voyageRepository;
    private final FinishVoyageService finishVoyageService;
    private final ProcessVoyageTickService processVoyageTickService;

    public VoyageController(
            StartVoyageService startVoyageService,
            GetVoyagesService getVoyagesService,
            JpaVoyageRepository voyageRepository,
            FinishVoyageService finishVoyageService,
            ProcessVoyageTickService processVoyageTickService
    ) {
        this.startVoyageService = startVoyageService;
        this.getVoyagesService = getVoyagesService;
        this.voyageRepository = voyageRepository;
        this.finishVoyageService = finishVoyageService;
        this.processVoyageTickService = processVoyageTickService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startVoyage(
            @RequestBody StartVoyageRequest request
    ) {
        try {
            VoyageResponse voyage =
                    VoyageResponse.from(
                            startVoyageService.startVoyage(
                                    request.getShipId(),
                                    request.getCargoId(),
                                    request.getSessionId(),
                                    request.getCurrentTick()
                            ),
                            request.getCurrentTick()
                    );

            return ResponseEntity.ok(voyage);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/finish")
    public VoyageResponse finishVoyage(
            @PathVariable Long id,
            @RequestParam int currentTick
    ) {

        return VoyageResponse.from(
                finishVoyageService.finishVoyage(
                        id,
                        currentTick
                ),
                currentTick
        );
    }

    @GetMapping
    public List<VoyageResponse> getVoyages(
            @RequestParam Long sessionId,
            @RequestParam int currentTick
    ) {

        return getVoyagesService
                .getAllVoyages(sessionId, currentTick);
    }

    @GetMapping("/{id}")
    public VoyageResponse getVoyage(
            @PathVariable Long id
    ) {

        Voyage voyage =
                voyageRepository.findById(id)
                        .orElseThrow();

        return VoyageResponse.from(
                voyage,
                voyage.getStartTick()
        );
    }

    @PostMapping("/{id}/event-triggered")
    public void markEventTriggered(
            @PathVariable Long id
    ) {

        Voyage voyage =
                voyageRepository.findById(id)
                        .orElseThrow();

        voyage.setEventTriggered(true);

        voyageRepository.save(voyage);
    }

    @PostMapping("/{id}/event-resolved")
    public void markEventResolved(
            @PathVariable Long id,
            @RequestBody EventResolvedRequest request
    ) {

        Voyage voyage =
                voyageRepository.findById(id)
                        .orElseThrow();

        voyage.setEventResolved(true);
        voyage.setEventResultMessage(request.getResultMessage());

        voyageRepository.save(voyage);
    }

    @PostMapping("/{id}/event-cost")
    public void setEventCost(
            @PathVariable Long id,
            @RequestBody EventCostRequest request
    ) {

        Voyage voyage =
                voyageRepository.findById(id)
                        .orElseThrow();

        voyage.setEventCost(request.getEventCost());

        voyageRepository.save(voyage);
    }

    @PostMapping("/{id}/delay")
    public void delayVoyage(
            @PathVariable Long id,
            @RequestBody DelayVoyageRequest request
    ) {

        Voyage voyage =
                voyageRepository.findById(id)
                        .orElseThrow();

        voyage.setArrivalTick(
                voyage.getArrivalTick() + request.getExtraDelayTicks()
        );

        voyage.setExtraDelayTicks(
                voyage.getExtraDelayTicks() + request.getExtraDelayTicks()
        );

        voyage.setExtraFuelLoss(
                voyage.getExtraFuelLoss() + request.getExtraFuelLoss()
        );

        voyage.setExtraConditionLoss(
                voyage.getExtraConditionLoss() + request.getExtraConditionLoss()
        );

        voyageRepository.save(voyage);
    }

    @PostMapping("/{id}/reward-loss")
    public void reduceReward(
            @PathVariable Long id,
            @RequestBody RewardLossRequest request
    ) {

        Voyage voyage =
                voyageRepository.findById(id)
                        .orElseThrow();

        double factor =
                (100.0 - request.getRewardLossPercent()) / 100.0;

        voyage.setReward(
                voyage.getReward() * factor
        );

        voyage.setRewardLossPercent(
                voyage.getRewardLossPercent() + request.getRewardLossPercent()
        );

        voyageRepository.save(voyage);
    }

    @PostMapping("/{id}/event-plan")
    public void assignEventPlan(
            @PathVariable Long id,
            @RequestBody EventPlanRequest request
    ) {

        Voyage voyage =
                voyageRepository.findById(id)
                        .orElseThrow();

        voyage.setPendingEventType(
                request.getEventType()
        );

        voyage.setEventTriggerTick(
                request.getEventTriggerTick()
        );

        voyage.setEventTriggered(false);
        voyage.setEventResolved(false);

        voyageRepository.save(voyage);
    }

    @PostMapping("/process-tick")
    public List<VoyageResponse> processTick(
            @RequestParam Long sessionId,
            @RequestParam int currentTick
    ) {
        return processVoyageTickService
                .processTick(sessionId, currentTick)
                .stream()
                .map(voyage -> VoyageResponse.from(voyage, currentTick))
                .toList();
    }

}