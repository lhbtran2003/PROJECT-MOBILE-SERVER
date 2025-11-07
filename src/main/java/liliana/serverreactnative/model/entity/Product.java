package liliana.serverreactnative.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;


    @Column(nullable = false)
    private String productName;


    private String description;


    @Column(nullable = false)
    private BigDecimal price;


    @Column(nullable = false)
    private Integer stockQuantity;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    private String imageUrl;


    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}