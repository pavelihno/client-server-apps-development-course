package com.mirea

import org.springframework.stereotype.Service


@Service
class StockService {

    def saveStock(Stock stock) {
        Stock.withTransaction {
            stock.save(flush: true) 
        } 
    }

    def findStocksByCompanyName(String companyName) {
        return Stock.findAllByCompanyName(companyName)
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
            Stock.withTransaction {
                stock.save(flush: true)
            }
        }
    }

    def deleteStockByCompanyName(String companyName) {
        def stock = Stock.findByCompanyName(companyName)
        if (stock) {
            Stock.withTransaction {
                stock.delete(flush: true)
            }
        }
    }
}