package main.agents

import v13.OrderBook

/**
 * Cet class est objet servant à créer un chandelier.
 *
 * On peut créer un chandelier manuellement grâce au constructeur ou
 * en utilisant la fonction ReadDay.
 * TODO: possible modification en utilisant un constructeur alternatif
 */
data class PriceCandle(var low: Long = 0, var high: Long = 0, var last: Long = 0, var first: Long = 0, var tick: Int = 0) {
    fun ReadDay(day: OrderBook, interval: Int = 5, currentTick: Int = 0): PriceCandle? {
        if (day.lastPrices.size < interval)
            return null
        val maxPrices = day.lastPrices.size-1
        first = day.lastPrices[maxPrices-(interval-1)].price
        last = day.lastPrices[maxPrices].price
        for (i in 0..(interval-1)) {
            if (low > day.lastPrices[maxPrices-i].price || low == (0).toLong())
                low = day.lastPrices[maxPrices-i].price
            if (high < day.lastPrices[maxPrices-i].price || high == (0).toLong())
                high = day.lastPrices[maxPrices-i].price
        }
        tick = currentTick
        return this
    }
}
