package Farmer.Direct.Marketing.System.resource;

import Farmer.Direct.Marketing.System.entity.Product;
import Farmer.Direct.Marketing.System.repository.FarmerRepository;
import Farmer.Direct.Marketing.System.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository products;
    private final FarmerRepository farmers;

    public ProductController(ProductRepository products, FarmerRepository farmers) {
        this.products = products;
        this.farmers = farmers;
    }

    // adds farmer name + location to the product before sending it to the frontend
    private Product fill(Product p) {
        farmers.findById(p.farmerId).ifPresentOrElse(f -> {
            p.farmerName = f.name;
            p.farmerLocation = Check.location(f);
        }, () -> {
            p.farmerName = "Unknown farmer";
            p.farmerLocation = "";
        });
        return p;
    }

    private void validate(Product p, int minQuantity) {
        Check.required(p.name, "Product name");
        Check.required(p.category, "Category");
        if (p.quantity < minQuantity) {
            throw new ApiException(400, "Invalid quantity");
        }
        if (p.price <= 0) {
            throw new ApiException(400, "Invalid price. Price must be greater than 0");
        }
    }

    @GetMapping
    public List<Product> all(@RequestParam(name = "farmerId", required = false) Long farmerId) {
        List<Product> list = farmerId == null ? products.findAll() : products.findByFarmerId(farmerId);
        list.forEach(this::fill);
        return list;
    }

    @GetMapping("/{id}")
    public Product one(@PathVariable("id") Long id) {
        Product p = products.findById(id).orElseThrow(() -> new ApiException(404, "Product not found"));
        return fill(p);
    }

    @PostMapping
    public Product add(@RequestBody Product p) {
        validate(p, 1);
        if (p.farmerId == null || !farmers.existsById(p.farmerId)) {
            throw new ApiException(404, "Farmer not found");
        }
        p.id = null;
        p.name = p.name.trim();
        p.image = ImageUtil.forName(p.name);
        return fill(products.save(p));
    }

    @PutMapping("/{id}")
    public Product edit(@PathVariable("id") Long id, @RequestBody Product body) {
        Product p = products.findById(id).orElseThrow(() -> new ApiException(404, "Product not found"));
        if (body.farmerId == null || !p.farmerId.equals(body.farmerId)) {
            throw new ApiException(403, "You can edit only your own products");
        }
        validate(body, 0);
        p.name = body.name.trim();
        p.description = body.description;
        p.quantity = body.quantity;
        p.price = body.price;
        p.category = body.category;
        p.image = ImageUtil.forName(p.name); // image updates automatically when name changes
        return fill(products.save(p));
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable("id") Long id, @RequestParam("farmerId") Long farmerId) {
        Product p = products.findById(id).orElseThrow(() -> new ApiException(404, "Product not found"));
        if (!p.farmerId.equals(farmerId)) {
            throw new ApiException(403, "You can delete only your own products");
        }
        products.delete(p);
        return Map.of("message", "Product deleted");
    }
}
