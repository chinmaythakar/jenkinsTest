package com.ensat.entities

import spock.lang.Specification

class ProductSpec extends Specification{

    Product product = new Product()

    def"asd"(){
        given:
        product.setProductId("1")
        product.setVersion(1)
        product.setPrice(1500 as BigDecimal)
        product.setName("oracle")
        product.setId(1)

        when:
        String name = product.getName()
        int id = product.getId()
        BigDecimal price = product.getPrice()
        int version = product.getVersion()
        String productId = product.getProductId()

        then:
        name == "oracle"
        id == 1
        price == 1500 as BigDecimal
        productId == "1"
        version == 1

    }
}
