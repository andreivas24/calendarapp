package app.calendar.user.presentation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/user")
public class UserResource {
    @GetMapping("/default")
    public String defaultTest() {
        return "Working";
    }
}