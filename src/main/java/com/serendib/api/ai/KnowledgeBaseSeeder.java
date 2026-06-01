package com.serendib.api.ai;

import com.pgvector.PGvector;
import com.serendib.api.entity.KnowledgeBase;
import com.serendib.api.repository.KnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.List;
import java.util.UUID;

// ApplicationRunner = runs automatically when Spring Boot starts
@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseSeeder implements ApplicationRunner {

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final GoogleEmbeddingClient embeddingClient;
    private final JdbcTemplate jdbcTemplate;

    // Sri Lanka travel knowledge
    // In a real app, this would come from a CMS or file
    private static final List<String[]> KNOWLEDGE = List.of(
            // format: { content, category }
            new String[]{
                    "Ella is a scenic hill town best visited from December to March. " +
                            "The famous Nine Arch Bridge is a must-see. " +
                            "Hiking Little Adam's Peak takes about 2 hours and is suitable " +
                            "for moderate fitness levels. Ella Rock is more challenging.",
                    "destinations"
            },
            new String[]{
                    "Sigiriya Rock Fortress requires climbing 1,200 steps. " +
                            "Not recommended for people with heart conditions, severe asthma, " +
                            "or fear of heights. Best visited early morning before 8am " +
                            "to avoid heat. Entrance fee is around $30 USD for foreigners.",
                    "safety"
            },
            new String[]{
                    "Sri Lanka has two monsoon seasons. Southwest monsoon: May-September " +
                            "affects Colombo, Galle, and western coast. Northeast monsoon: " +
                            "October-January affects Trincomalee and east coast. " +
                            "December to March is the best time to visit most of Sri Lanka.",
                    "weather"
            },
            new String[]{
                    "Must-try Sri Lankan foods: Rice and curry (staple meal), " +
                            "Hoppers (bowl-shaped pancakes), Kottu Roti (chopped flatbread stir-fry), " +
                            "String hoppers, Lamprais (Dutch-influenced rice dish), " +
                            "Fresh seafood in coastal areas. Vegetarian options widely available.",
                    "food"
            },
            new String[]{
                    "Getting around Sri Lanka: Tuk-tuks are cheap for short distances " +
                            "(negotiate price first, around 50-100 LKR per km). " +
                            "Trains are scenic and affordable especially Colombo-Kandy-Ella route. " +
                            "AC buses connect major cities. Uber works in Colombo. " +
                            "Private drivers cost $40-80 USD per day but offer flexibility.",
                    "transport"
            },
            new String[]{
                    "Colombo highlights: Gangaramaya Temple, Galle Face Green promenade, " +
                            "Pettah market for local shopping, Dutch Hospital for dining, " +
                            "National Museum, Viharamahadevi Park. " +
                            "Best areas to stay: Colombo 3 (Kollupitiya) and Colombo 7 (Cinnamon Gardens).",
                    "destinations"
            },
            new String[]{
                    "Galle Fort is a UNESCO World Heritage Site. " +
                            "Best explored on foot. Full walk takes 2-3 hours. " +
                            "Great for photography especially at sunset from the fort walls. " +
                            "Nearby Unawatuna beach is popular for swimming. " +
                            "Mirissa (40km east) is best for whale watching November to April.",
                    "destinations"
            },
            new String[]{
                    "Safety in Sri Lanka: Generally safe for tourists. " +
                            "Petty theft can occur in crowded areas, keep valuables secure. " +
                            "Avoid swimming at unflagged beaches due to strong currents. " +
                            "Political situation stable since 2022 economic recovery. " +
                            "Emergency number: 119 (Police), 110 (Ambulance).",
                    "safety"
            },
            new String[]{
                    "Budget travel in Sri Lanka: Guesthouses cost $10-25/night. " +
                            "Local meals cost $1-3. Tuk-tuk rides $0.50-2. " +
                            "Daily budget traveler can manage on $30-50/day including " +
                            "accommodation, food, and transport. " +
                            "Comfort traveler should budget $80-150/day.",
                    "budget"
            },
            new String[]{
                    "Kandy: Home of the Sacred Tooth Relic Temple (Dalada Maligawa). " +
                            "Perahera festival in July/August is spectacular but very crowded. " +
                            "Kandy Lake walk is peaceful. Royal Botanical Gardens at Peradeniya " +
                            "is excellent. 3 hours by train from Colombo, very scenic journey.",
                    "destinations"
            },
            new String[]{
                    "Adam's Peak (Sri Pada): Sacred pilgrimage site, 5,558 steps. " +
                            "Climbing season: December to May. Night hike recommended " +
                            "to reach summit at sunrise. Takes 3-5 hours to climb. " +
                            "Not suitable for people with asthma, heart conditions, " +
                            "or very low fitness. Carry warm clothes for summit.",
                    "safety"
            },
            new String[]{
                    "Sri Lankan health tips: Drink bottled water only. " +
                            "Mosquito repellent essential, especially in low-lying areas. " +
                            "Sun protection critical - UV index very high. " +
                            "Travel insurance strongly recommended. " +
                            "Nearest hospitals: Colombo National Hospital (011-2691111), " +
                            "Kandy General Hospital (081-2222261).",
                    "health"
            }
    );

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        if (knowledgeBaseRepository.count() > 0) {
            log.info("Knowledge base already seeded. Skipping.");
            return;
        }

        log.info("Seeding Sri Lanka knowledge base...");

        for (String[] item : KNOWLEDGE) {
            String content  = item[0];
            String category = item[1];

            float[] embedding = embeddingClient.embed(content);

            // Convert float[] to "[0.1,0.2,...]" string
            String vectorStr = "[" +
                    IntStream.range(0, embedding.length)
                            .mapToObj(i -> String.valueOf(embedding[i]))
                            .collect(Collectors.joining(",")) +
                    "]";

            // Use JdbcTemplate with explicit ::vector cast
            // This bypasses Hibernate's type system entirely
            jdbcTemplate.update(
                    """
                    INSERT INTO knowledge_base (id, content, category, source, embedding, created_at)
                    VALUES (?::uuid, ?, ?, ?, ?::vector, NOW())
                    """,
                    UUID.randomUUID().toString(),
                    content,
                    category,
                    "serendib-curated",
                    vectorStr
            );

            log.info("Seeded: [{}] {}...", category, content.substring(0, 50));
        }

        log.info("Knowledge base seeding complete! {} entries.", KNOWLEDGE.size());
    }
}