package com.federation.tdfinale.repository;

import com.federation.tdfinale.model.*;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class FederationRepository {
    private final Map<String, Collectivity> collectivities = new ConcurrentHashMap<>();
    private final Map<String, Member> members = new ConcurrentHashMap<>();
    private final AtomicLong cId = new AtomicLong(1), mId = new AtomicLong(1);

    public Member saveMember(Member m) {
        if (m.getId() == null) m.setId("M" + mId.getAndIncrement());
        members.put(m.getId(), m);
        return m;
    }

    public Optional<Member> findMemberById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    public Collectivity saveCollectivity(Collectivity c) {
        if (c.getId() == null) c.setId("C" + cId.getAndIncrement());
        collectivities.put(c.getId(), c);
        return c;
    }

    public Optional<Collectivity> findCollectivityById(String id) {
        return Optional.ofNullable(collectivities.get(id));
    }

    public List<Collectivity> findAllCollectivities() {
        return new ArrayList<>(collectivities.values());
    }
}
