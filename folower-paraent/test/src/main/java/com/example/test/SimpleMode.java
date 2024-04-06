package com.example.test;

import com.rabbitmq.client.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SimpleMode {
    private Channel channel;

    @Before
    public void initChannel() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.243.133");
        factory.setPort(5672);
        factory.setUsername("guset");
        factory.setPassword("guset");
        factory.setVirtualHost("/");

        Connection conn = factory.newConnection();
        channel = conn.createChannel();

    }

    @Test
    public void send() throws Exception{
        String msg = "hello,kamicg";
        String routingKey = "shenzhen";

        channel.queueDeclare("shenzhen", false, false, false, null);
        channel.basicPublish("", routingKey, null, msg.getBytes());
    }

    @Test
    public void consumer() throws Exception {
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume("foshan", consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            System.out.println(new String(delivery.getBody()));
        }
    }

}
