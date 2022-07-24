package com.mbt.estore.productservice.command.rest;

import com.mbt.estore.productservice.command.CreateProductCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {

    private final Environment env;

    private final CommandGateway commandGateway;

    public ProductsCommandController(Environment env, CommandGateway commandGateway) {
        this.env = env;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel model) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder().price(model.getPrice())
                .quantity(model.getQuantity())
                .title(model.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();

        String returnValue;
        //try {
            returnValue = commandGateway.sendAndWait(createProductCommand);
        //} catch (Exception e) {
         //   returnValue = e.getLocalizedMessage();
       // }
        return returnValue;
    }
}
