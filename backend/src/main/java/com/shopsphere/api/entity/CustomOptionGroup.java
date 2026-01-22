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
@Table(name = "custom_option_groups")
public class CustomOptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @ElementCollection
    @CollectionTable(name = "custom_option_items", joinColumns = @JoinColumn(name = "option_group_id"))
    private List<CustomOptionItem> options;
}
