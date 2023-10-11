package com.mirea

class Stock {

    String companyName
    Long price

    @Override
    String toString() {
        "Stock: [companyName: $companyName, price: $price]"
    }
}