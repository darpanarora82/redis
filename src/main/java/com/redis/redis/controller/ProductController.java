package com.redis.redis.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.redis.entity.Product;
import com.redis.redis.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    @Autowired
    public ProductRepository productRepository;

    @PostMapping("/saveProduct")
    @Transactional
    public Product createProduct(@RequestBody Product product) throws JsonProcessingException {
        Jedis jedis = new Jedis();
        ObjectMapper objectMapper = new ObjectMapper();
        jedis.set(product.getId().toString(), objectMapper.writeValueAsString(product));
        return productRepository.save(product);
    }

    @GetMapping("/{id}")
    public Product findProduct(@PathVariable("id") Long id) throws JsonProcessingException {
        Jedis jedis = new Jedis();
        ObjectMapper objectMapper = new ObjectMapper();
        if(jedis.get(id.toString()) != null){
            String productInString = jedis.get(id.toString());
            log.info("Product info fetched from cache: "+ productInString);
            return objectMapper.readValue(productInString, Product.class);
        }
        Product product = productRepository.findById(id).get();
        jedis.set(product.getId().toString(), objectMapper.writeValueAsString(product));
        return product;
    }

}
