package Farmer.Direct.Marketing.System.resource;

import Farmer.Direct.Marketing.System.entity.Order;
import Farmer.Direct.Marketing.System.repository.CustomerRepository;
import Farmer.Direct.Marketing.System.repository.FarmerRepository;
import Farmer.Direct.Marketing.System.repository.OrderRepository;
import Farmer.Direct.Marketing.System.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final FarmerRepository farmers;
    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrderRepository orders;

    public AdminController(FarmerRepository farmers, CustomerRepository customers,
                           ProductRepository products, OrderRepository orders) {
        this.farmers = farmers;
        this.customers = customers;
        this.products = products;
        this.orders = orders;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("farmers", farmers.count());
        m.put("customers", customers.count());
        m.put("products", products.count());
        m.put("orders", orders.count());
        return m;
    }

    @DeleteMapping("/farmers/{id}")
    @Transactional
    public Map<String, String> deleteFarmer(@PathVariable("id") Long id) {
        if (!farmers.existsById(id)) throw new ApiException(404, "Farmer not found");
        products.deleteAll(products.findByFarmerId(id)); // remove his products too
        farmers.deleteById(id);
        return Map.of("message", "Farmer deleted");
    }

    @DeleteMapping("/customers/{id}")
    public Map<String, String> deleteCustomer(@PathVariable("id") Long id) {
        if (!customers.existsById(id)) throw new ApiException(404, "Customer not found");
        customers.deleteById(id);
        return Map.of("message", "Customer deleted");
    }

    @DeleteMapping("/products/{id}")
    public Map<String, String> deleteProduct(@PathVariable("id") Long id) {
        if (!products.existsById(id)) throw new ApiException(404, "Product not found");
        products.deleteById(id);
        return Map.of("message", "Product deleted");
    }

    @DeleteMapping("/orders/{id}")
    @Transactional
    public Map<String, String> deleteOrder(@PathVariable("id") Long id) {
        Order o = orders.findById(id).orElseThrow(() -> new ApiException(404, "Order not found"));
        if ("PENDING".equals(o.status)) { // give the stock back before deleting
            products.findById(o.productId).ifPresent(p -> {
                p.quantity += o.quantity;
                products.save(p);
            });
        }
        orders.delete(o);
        return Map.of("message", "Order deleted");
    }
}
