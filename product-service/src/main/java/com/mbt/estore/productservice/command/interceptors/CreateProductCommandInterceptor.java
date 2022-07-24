package com.mbt.estore.productservice.command.interceptors;

import com.mbt.estore.productservice.command.CreateProductCommand;
import com.mbt.estore.productservice.core.data.ProductLookupEntity;
import com.mbt.estore.productservice.core.repository.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER= LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        return (index,comand)-> {

            LOGGER.info("Interceptor command: "+comand.getPayloadType());
            if(CreateProductCommand.class.equals(comand.getPayloadType())){

                CreateProductCommand createProductCommand=(CreateProductCommand)comand.getPayload() ;
                ProductLookupEntity byProductIdOrTitle = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
                if(byProductIdOrTitle!=null){
                    throw new IllegalStateException(String.format("Product with productId %s or title %s already exist",
                            createProductCommand.getProductId(),createProductCommand.getTitle()));
                }

            }
            return comand;
        };
    }
}
