package tn.esprit.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class TimesheetDevopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimesheetDevopsApplication.class, args);
    }

    @GetMapping("/echo")
    public String echo(@RequestParam String input) {
        return "<h1>" + input + "</h1>";
    }

    @RestController
    public class VulnerabilityTestController {

        @GetMapping("/echo")
        public String echo(@RequestParam String input) {
            return "<h1>" + input + "</h1>";
        }
    }

}
