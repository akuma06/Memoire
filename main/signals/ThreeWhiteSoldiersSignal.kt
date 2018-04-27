package main.signals

import main.helpers.PricesList
import v13.Day
import v13.OrderBook
import v13.agents.chartist.signal.Signal

class ThreeWhiteSoldiersSignal() : ChartSignal() {
    private val prices = PricesList

    override fun update(asset: OrderBook, day: Day): Signal.Direction {
        if (prices.size < 4) { // On a besoin de t, t-1 t-2 et t-3
            return Signal.Direction.HOLD
        }
        // t-3 must be in Downtrend
        if (getTrend() != Direction.DOWN)
            return Signal.Direction.HOLD
        for (i in 1..2) {
            val t = prices.size-1-i
            // Chaque close prix antérieur doit être supérieur à celui qui le précédait
            if (prices[t].last <= prices[t-1].last)
                return Signal.Direction.HOLD
            // Chaque Close doit etre superieur a son Open
            if (prices[t].last - prices[t].first <= 0)
                return Signal.Direction.HOLD
            // Chaque jour doit ouvrir dans l'intervalle du jour précédant
            if (prices[t].first <= prices[t-1].first || prices[t].first >= prices[t-1].last)
                return Signal.Direction.HOLD
        }
        // Si la loop se termine, ça veut dire qu'on est dans le pattern donc tendance haussiere
        return Signal.Direction.BUY
    }
}
