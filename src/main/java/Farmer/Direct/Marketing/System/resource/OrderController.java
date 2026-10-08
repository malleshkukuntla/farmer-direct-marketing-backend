package Farmer.Direct.Marketing.System.resource;

import Farmer.Direct.Marketing.System.entity.Customer;
import Farmer.Direct.Marketing.System.entity.Farmer;
import Farmer.Direct.Marketing.System.entity.Order;
import Farmer.Direct.Marketing.System.entity.Product;
import Farmer.Direct.Marketing.System.repository.CustomerRepository;
import Farmer.Direct.Marketing.System.repository.FarmerRepository;
import Farmer.Direct.Marketing.System.repository.OrderRepository;
import Farmer.Direct.Marketing.System.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orders;
    private final ProductRepository products;
    private final FarmerRepository farmers;
    private final CustomerRepository customers;

    public OrderController(OrderRepository orders, ProductRepository products,
                           FarmerRepository farmers, CustomerRepository customers) {
        this.orders = orders;
        this.products = products;
        this.farmers = farmers;
        this.customers = customers;
    }

    public static class PlaceRequest {
        public Long customerId;
        public Long productId;
        public int quantity;
    }

    private Order fill(Order o) {
        farmers.findById(o.farmerId).ifPresentOrElse(
                f -> o.farmerLocation = Check.location(f),
                () -> o.farmerLocation = "");
        return o;
    }

    // Puts the ordered quantity back into the product stock.
    // Called only when an order leaves the PENDING status, so it can never run twice.
    private void restoreStock(Order o) {
        products.findById(o.productId).ifPresent(p -> {
            p.quantity += o.quantity;
            products.save(p);
        });
    }

    // GET /api/orders?customerId=1   -> orders of one customer
    // GET /api/orders?farmerId=2     -> orders of one farmer
    // GET /api/orders                -> all orders (admin)
    @GetMapping
    public List<Order> list(@RequestParam(name = "customerId", required = false) Long customerId,
                            @RequestParam(name = "farmerId", required = false) Long farmerId) {
        List<Order> list;
        if (customerId != null) {
            list = orders.findByCustomerIdOrderByIdDesc(customerId);
        } else if (farmerId != null) {
            list = orders.findByFarmerIdOrderByIdDesc(farmerId);
        } else {
            list = orders.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        list.forEach(this::fill);
        return list;
    }

    @PostMapping
    @Transactional
    public Order place(@RequestBody PlaceRequest r) {
        if (r.customerId == null || r.productId == null) {
            throw new ApiException(400, "Customer and product are required");
        }
        Customer c = customers.findById(r.customerId)
                .orElseThrow(() -> new ApiException(404, "Customer not found"));
        Product p = products.findById(r.productId)
                .orElseThrow(() -> new ApiException(404, "Product not found"));
        Farmer f = farmers.findById(p.farmerId)
                .orElseThrow(() -> new ApiException(404, "Farmer not found"));
        if (r.quantity <= 0) {
            throw new ApiException(400, "Quantity must be at least 1");
        }
        if (r.quantity > p.quantity) {
            throw new ApiException(400, "Only " + p.quantity + " available in stock");
        }

        p.quantity -= r.quantity;
        products.save(p);

        Order o = new Order();
        o.customerId = c.id;
        o.customerName = c.name;
        o.customerPhone = c.mobile;
        o.customerVillage = c.village;
        o.customerMandal = c.mandal;
        o.customerDistrict = c.district;
        o.customerState = c.state;
        o.productId = p.id;
        o.productName = p.name;
        o.productImage = p.image;
        o.farmerId = f.id;
        o.farmerName = f.name;
        o.quantity = r.quantity;
        o.price = p.price;
        o.totalPrice = p.price * r.quantity;
        o.orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        o.status = "PENDING";
        return fill(orders.save(o));
    }

    // Customer cancels own PENDING order
    @PutMapping("/{id}/cancel")
    @Transactional
    public Order cancel(@PathVariable("id") Long id, @RequestParam("customerId") Long customerId) {
        Order o = orders.findById(id).orElseThrow(() -> new ApiException(404, "Order not found"));
        if (!o.customerId.equals(customerId)) {
            throw new ApiException(403, "You can cancel only your own orders");
        }
        if (!"PENDING".equals(o.status)) {
            throw new ApiException(400, "Only PENDING orders can be cancelled");
        }
        o.status = "CANCELLED";
        restoreStock(o);
        return fill(orders.save(o));
    }

    // Farmer accepts or rejects a PENDING order for his own product
    @PutMapping("/{id}/status")
    @Transactional
    public Order updateStatus(@PathVariable("id") Long id,
                              @RequestParam("farmerId") Long farmerId,
                              @RequestParam("status") String status) {
        Order o = orders.findById(id).orElseThrow(() -> new ApiException(404, "Order not found"));
        if (!o.farmerId.equals(farmerId)) {
            throw new ApiException(403, "You can update only orders of your own products");
        }
        if (!status.equals("ACCEPTED") && !status.equals("REJECTED")) {
            throw new ApiException(400, "Status must be ACCEPTED or REJECTED");
        }
        if (!"PENDING".equals(o.status)) {
            throw new ApiException(400, "Only PENDING orders can be updated");
        }
        o.status = status;
        if (status.equals("REJECTED")) {
            restoreStock(o);
        }
        return fill(orders.save(o));
    }
}
