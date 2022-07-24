package com.mbt.estore.userservice.query;

import com.mbt.estore.core.model.PaymentDetails;
import com.mbt.estore.core.model.User;
import com.mbt.estore.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {

    @QueryHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query){
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("Berker Turan")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User userRest = User.builder()
                .firstName("Berker")
                .lastName("Turan")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();
        return userRest;
    }
}
