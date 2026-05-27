package com.serendib.api.ai;

import com.serendib.api.entity.Trip;
import org.springframework.stereotype.Component;

@Component
public class ItineraryPromptBuilder {

    // Build the system prompt — tells AI who it is and how to behave
    public String buildSystemPrompt() {
        return """
        You are SerendibAI, an expert Sri Lanka travel planner with deep
        knowledge of Sri Lankan destinations, culture, food, transportation,
        weather patterns, and safety considerations.
        
        CRITICAL: You MUST respond with ONLY raw valid JSON.
        - NO markdown formatting
        - NO ```json blocks
        - NO explanation text before or after
        - NO comments inside JSON
        - ONLY the JSON object, nothing else
        
        Your recommendations must be:
        - Realistic and achievable within the given budget
        - Safe and appropriate for the traveler's health conditions
        - Culturally sensitive and authentic
        - Specific to Sri Lanka (real places, real prices in USD)
        """;
    }

    // Build the user prompt with all trip preferences
    public String buildUserPrompt(Trip trip) {

        // Format health conditions nicely
        String healthInfo = trip.getHealthConditions() != null
                ? trip.getHealthConditions().toString()
                : "No specific health conditions";

        String foodInfo = trip.getFoodPreferences() != null
                ? String.join(", ", trip.getFoodPreferences())
                : "No specific preferences";

        return """
            Generate a complete %d-day Sri Lanka travel itinerary with these details:
            
            TRAVELER PROFILE:
            - Budget: $%.2f USD total
            - Group: %s
            - Travel Style: %s
            - Adventure Level: %s
            - Starting Location: %s
            - Food Preferences: %s
            - Health Conditions: %s
            
            REQUIREMENTS:
            - Keep total cost within $%.2f budget
            - Suggest activities appropriate for %s adventure level
            - Account for health conditions when suggesting activities
            - Include realistic Sri Lankan prices
            - Mix of popular and hidden gem locations
            - Include local food experiences
            
            Respond with this EXACT JSON structure:
            {
              "days": [
                {
                  "dayNumber": 1,
                  "theme": "string",
                  "places": [
                    {
                      "name": "string",
                      "description": "string",
                      "location": "string",
                      "visitDurationMinutes": 90,
                      "bestTimeToVisit": "string",
                      "entranceFee": 5.00,
                      "safetyNote": "string or null"
                    }
                  ],
                  "hotel": {
                    "name": "string",
                    "area": "string",
                    "type": "string",
                    "pricePerNight": 25.00,
                    "highlights": ["string"]
                  },
                  "meals": ["Restaurant name - dish name"],
                  "transport": "string",
                  "estimatedDayCost": 50.00,
                  "tips": ["string"]
                }
              ],
              "estimatedTotalCost": 350.00,
              "packingSuggestions": ["string"],
              "generalTips": ["string"]
            }
            """.formatted(
                trip.getNumberOfDays(),
                trip.getBudgetUsd(),
                trip.getGroupType(),
                trip.getTravelStyle(),
                trip.getAdventureLevel(),
                trip.getStartLocation(),
                foodInfo,
                healthInfo,
                trip.getBudgetUsd(),
                trip.getAdventureLevel()
        );
    }
}