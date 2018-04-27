package main.helpers

import main.agents.PriceCandle

object PricesList {
    val prices = arrayListOf<PriceCandle>()
    val lastIndex: Int
        get() = prices.lastIndex
    val size:Int
        get() = prices.size

    fun add(p: PriceCandle) {
        prices.add(p)
    }
    fun clear() {
        prices.clear()
    }
    operator fun get(index: Int): PriceCandle {
        return prices[index]
    }

    operator fun iterator(): Iterator<PriceCandle> {
        return prices.iterator()
    }

    operator fun set(index: Int, value: PriceCandle) {
        prices.set(index, value)
    }
}