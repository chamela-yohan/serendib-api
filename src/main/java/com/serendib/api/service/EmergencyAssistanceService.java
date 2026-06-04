package com.serendib.api.service;

import com.serendib.api.common.AuthUtils;
import com.serendib.api.dto.request.SosRequest;
import com.serendib.api.dto.response.EmergencyServiceResponse;
import com.serendib.api.dto.response.SosResponse;
import com.serendib.api.entity.EmergencyService;
import com.serendib.api.entity.SosAlert;
import com.serendib.api.entity.User;
import com.serendib.api.repository.EmergencyServiceRepository;
import com.serendib.api.repository.SosAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyAssistanceService {
    private final EmergencyServiceRepository emergencyServiceRepository;
    private final SosAlertRepository sosAlertRepository;
    private final AuthUtils authUtils;

    private static final List<String> NATIONAL_EMERGENCY_NUMBERS = List.of(
            "Police: 119",
            "Ambulance: 110",
            "Fire Brigade: 111",
            "Tourist Police: 1912",
            "Disaster Management: 117",
            "Suwa Seriya (Ambulance): 1990"
    );

    private static final List<String> FIRST_AID_TIPS = List.of(
            "Stay calm and assess the situation before acting",
            "Call 110 (ambulance) or 119 (police) immediately for serious emergencies",
            "Do not move an injured person unless they are in immediate danger",
            "For bleeding: apply firm pressure with a clean cloth",
            "For choking: perform 5 back blows then 5 abdominal thrusts",
            "For unconscious person: check breathing and place in recovery position",
            "Keep the person warm and reassured until help arrives",
            "Tourist Police (1912) speak English and can coordinate help for visitors"
    );

    // Get national emergency contacts (no auth needed)
    public List<String> getNationalEmergencyNumbers() {
        return NATIONAL_EMERGENCY_NUMBERS;
    }

    // Find nearest services by GPS cordinate
    public List<EmergencyServiceResponse> findNearby(Double lat, Double lng, String type, int limit) {
        return emergencyServiceRepository
                .findNearest(lat, lng, type, limit)
                .stream()
                .map(EmergencyServiceResponse::from)
                .toList();
    }

    // Get services by province (useful offline)
    public List<EmergencyServiceResponse> findByProvince(String province, String type) {
        var services = (type != null && !type.isBlank())
                ? emergencyServiceRepository
                .findByProvinceIgnoreCaseAndType(province, type.toUpperCase())
                : emergencyServiceRepository.findByProvinceIgnoreCase(province);

        return services.stream()
                .map(EmergencyServiceResponse::from)
                .toList();
    }

    // SOS - save alert + return nearest help
    @Transactional
    public SosResponse triggerSos(SosRequest  request) {
        User user = authUtils.getCurrentUser();

        // Save the SOS alert
        SosAlert alert  = new SosAlert();
        alert.setUser(user);
        alert.setLatitude(request.getLatitude());
        alert.setLongitude(request.getLongitude());
        alert.setMessage(request.getMessage());
        alert = sosAlertRepository.save(alert);

        log.warn("SOS ALERT from user {} at [{}, {}]: {}",
                user.getEmail(),
                request.getLatitude(),
                request.getLongitude(),
                request.getMessage()
        );

        // Find nearest hospital and police
        EmergencyServiceResponse nearestHospital = null;
        EmergencyServiceResponse nearestPolice = null;

        if(request.getLatitude()!=null && request.getLongitude()!=null) {
            var hospitals = emergencyServiceRepository
                    .findNearest(request.getLatitude(), request.getLongitude(), "HOSPITAL", 1);
            var police = emergencyServiceRepository
                    .findNearest(request.getLatitude(), request.getLongitude(), "POLICE", 1);

            if (!hospitals.isEmpty())
                nearestHospital = EmergencyServiceResponse.from(hospitals.get(0));
            if (!police.isEmpty())
                nearestPolice = EmergencyServiceResponse.from(police.get(0));
        }

        return SosResponse.builder()
                .alertId(alert.getId())
                .status("ACTIVE")
                .nationalEmergencyNumbers(NATIONAL_EMERGENCY_NUMBERS)
                .nearestHospital(nearestHospital)
                .nearestPolice(nearestPolice)
                .firstAidTips(FIRST_AID_TIPS)
                .message("Emergency alert recorded. Call 119 (Police) or 110 (Ambulance) immediately.")
                .build();

    }


}
