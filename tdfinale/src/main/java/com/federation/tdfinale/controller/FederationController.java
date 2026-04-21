package com.federation.tdfinale.controller;

import com.federation.tdfinale.model.*;
import com.federation.tdfinale.service.FederationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
public class FederationController {
    private final FederationService service;

    public FederationController(FederationService service) {
        this.service = service;
    }

    @PostMapping("/collectivities")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Collectivity> createCollectivities(@RequestBody List<CreateCollectivity> requests) {
        return service.createCollectivities(requests);
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Member> createMembers(@RequestBody List<CreateMember> requests) {
        return service.createMembers(requests);
    }

    @GetMapping("/members")
    public List<Member> getAllMembers() {
        return service.findAllMembers();
    }

    @GetMapping("/collectivities")
    public List<Collectivity> getAllCollectivities() {
        return service.findAllCollectivities();
    }
}
