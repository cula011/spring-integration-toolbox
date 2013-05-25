package org.cula011.springintegration.aggregate;

import org.cula011.springintegration.ItemType;
import org.cula011.springintegration.OrderRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Aggregator;

import java.util.List;

public class OrderItemAggregator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderItemAggregator.class);

    @Aggregator
    public OrderRequestType aggregateOrderItems(List<ItemType> items)
    {
        LOGGER.debug("Aggregating OrderItems: " + items.toString());
        OrderRequestType order = new OrderRequestType();
        order.getItem().addAll(items);
        return order;
    }
}