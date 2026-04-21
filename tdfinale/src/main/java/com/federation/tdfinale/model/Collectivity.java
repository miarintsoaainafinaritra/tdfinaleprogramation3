package com.federation.tdfinale.model;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collectivity {
    private String id;
    private String location;
    private LocalDate creationDate;
    private CollectivityStructure structure;
    private List<Member> members;
}
