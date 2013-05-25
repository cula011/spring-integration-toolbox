package org.cula011.springintegration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-integration-test.xml")
public class SpringIntegrationTest
{
    @Autowired
    MessageChannel orderInChannel;

    @Autowired
    QueueChannel testChannel;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringIntegrationTest.class);

    @Test
    public void messageFlowShouldCorrectlySplitAndAggregateOrderRequest()
    {
        new OrderGenerator(3).submitOrder();
        Message orderMessage = testChannel.receive();
        assertThat(orderMessage.getPayload(), instanceOf(OrderRequestType.class));
        OrderRequestType order = (OrderRequestType) orderMessage.getPayload();
        assertThat(order, notNullValue());
        LOGGER.debug("<-<-<-<-<-<- Received Order: {}", order);
        // TODO: Add further assertions to check individual items.
    }

    private class OrderGenerator
    {
        private final int itemCount;
        private int orderCount;

        public OrderGenerator(int itemCount)
        {
            this.itemCount = itemCount;
        }

        public void submitOrder()
        {
            OrderRequestType order = new OrderRequestType();
            order.setOrderId(orderCount);
            order.getItem().addAll(createOrderItems());
            order.setShippingAddress(createOrderAddress());
            LOGGER.debug("->->->->->-> Sending order {}", orderCount);
            orderInChannel.send(MessageBuilder.withPayload(order).build());
            orderCount++;
        }

        private List<ItemType> createOrderItems()
        {
            List<ItemType> items = new ArrayList<ItemType>();
            for (int itemNumber=1; itemNumber<= itemCount; itemNumber++)
            {
                ItemType item = new ItemType();
                int itemId = Integer.valueOf(String.format("%d%d", orderCount, itemNumber));
                LOGGER.debug("Creating item {} for order {}", itemId, orderCount);
                item.setId(itemId);
                item.setName("Item_" + itemNumber);
                item.setDescription("This is item " + itemNumber);
                item.setCost(10.0);
                items.add(item);
            }
            return items;
        }

        private AddressType createOrderAddress()
        {
            AddressType address = new AddressType();
            address.setName("John Doe");
            address.setAddress("1 Unknown Road");
            address.setCity("Someplace");
            address.setCountry("Else");
            return address;
        }
    }
}