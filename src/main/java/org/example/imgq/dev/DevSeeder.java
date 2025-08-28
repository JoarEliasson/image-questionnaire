package org.example.imgq.dev;

import org.example.imgq.domain.*;
import org.example.imgq.repo.*;
import org.example.imgq.service.Hashing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("!prod")
public class DevSeeder implements CommandLineRunner {

    private final InvitationRepository invitations;
    private final StudyRepository studies;
    private final QuestionSetRepository questionSets;
    private final QuestionRepository questionRepo;
    private final ImageRepository imageRepo;

    public DevSeeder(InvitationRepository invitations,
                     StudyRepository studies,
                     QuestionSetRepository questionSets,
                     QuestionRepository questionRepo,
                     ImageRepository imageRepo) {
        this.invitations = invitations;
        this.studies = studies;
        this.questionSets = questionSets;
        this.questionRepo = questionRepo;
        this.imageRepo = imageRepo;
    }

    @Override
    public void run(String... args) {
        UUID studyId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // 1) Study (version 1)
        if (!studies.existsById(studyId)) {
            Study s = new Study();
            s.setId(studyId);
            s.setName("Demo Study");
            s.setDescription("Seeded demo study for local dev");
            s.setQuestionSetVersion(1);
            studies.save(s);
            System.out.println("[DevSeeder] Created demo study: " + studyId);
        }

        // 2) Question set v1 with 15 questions if missing
        if (!questionSets.existsById(1)) {
            QuestionSet qs = new QuestionSet();
            qs.setVersion(1);
            qs.setTitle("Default 15");
            qs.setCreatedAt(OffsetDateTime.now());
            qs.setLocked(false);
            questionSets.save(qs);

            for (int i = 1; i <= 15; i++) {
                Question q = new Question();
                q.setQuestionSetVersion(1);
                q.setIndexInSet(i);
                q.setText("Question " + i + " — rate on the slider");
                q.setMinLabel("Low");
                q.setMaxLabel("High");
                questionRepo.save(q);
            }
            System.out.println("[DevSeeder] Seeded question set v1 with 15 questions");
        }

        // 3) Images for the study (2 demo rows if none)
        List<Image> existingImages = imageRepo.findByStudyIdOrderByOrderIndexAsc(studyId);
        if (existingImages.isEmpty()) {
            Image img1 = new Image();
            img1.setStudyId(studyId);
            img1.setStoragePath("/images/demo1.jpg");
            img1.setOrderIndex(1);
            img1.setLabel("Demo 1");
            imageRepo.save(img1);

            Image img2 = new Image();
            img2.setStudyId(studyId);
            img2.setStoragePath("/images/demo2.jpg");
            img2.setOrderIndex(2);
            img2.setLabel("Demo 2");
            imageRepo.save(img2);

            System.out.println("[DevSeeder] Seeded 2 demo images (order_index 1..2)");
        }

        // 4) Demo invitation
        String code = "DEMO-CODE-123";
        String hash = Hashing.sha256Hex(code);
        Optional<Invitation> existing = invitations.findByCodeHash(hash);
        if (existing.isEmpty()) {
            Invitation inv = new Invitation();
            inv.setStudyId(studyId);
            inv.setEmail("demo@example.org");
            inv.setCodeHash(hash);
            inv.setStatus("sent");
            inv.setCreatedAt(OffsetDateTime.now());
            inv.setSentAt(OffsetDateTime.now());
            invitations.save(inv);
            System.out.println("[DevSeeder] Created demo invitation with code: " + code);
        } else {
            System.out.println("[DevSeeder] Demo invitation already exists.");
        }
    }
}
