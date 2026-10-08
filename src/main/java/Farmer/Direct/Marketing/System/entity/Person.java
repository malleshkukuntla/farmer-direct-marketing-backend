package Farmer.Direct.Marketing.System.entity;

import jakarta.persistence.*;

// Shared fields of Farmer and Customer (no address, no pincode)
@MappedSuperclass
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String mobile;
    @Column(unique = true)
    public String email;
    public String password;
    public String state;
    public String district;
    public String mandal;
    public String village;
}
