package liliana.serverreactnative.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(nullable = false)
    private BigDecimal totalPrice;


    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;


    private String shippingAddress;


    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}