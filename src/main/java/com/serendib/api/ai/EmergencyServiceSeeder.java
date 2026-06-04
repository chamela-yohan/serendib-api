package com.serendib.api.ai;

import com.serendib.api.repository.EmergencyServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2) // runs after KnowledgeBaseSeeder
public class EmergencyServiceSeeder implements ApplicationRunner {
    private final EmergencyServiceRepository emergencyServiceRepository;
    private final JdbcTemplate jdbcTemplate;

    // Format: {name, type, address, province, district, lat, lng, phone, phone2, is24h, notes}
    private static final Object[][] SERVICES = {
            // HOSPITALS
            {"National Hospital Colombo", "HOSPITAL",
                    "Regent St, Colombo 00800", "Western", "Colombo",
                    6.9271, 79.8612, "011-2691111", "011-2685401", true,
                    "Largest public hospital in Sri Lanka"},

            {"Colombo South Teaching Hospital", "HOSPITAL",
                    "Kalubowila, Dehiwala", "Western", "Colombo",
                    6.8649, 79.8735, "011-2717222", null, true,
                    "Major teaching hospital south of Colombo"},

            {"Kandy Teaching Hospital", "HOSPITAL",
                    "Sangaraja Mawatha, Kandy", "Central", "Kandy",
                    7.2906, 80.6337, "081-2223337", "081-2234401", true,
                    "Main referral hospital for Central Province"},

            {"Galle Teaching Hospital", "HOSPITAL",
                    "Galle", "Southern", "Galle",
                    6.0535, 80.2210, "091-2234561", null, true,
                    "Main hospital for Southern Province"},

            {"Jaffna Teaching Hospital", "HOSPITAL",
                    "Hospital Rd, Jaffna", "Northern", "Jaffna",
                    9.6615, 80.0255, "021-2222261", null, true,
                    "Main hospital for Northern Province"},

            {"Anuradhapura Teaching Hospital", "HOSPITAL",
                    "Maithripala Senanayake Mawatha, Anuradhapura",
                    "North Central", "Anuradhapura",
                    8.3114, 80.4037, "025-2222261", null, true,
                    "Main hospital for North Central Province"},

            {"Trincomalee General Hospital", "HOSPITAL",
                    "Inner Harbour Rd, Trincomalee", "Eastern", "Trincomalee",
                    8.5874, 81.2152, "026-2222261", null, true,
                    "Main hospital for Eastern Province"},

            {"Matara Teaching Hospital", "HOSPITAL",
                    "Uyanwatta, Matara", "Southern", "Matara",
                    5.9549, 80.5550, "041-2222261", null, true,
                    "Main hospital for Matara District"},

            {"Negombo General Hospital", "HOSPITAL",
                    "Hospital Rd, Negombo", "Western", "Gampaha",
                    7.2083, 79.8358, "031-2222261", null, true,
                    "Convenient for Bandaranaike airport area"},

            // POLICE
            {"Colombo Fort Police Station", "POLICE",
                    "Lotus Rd, Colombo Fort", "Western", "Colombo",
                    6.9366, 79.8503, "011-2433333", null, true,
                    "Main police station, Colombo city centre"},

            {"Tourist Police Colombo", "POLICE",
                    "Sir Baron Jayatilleka Mawatha, Colombo 1",
                    "Western", "Colombo",
                    6.9271, 79.8500, "011-2421451", "1912", true,
                    "Dedicated tourist assistance — call 1912"},

            {"Tourist Police Kandy", "POLICE",
                    "Dalada Veediya, Kandy", "Central", "Kandy",
                    7.2953, 80.6356, "081-2234444", "1912", true,
                    "Tourist police near Temple of the Tooth"},

            {"Tourist Police Galle", "POLICE",
                    "Galle Fort", "Southern", "Galle",
                    6.0328, 80.2170, "091-2234567", "1912", true,
                    "Tourist police inside Galle Fort"},

            {"Negombo Police Station", "POLICE",
                    "Negombo", "Western", "Gampaha",
                    7.2094, 79.8381, "031-2222222", null, true,
                    "Close to Bandaranaike International Airport"},

            {"Sigiriya Police Station", "POLICE",
                    "Sigiriya", "Central", "Matale",
                    7.9570, 80.7603, "066-2286222", null, true,
                    "Police station near Sigiriya rock fortress"},

            {"Nuwara Eliya Police Station", "POLICE",
                    "New Bazaar St, Nuwara Eliya", "Central", "Nuwara Eliya",
                    6.9497, 80.7891, "052-2222222", null, true,
                    "Police station in hill country"},

            {"Hikkaduwa Police Station", "POLICE",
                    "Hikkaduwa", "Southern", "Galle",
                    6.1395, 80.1060, "091-2277222", null, true,
                    "Police near popular beach area"},

            // FIRE
            {"Colombo Fire Brigade", "FIRE",
                    "Aluthmawatha Rd, Colombo 15", "Western", "Colombo",
                    6.9565, 79.8731, "011-2422222", "110", true,
                    "Main fire brigade, Colombo"},

            {"Kandy Fire Station", "FIRE",
                    "Yatinuwara Veediya, Kandy", "Central", "Kandy",
                    7.2920, 80.6350, "081-2222222", "110", true,
                    "Central fire station, Kandy"},

            // PHARMACY (24h)
            {"Osu Sala 24h Pharmacy Colombo", "PHARMACY",
                    "York St, Colombo 01", "Western", "Colombo",
                    6.9344, 79.8481, "011-2320600", null, true,
                    "Government 24-hour pharmacy, Fort"},
    };

    @Override
    public void run(ApplicationArguments args) {
        if (emergencyServiceRepository.count() > 0) {
            log.info("Emergency Service already seeded. Skipping...");
            return;
        }

        log.info("Seeding Sri Lanka Emergency Services...");

        for (Object[] s : SERVICES) {
            jdbcTemplate.update("""
                            INSERT INTO emergency_services
                              (id, name, type, address, province, district,
                               latitude, longitude, phone, phone2, is_24h, notes, created_at)
                            VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
                            """,
                    UUID.randomUUID().toString(),
                    s[0], s[1], s[2], s[3], s[4],
                    s[5], s[6], s[7], s[8], s[9], s[10]
            );
        }

        log.info("Emergency services seeded: {} entries.", SERVICES.length);

    }
}
