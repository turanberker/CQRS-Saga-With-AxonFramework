package com.mbt.estore.productservice.command.rest;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/management")
public class EventsReplayController {

    @Autowired
    private EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventProcessor/{eventProcessor}/reset")
    public ResponseEntity<String> replayEvents(@PathVariable(value = "eventProcessor") String eventProcessorName) {
        Optional<TrackingEventProcessor> trackingEventProcessor =
                eventProcessingConfiguration.eventProcessor(eventProcessorName, TrackingEventProcessor.class);
        if (trackingEventProcessor.isPresent()) {
            TrackingEventProcessor eventProcessor = trackingEventProcessor.get();
            eventProcessor.shutDown();
            eventProcessor.resetTokens();
            eventProcessor.start();

            return ResponseEntity.ok(String.format("The event processor with a name [%s] has been reset", eventProcessorName));
        } else {
            return ResponseEntity.badRequest().body(String.format("The event processor with a name [%s] is not a event processor. Only Tracking event processor is supported"
                    , eventProcessorName));
        }
    }
}
