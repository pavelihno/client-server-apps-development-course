package com.mirea

class Stock {

    String companyName
    Long price

    static constraints = {
        companyName blank: false, maxSize: 255
        price min: 0
    }
}