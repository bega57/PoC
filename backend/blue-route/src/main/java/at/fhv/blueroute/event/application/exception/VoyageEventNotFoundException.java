package at.fhv.blueroute.event.application.exception;

public class VoyageEventNotFoundException extends RuntimeException {

    public VoyageEventNotFoundException(Long voyageId) {
        super("No event found for voyage with id: " + voyageId);
    }
}