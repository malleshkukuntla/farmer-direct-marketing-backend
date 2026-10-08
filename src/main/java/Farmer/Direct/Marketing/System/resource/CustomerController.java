package Farmer.Direct.Marketing.System.resource;

import Farmer.Direct.Marketing.System.entity.Customer;
import Farmer.Direct.Marketing.System.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository customers;

    public CustomerController(CustomerRepository customers) {
        this.customers = customers;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Customer c) {
        Check.clean(c);
        Check.person(c);
        if (customers.existsByEmail(c.email)) {
            throw new ApiException(409, "Email already registered");
        }
        c.id = null;
        return Check.safe(customers.save(c), "customer");
    }

    @GetMapping
    public List<Map<String, Object>> all() {
        return customers.findAll().stream().map(c -> Check.safe(c, "customer")).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> one(@PathVariable("id") Long id) {
        Customer c = customers.findById(id).orElseThrow(() -> new ApiException(404, "Customer not found"));
        return Check.safe(c, "customer");
    }
}
