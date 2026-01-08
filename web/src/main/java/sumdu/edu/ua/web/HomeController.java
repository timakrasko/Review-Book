package sumdu.edu.ua.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Перенаправлення з кореня "/" на "/books"
        return "redirect:/books";
    }
}
