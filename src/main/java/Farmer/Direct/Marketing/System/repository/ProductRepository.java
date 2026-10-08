package Farmer.Direct.Marketing.System.repository;

import Farmer.Direct.Marketing.System.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFarmerId(Long farmerId);
}
