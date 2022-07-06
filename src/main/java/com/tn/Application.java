package com.tn;

import com.tn.bean.DeliveryService;
import com.tn.bean.Item;
import com.tn.bean.OrderService;
import com.tn.bean.Service;
import com.tn.context.ApplicationContextImpl;

public class Application {

    public static void main(String[] args) {
        var applicationContext = new ApplicationContextImpl("com.tn");

        var orderService = applicationContext.getBean("orderService", OrderService.class);
        orderService.process();

        var deliveryService = applicationContext.getBean("deliveryService", Service.class);
        deliveryService.process();

        var item1 = applicationContext.getBean(Item.class);
        System.out.println(item1.name());

        var item2 = applicationContext.getBean("Laptop1", Item.class);
        System.out.println(item2.name());

        var services = applicationContext.getAllBeans(Service.class);
        System.out.println(services);

        var deliveryServiceMap = applicationContext.getAllBeans(DeliveryService.class);
        deliveryServiceMap.get("deliveryService")
                          .process();

        // var item3 = applicationContext.getBean("laptop", Service.class);
    }
}
