package org.example.imgq.web.admin;

import org.example.imgq.repo.AuthLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/logs")
public class AdminAuthLogsController {

    private final AuthLogRepository logs;

    public AdminAuthLogsController(AuthLogRepository logs) {
        this.logs = logs;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", logs.findAll(Sort.by(Sort.Direction.DESC, "at")));
        return "admin/logs/list";
    }
}
