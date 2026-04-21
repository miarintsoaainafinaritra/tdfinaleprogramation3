package com.federation.tdfinale.model;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMember {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private Integer phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private String collectivityIdentifier;
    private List<String> referees;
    private boolean registrationFeePaid;
    private boolean membershipDuesPaid;
}
