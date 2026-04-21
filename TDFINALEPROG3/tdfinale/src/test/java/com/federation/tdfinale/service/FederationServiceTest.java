package com.federation.tdfinale.service;

import com.federation.tdfinale.model.*;
import com.federation.tdfinale.repository.FederationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FederationServiceTest {

    private FederationRepository repo;
    private FederationService service;

    @BeforeEach
    void setUp() {
        repo = new FederationRepository();
        service = new FederationService(repo);
    }

    @Test
    void createCollectivity_ShouldFail_WhenLessThen10Members() {
        CreateCollectivity req = new CreateCollectivity("Tana", Collections.emptyList(), true, null);
        assertThrows(RuntimeException.class, () -> service.createCollectivities(Collections.singletonList(req)));
    }

    @Test
    void createMember_ShouldFail_WhenNoSponsors() {
        CreateMember req = new CreateMember();
        req.setRegistrationFeePaid(true);
        req.setMembershipDuesPaid(true);
        req.setCollectivityIdentifier("C1");
        req.setReferees(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> service.createMembers(Collections.singletonList(req)));
    }

    @Test
    void createMember_ShouldSucceed_WithCorrectSponsors() {
        Member m1 = new Member("M1", "John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE, "Addr", "Dev", 123, "a@b.com", MemberOccupation.SENIOR, LocalDate.now().minusMonths(7), Collections.emptyList());
        Member m2 = new Member("M2", "Jane", "Doe", LocalDate.of(1990, 1, 1), Gender.FEMALE, "Addr", "Dev", 456, "c@d.com", MemberOccupation.SENIOR, LocalDate.now().minusMonths(7), Collections.emptyList());
        repo.saveMember(m1);
        repo.saveMember(m2);

        List<String> memberIds = new ArrayList<>();
        for(int i=0; i<10; i++) {
            Member m = new Member("MX"+i, "M", "X", LocalDate.now(), Gender.MALE, "A", "P", 1, "e", MemberOccupation.SENIOR, LocalDate.now().minusMonths(7), Collections.emptyList());
            repo.saveMember(m);
            memberIds.add(m.getId());
        }

        CreateCollectivityStructure struct = new CreateCollectivityStructure(memberIds.get(0), memberIds.get(1), memberIds.get(2), memberIds.get(3));
        CreateCollectivity cReq = new CreateCollectivity("Tana", memberIds, true, struct);
        Collectivity c = service.createCollectivities(Collections.singletonList(cReq)).get(0);

        CreateMember mReq = new CreateMember();
        mReq.setFirstName("New");
        mReq.setLastName("Member");
        mReq.setBirthDate(LocalDate.of(2000, 1, 1));
        mReq.setGender(Gender.MALE);
        mReq.setOccupation(MemberOccupation.JUNIOR);
        mReq.setCollectivityIdentifier(c.getId());
        mReq.setRegistrationFeePaid(true);
        mReq.setMembershipDuesPaid(true);
        
        List<String> referees = new ArrayList<>();
        referees.add(memberIds.get(0));
        referees.add(memberIds.get(1));
        mReq.setReferees(referees);

        List<Member> created = service.createMembers(Collections.singletonList(mReq));
        assertEquals(1, created.size());
        assertEquals("New", created.get(0).getFirstName());
    }
}
