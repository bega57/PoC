package at.fhv.blueroute.common.service;

import org.springframework.stereotype.Service;

@Service
public class PricingService {

    private static final double VAT = 0.20;

    public double applyVAT(double netPrice) {
        return netPrice * (1 + VAT);
    }
}