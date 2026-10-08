package Farmer.Direct.Marketing.System.repository;

import Farmer.Direct.Marketing.System.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByIdDesc(Long customerId);
    List<Order> findByFarmerIdOrderByIdDesc(Long farmerId);
}
