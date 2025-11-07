package liliana.serverreactnative.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sizes")
@Getter
@Setter
@NoArgsConstructor
class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sizeId;


    @Column(nullable = false)
    private String sizeName;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}