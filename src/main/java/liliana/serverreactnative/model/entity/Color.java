package liliana.serverreactnative.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "colors")
@Getter
@Setter
@NoArgsConstructor
class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer colorId;


    @Column(nullable = false)
    private String colorName;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
