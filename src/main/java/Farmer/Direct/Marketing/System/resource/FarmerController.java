package Farmer.Direct.Marketing.System.resource;

import Farmer.Direct.Marketing.System.entity.Farmer;
import Farmer.Direct.Marketing.System.repository.FarmerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmers")
public class FarmerController {
    private final FarmerRepository farmers;

    public FarmerController(FarmerRepository farmers) {
        this.farmers = farmers;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Farmer f) {
        Check.clean(f);
        Check.person(f);
        if (farmers.existsByEmail(f.email)) {
            throw new ApiException(409, "Email already registered");
        }
        f.id = null;
        return Check.safe(farmers.save(f), "farmer");
    }

    @GetMapping
    public List<Map<String, Object>> all() {
        return farmers.findAll().stream().map(f -> Check.safe(f, "farmer")).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> one(@PathVariable("id") Long id) {
        Farmer f = farmers.findById(id).orElseThrow(() -> new ApiException(404, "Farmer not found"));
        return Check.safe(f, "farmer");
    }
}
