package Farmer.Direct.Marketing.System.resource;

import Farmer.Direct.Marketing.System.repository.CustomerRepository;
import Farmer.Direct.Marketing.System.repository.FarmerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // Fixed admin credentials (simple, for a college project)
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    private final FarmerRepository farmers;
    private final CustomerRepository customers;

    public AuthController(FarmerRepository farmers, CustomerRepository customers) {
        this.farmers = farmers;
        this.customers = customers;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String role = body.getOrDefault("role", "");
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String password = body.getOrDefault("password", "");
        Check.required(email, "Email");
        Check.required(password, "Password");

        if (role.equals("admin")) {
            if (email.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", 0);
                m.put("name", "Admin");
                m.put("role", "admin");
                return m;
            }
            throw new ApiException(401, "Invalid admin username or password");
        }
               if (role.equals("farmer")) {
            var f = farmers.findByEmail(email)
                    .orElseThrow(() -> new ApiException(404, "Email not registered"));
            if (!f.password.equals(password)) {
                throw new ApiException(401, "Incorrect password");
            }
            return Check.safe(f, "farmer");
        }
        if (role.equals("customer")) {
            var c = customers.findByEmail(email)
                    .orElseThrow(() -> new ApiException(404, "Email not registered"));
            if (!c.password.equals(password)) {
                throw new ApiException(401, "Incorrect password");
            }
            return Check.safe(c, "customer");
        }
        throw new ApiException(400, "Please select a valid login type");
    }
}
