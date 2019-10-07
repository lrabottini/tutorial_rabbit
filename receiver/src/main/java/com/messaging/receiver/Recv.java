package com.messaging.receiver;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Recv {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Message durability
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        
        // Fair dispatch
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            System.out.println(" [x] Received '" + message + "'");
            try{
                doWork(message);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                System.out.println(" [x] Done");
            }
        };

        // Message acknowledgment
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task) throws InterruptedException{
        for (char ch: task.toCharArray()){
            if (ch == '.') Thread.sleep(1000);
        }
    }
}