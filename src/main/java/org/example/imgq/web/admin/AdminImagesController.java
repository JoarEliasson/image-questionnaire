package org.example.imgq.web.admin;

import org.example.imgq.domain.Image;
import org.example.imgq.repo.ImageRepository;
import org.example.imgq.repo.StudyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.example.imgq.service.FileStorageService;

import java.util.UUID;

@Controller
@RequestMapping("/admin/studies/{studyId}/images")
public class AdminImagesController {

    private final ImageRepository images;
    private final StudyRepository studies;
    private final FileStorageService storage;

    public AdminImagesController(ImageRepository images, StudyRepository studies, FileStorageService storage) {
        this.images = images; this.studies = studies; this.storage = storage;
    }

    @GetMapping
    public String list(@PathVariable UUID studyId, Model model) {
        model.addAttribute("study", studies.findById(studyId).orElseThrow());
        model.addAttribute("items", images.findByStudyIdOrderByOrderIndexAsc(studyId));
        return "admin/images/list";
    }

    @GetMapping("/new")
    public String formNew(@PathVariable UUID studyId, Model model) {
        Image img = new Image();
        img.setStudyId(studyId);
        model.addAttribute("image", img);
        model.addAttribute("studyId", studyId);
        return "admin/images/form";
    }

    @PostMapping
    public String create(@PathVariable UUID studyId,
                         @ModelAttribute Image image,
                         @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        image.setStudyId(studyId);
        if (file != null && !file.isEmpty()) {
            image.setStoragePath(storage.saveToUploads(file));
        }
        images.save(image);
        return "redirect:/admin/studies/" + studyId + "/images";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID studyId, @PathVariable UUID id,
                         @ModelAttribute Image image,
                         @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        image.setId(id);
        image.setStudyId(studyId);
        if (file != null && !file.isEmpty()) {
            image.setStoragePath(storage.saveToUploads(file));
        }
        images.save(image);
        return "redirect:/admin/studies/" + studyId + "/images";
    }

    @GetMapping("/{id}/edit")
    public String formEdit(@PathVariable UUID studyId, @PathVariable UUID id, Model model) {
        model.addAttribute("image", images.findById(id).orElseThrow());
        model.addAttribute("studyId", studyId);
        return "admin/images/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID studyId, @PathVariable UUID id, @ModelAttribute Image image) {
        image.setId(id);
        image.setStudyId(studyId);
        images.save(image);
        return "redirect:/admin/studies/" + studyId + "/images";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID studyId, @PathVariable UUID id) {
        images.deleteById(id);
        return "redirect:/admin/studies/" + studyId + "/images";
    }
}
