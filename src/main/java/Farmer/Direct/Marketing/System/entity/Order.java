package Farmer.Direct.Marketing.System.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long customerId;
    public String customerName;
    public String customerPhone;
    public String customerVillage;
    public String customerMandal;
    public String customerDistrict;
    public String customerState;

    public Long productId;
    public String productName;
    public String productImage;

    public Long farmerId;
    public String farmerName;

    public int quantity;
    public double price;
    public double totalPrice;
    public String orderDate;
    public String status; // PENDING, ACCEPTED, REJECTED, CANCELLED

    // Not stored in DB. Filled in before sending to the frontend.
    @Transient
    public String farmerLocation;
}
