package com.mirea

import org.springframework.stereotype.Service


@Service
class StockService {

    def saveStock(Stock stock) {
        stock.save(flush: true)
    }

    def findStockByCompanyName(String companyName) {
        Stock.findByCompanyName(companyName)
    }

    def findStocksByPrice(Long price) {
        Stock.findAllByPrice(price)
    }

    def findAllStocks() {
        Stock.list()
    }

    def updateStockPrice(String companyName, Long newPrice) {
        def stock = Stock.findByCompanyName(companyName)
        if (stock) {
            stock.price = newPrice
            stock.save(flush: true)
        }
    }

    def deleteStockByCompanyName(String companyName) {
        def stock = Stock.findByCompanyName(companyName)
        if (stock) {
            stock.delete(flush: true)
        }
    }
}