package com.shopsphere.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    private String productId; // Using the string ID from frontend

    private String name;

    @Column(length = 1000)
    private String description;

    private String category;

    private Double basePrice;

    private String previewImage;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    @Builder.Default
    private List<CustomOptionGroup> customOptions = new java.util.ArrayList<>();

    private Boolean isActive;
}
