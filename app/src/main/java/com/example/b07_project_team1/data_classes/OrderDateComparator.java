package com.example.b07_project_team1.data_classes;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;

public class OrderDateComparator implements Comparator<Order> {
    //use comparator over compareTo method because getRawTimestamp messes up serializability in Order

    private LocalDate getRawTimestamp(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        return LocalDate.parse(order.getFormattedTimestamp(), formatter);
    }

    @Override
    public int compare(Order order1, Order order2) {
        if ((order1.isCompleted() && order2.isCompleted()) || (!order1.isCompleted() && !order2.isCompleted())) {
            if ((getRawTimestamp(order1).compareTo(getRawTimestamp(order2)) != 0)) {
                return getRawTimestamp(order1).compareTo(getRawTimestamp(order2));
            } else {
                return order1.toString().compareTo(order2.toString());
            }
        }
        if (order1.isCompleted()) {
            return 1;
        }
        return -1;
    }

}
