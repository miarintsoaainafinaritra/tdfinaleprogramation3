package com.federation.tdfinale.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityStructure {
    private Member president;
    private Member vicePresident;
    private Member treasurer;
    private Member secretary;
}
