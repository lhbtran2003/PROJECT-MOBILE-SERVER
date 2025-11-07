package liliana.serverreactnative.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
