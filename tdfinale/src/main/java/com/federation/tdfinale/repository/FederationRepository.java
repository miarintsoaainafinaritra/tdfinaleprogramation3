package com.federation.tdfinale.repository;

import com.federation.tdfinale.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class FederationRepository {
    private final JdbcTemplate jdbc;
    private final AtomicLong cId = new AtomicLong(1), mId = new AtomicLong(1);

    public FederationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Member saveMember(Member m) {
        if (m.getId() == null) m.setId("M" + mId.getAndIncrement());
        
        jdbc.update("INSERT INTO member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                m.getId(), m.getFirstName(), m.getLastName(), m.getBirthDate(), m.getGender().name(), 
                m.getAddress(), m.getProfession(), m.getPhoneNumber(), m.getEmail(), m.getOccupation().name(), m.getAdhesionDate());

        if (m.getReferees() != null) {
            for (Member ref : m.getReferees()) {
                jdbc.update("INSERT INTO member_referees (member_id, referee_id) VALUES (?, ?)", m.getId(), ref.getId());
            }
        }
        return m;
    }

    public Optional<Member> findMemberById(String id) {
        List<Member> results = jdbc.query("SELECT * FROM member WHERE id = ?", new MemberRowMapper(), id);
        if (results.isEmpty()) return Optional.empty();
        
        Member m = results.get(0);
        List<Member> referees = jdbc.query(
            "SELECT m.* FROM member m JOIN member_referees mr ON m.id = mr.referee_id WHERE mr.member_id = ?",
            new MemberRowMapper(), id);
        m.setReferees(referees);
        
        return Optional.of(m);
    }

    public Collectivity saveCollectivity(Collectivity c) {
        if (c.getId() == null) c.setId("C" + cId.getAndIncrement());
        
        jdbc.update("INSERT INTO collectivity (id, location, creation_date, president_id, vice_president_id, treasurer_id, secretary_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                c.getId(), c.getLocation(), c.getCreationDate(),
                c.getStructure().getPresident().getId(),
                c.getStructure().getVicePresident().getId(),
                c.getStructure().getTreasurer().getId(),
                c.getStructure().getSecretary().getId());

        if (c.getMembers() != null) {
            for (Member m : c.getMembers()) {
                jdbc.update("INSERT INTO collectivity_members (collectivity_id, member_id) VALUES (?, ?)", c.getId(), m.getId());
            }
        }
        return c;
    }

    public Optional<Collectivity> findCollectivityById(String id) {
        List<Collectivity> results = jdbc.query("SELECT * FROM collectivity WHERE id = ?", new CollectivityRowMapper(), id);
        if (results.isEmpty()) return Optional.empty();
        
        Collectivity c = results.get(0);
        List<Member> members = jdbc.query(
            "SELECT m.* FROM member m JOIN collectivity_members cm ON m.id = cm.member_id WHERE cm.collectivity_id = ?",
            new MemberRowMapper(), id);
        c.setMembers(members);
        
        return Optional.of(c);
    }

    public List<Collectivity> findAllCollectivities() {
        List<Collectivity> collectivities = jdbc.query("SELECT * FROM collectivity", new CollectivityRowMapper());
        for (Collectivity c : collectivities) {
            List<Member> members = jdbc.query(
                "SELECT m.* FROM member m JOIN collectivity_members cm ON m.id = cm.member_id WHERE cm.collectivity_id = ?",
                new MemberRowMapper(), c.getId());
            c.setMembers(members);
        }
        return collectivities;
    }

    public List<Member> findAllMembers() {
        List<Member> members = jdbc.query("SELECT * FROM member", new MemberRowMapper());
        for (Member m : members) {
            List<Member> referees = jdbc.query(
                "SELECT m_ref.* FROM member m_ref JOIN member_referees mr ON m_ref.id = mr.referee_id WHERE mr.member_id = ?",
                new MemberRowMapper(), m.getId());
            m.setReferees(referees);
        }
        return members;
    }

    private class MemberRowMapper implements RowMapper<Member> {
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            Member m = new Member();
            m.setId(rs.getString("id"));
            m.setFirstName(rs.getString("first_name"));
            m.setLastName(rs.getString("last_name"));
            m.setBirthDate(rs.getDate("birth_date").toLocalDate());
            m.setGender(Gender.valueOf(rs.getString("gender")));
            m.setAddress(rs.getString("address"));
            m.setProfession(rs.getString("profession"));
            m.setPhoneNumber(rs.getInt("phone_number"));
            m.setEmail(rs.getString("email"));
            m.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
            m.setAdhesionDate(rs.getDate("adhesion_date").toLocalDate());
            return m;
        }
    }

    private class CollectivityRowMapper implements RowMapper<Collectivity> {
        @Override
        public Collectivity mapRow(ResultSet rs, int rowNum) throws SQLException {
            Collectivity c = new Collectivity();
            c.setId(rs.getString("id"));
            c.setLocation(rs.getString("location"));
            c.setCreationDate(rs.getDate("creation_date").toLocalDate());
            
            Member president = findMemberById(rs.getString("president_id")).orElse(null);
            Member vp = findMemberById(rs.getString("vice_president_id")).orElse(null);
            Member treasurer = findMemberById(rs.getString("treasurer_id")).orElse(null);
            Member secretary = findMemberById(rs.getString("secretary_id")).orElse(null);
            
            c.setStructure(new CollectivityStructure(president, vp, treasurer, secretary));
            return c;
        }
    }
}
