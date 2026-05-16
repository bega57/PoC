package at.fhv.blueroute.session.application.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionTickScheduler {

    private final SessionTickService sessionTickService;

    public SessionTickScheduler(SessionTickService sessionTickService) {
        this.sessionTickService = sessionTickService;
    }

    @Scheduled(fixedRateString = "${game.tick.rate}")
    public void tickSessions() {
        sessionTickService.processTicks();
    }
}