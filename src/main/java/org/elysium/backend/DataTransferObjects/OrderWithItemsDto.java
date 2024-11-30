package org.elysium.backend.DataTransferObjects;
import java.util.List;

import org.elysium.backend.models.*;

public class OrderWithItemsDto {
    private Order order;
    private List<OrderItem> orderItems;

    public OrderWithItemsDto(Order order, List<OrderItem> orderItems) {
        this.order = order;
        this.orderItems = orderItems;
    }

    public Order getOrder(){
        return order;
    }

    public void setOrder(Order order){
        this.order = order;
    }

    public List<OrderItem> getOrderItems(){
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems){
        this.orderItems = orderItems;
    }
}
