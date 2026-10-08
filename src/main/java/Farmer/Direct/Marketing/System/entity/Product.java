package Farmer.Direct.Marketing.System.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public Long id;
public String name;
@Column(length = 1000)
public String description;
public int quantity;
public String unit;
public double price;
public String category;
public Long farmerId;
public String image;

@Transient
public String farmerName;
@Transient
public String farmerLocation;

}