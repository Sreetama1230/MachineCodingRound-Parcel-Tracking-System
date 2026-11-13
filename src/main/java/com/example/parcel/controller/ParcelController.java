package com.example.parcel.controller;

import com.example.parcel.service.ParcelTrackingSystem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Thin REST wrapper so you can poke the system with curl/Postman.
 * The real logic lives in ParcelTrackingSystemImpl.
 */
@RestController
@RequestMapping("/api/parcels/{parcelId}/attributes")
public class ParcelController {

    public record SetRequest(int timestamp, int value, Integer ttl) {}

    private final ParcelTrackingSystem system;

    public ParcelController(ParcelTrackingSystem system) {
        this.system = system;
    }

    @PutMapping("/{attribute}")
    public ResponseEntity<Void> set(@PathVariable String parcelId,
                                    @PathVariable String attribute,
                                    @RequestBody SetRequest req) {
        if (req.ttl() == null) {
            system.setAttribute(req.timestamp(), parcelId, attribute, req.value());
        } else {
            system.setAttributeWithTtl(req.timestamp(), parcelId, attribute, req.value(), req.ttl());
        }
        return ResponseEntity.ok().build();
    }

    /** GET ...?timestamp=10            -> value at "now" (timestamp)
     *  GET ...?timestamp=10&at=3       -> value as it was at time 3 */
    @GetMapping("/{attribute}")
    public ResponseEntity<Integer> get(@PathVariable String parcelId,
                                       @PathVariable String attribute,
                                       @RequestParam int timestamp,
                                       @RequestParam(required = false) Integer at) {
        var value = (at == null)
                ? system.getAttribute(timestamp, parcelId, attribute)
                : system.getAttributeAt(timestamp, parcelId, attribute, at);
        return value.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<String> list(@PathVariable String parcelId,
                             @RequestParam int timestamp,
                             @RequestParam(defaultValue = "") String prefix) {
        return system.listAttributesByPrefix(timestamp, parcelId, prefix);
    }
}
