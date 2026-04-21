package com.federation.tdfinale.service;

import com.federation.tdfinale.model.*;
import com.federation.tdfinale.repository.FederationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FederationService {
    private final FederationRepository repo;
    public FederationService(FederationRepository repo) { this.repo = repo; }

    public List<Member> createMembers(List<CreateMember> requests) {
        return requests.stream().map(this::createMember).collect(Collectors.toList());
    }

    private Member createMember(CreateMember req) {
        if (!req.isRegistrationFeePaid() || !req.isMembershipDuesPaid())
            throw new RuntimeException("Fees must be paid");
        
        String targetCollectivityId = req.getCollectivityIdentifier();
        if (targetCollectivityId == null || repo.findCollectivityById(targetCollectivityId).isEmpty())
            throw new RuntimeException("Collectivity not found");

        List<Member> referees = new ArrayList<>();
        if (req.getReferees() == null || req.getReferees().size() < 2) {
            throw new RuntimeException("At least two referees are required");
        }

        int localSponsors = 0;
        int otherSponsors = 0;

        for (String refId : req.getReferees()) {
            Member referee = repo.findMemberById(refId).orElseThrow(() -> new RuntimeException("Referee not found"));
            
            if (referee.getOccupation() != MemberOccupation.SENIOR) {
                throw new RuntimeException("Referees must be confirmed members (SENIOR)");
            }

            Optional<Collectivity> refCollectivity = repo.findAllCollectivities().stream()
                .filter(c -> c.getMembers().stream().anyMatch(m -> m.getId().equals(refId)))
                .findFirst();

            if (refCollectivity.isPresent() && refCollectivity.get().getId().equals(targetCollectivityId)) {
                localSponsors++;
            } else {
                otherSponsors++;
            }
            
            referees.add(referee);
        }

        if (localSponsors < otherSponsors) {
            throw new RuntimeException("Local sponsors must be at least equal to sponsors from other collectivities");
        }

        Member m = new Member(null, req.getFirstName(), req.getLastName(), req.getBirthDate(), 
                             req.getGender(), req.getAddress(), req.getProfession(), 
                             req.getPhoneNumber(), req.getEmail(), req.getOccupation(), 
                             LocalDate.now(), referees);
        
        Member savedMember = repo.saveMember(m);
        
        Collectivity c = repo.findCollectivityById(targetCollectivityId).get();
        c.getMembers().add(savedMember);
        repo.saveCollectivity(c);

        return savedMember;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivity> requests) {
        return requests.stream().map(this::createCollectivity).collect(Collectors.toList());
    }

    private Collectivity createCollectivity(CreateCollectivity req) {
        if (!req.isFederationApproval()) throw new RuntimeException("Federation approval required");
        if (req.getStructure() == null) throw new RuntimeException("Structure missing");

        if (req.getMembers() == null || req.getMembers().size() < 10) {
            throw new RuntimeException("A collectivity must have at least 10 members");
        }

        List<Member> members = req.getMembers().stream()
            .map(id -> repo.findMemberById(id).orElseThrow(() -> new RuntimeException("Member not found: " + id)))
            .collect(Collectors.toList());

        long seniorMembersCount = members.stream()
            .filter(m -> m.getAdhesionDate() != null && 
                    ChronoUnit.MONTHS.between(m.getAdhesionDate(), LocalDate.now()) >= 6)
            .count();
        
        if (seniorMembersCount < 5) {
            throw new RuntimeException("At least 5 members must have at least 6 months of seniority");
        }

        Member president = repo.findMemberById(req.getStructure().getPresident()).orElseThrow(() -> new RuntimeException("President not found"));
        Member vp = repo.findMemberById(req.getStructure().getVicePresident()).orElseThrow(() -> new RuntimeException("Vice President not found"));
        Member treasurer = repo.findMemberById(req.getStructure().getTreasurer()).orElseThrow(() -> new RuntimeException("Treasurer not found"));
        Member secretary = repo.findMemberById(req.getStructure().getSecretary()).orElseThrow(() -> new RuntimeException("Secretary not found"));

        CollectivityStructure structure = new CollectivityStructure(president, vp, treasurer, secretary);

        return repo.saveCollectivity(new Collectivity(null, req.getLocation(), LocalDate.now(), structure, members));
    }
}
