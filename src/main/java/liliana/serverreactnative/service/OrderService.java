package liliana.serverreactnative.service;

import liliana.serverreactnative.config.exception.AccessDeniedException;
import liliana.serverreactnative.config.exception.BadRequestException;
import liliana.serverreactnative.config.exception.NotFoundException;
import liliana.serverreactnative.model.dto.request.OrderRequest;
import liliana.serverreactnative.model.dto.response.OrderItemResponse;
import liliana.serverreactnative.model.dto.response.OrderResponse;
import liliana.serverreactnative.model.entity.*;
import liliana.serverreactnative.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;


    private User getCurrentAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Lỗi xác thực người dùng."));
    }

    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getProductId())
                .productName(item.getProduct().getProductName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }

    private OrderResponse mapOrderToResponse(Order order) {
        List<OrderItemResponse> itemsResponse = order.getOrderItems() != null ?
                order.getOrderItems().stream()
                        .map(this::mapOrderItemToResponse)
                        .collect(Collectors.toList()) : null;

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .status(order.getOrderStatus())
                .totalAmount(order.getTotalPrice())
                .orderDate(order.getCreatedAt())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .shippingAddress(order.getShippingAddress())
                .items(itemsResponse)
                .build();
    }

    // Tạo đơn hàng
    public OrderResponse createOrder(OrderRequest request) {
        User user = getCurrentAuthenticatedUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Giỏ hàng của bạn không tồn tại."));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Giỏ hàng rỗng, không thể tạo đơn hàng.");
        }


        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(cartItem -> cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());

                    return orderItem;
                })
                .collect(Collectors.toList());

        // 3. Tạo Order
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setTotalPrice(totalPrice);
        newOrder.setReceiverName(request.getReceiverName());
        newOrder.setReceiverPhone(request.getReceiverPhone());
        newOrder.setShippingAddress(request.getShippingAddress());
        newOrder.setOrderItems(orderItems);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());

        orderItems.forEach(item -> item.setOrder(newOrder));
        Order savedOrder = orderRepository.save(newOrder);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapOrderToResponse(savedOrder);
    }

    //2. Lấy danh sách đơn hàng của người dùng (có thể lọc theo trạng thái)
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(String statusFilter) {
        User user = getCurrentAuthenticatedUser();
        List<Order> orders;

        if (statusFilter != null && !statusFilter.isEmpty()) {
            try {
                OrderStatus status = OrderStatus.valueOf(statusFilter.toUpperCase());
                orders = orderRepository.findByUserAndOrderStatusOrderByCreatedAtDesc(user, status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Trạng thái đơn hàng không hợp lệ.");
            }
        } else {
            orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return orders.stream()
                .map(order -> {
                    OrderResponse response = mapOrderToResponse(order);
                    response.setItems(null);
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Hiển thị chi tiết đơn hàng
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Integer orderId) {
        User user = getCurrentAuthenticatedUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại."));

        // Kiểm tra quyền sở hữu
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này.");
        }

        return mapOrderToResponse(order);
    }

   //  Hủy đơn hàng (Chỉ cho phép hủy khi trạng thái là PENDING)
    public OrderResponse cancelOrder(Integer orderId) {
        User user = getCurrentAuthenticatedUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại."));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền hủy đơn hàng này.");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Không thể hủy đơn hàng này vì nó đang ở trạng thái: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        return mapOrderToResponse(updatedOrder);
    }
}