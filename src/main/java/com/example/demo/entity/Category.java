package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NamedQueries({
    @NamedQuery(
        name = "Category.findCategoriesCreatedAfter",
        query = "SELECT c FROM Category c WHERE c.createdAt > :date AND c.isActive = true ORDER BY c.createdAt DESC"
    ),
    @NamedQuery(
        name = "Category.findCategoriesWithMinProducts",
        query = "SELECT c FROM Category c WHERE c.isActive = true AND " +
                "c.id IN (SELECT p.category.id FROM Product p WHERE p.isActive = true GROUP BY p.category.id HAVING COUNT(p.id) >= :minCount)"
    )
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // One-to-Many relationship với Product
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    
    // // Helper methods
    // public void addProduct(Product product) {
    //     products.add(product);
    //     product.setCategory(this);
    // }
    
    // public void removeProduct(Product product) {
    //     products.remove(product);
    //     product.setCategory(null);
    // }
}
