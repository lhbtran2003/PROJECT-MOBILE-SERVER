package liliana.serverreactnative.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;


    @Column(unique = true, nullable = false)
    private String categoryName;


    private String description;
    private String imageUrl;


    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

}
