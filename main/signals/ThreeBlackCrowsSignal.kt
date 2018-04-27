package main.signals;

import v13.Day
import v13.OrderBook
import v13.agents.chartist.signal.Signal

class ThreeBlackCrowsSignal() : ChartSignal() {

    override fun update(asset: OrderBook, day: Day): Signal.Direction {
        if (prices.size < 4) { // On a besoin de t, t-1 t-2 et t-3
            return Signal.Direction.HOLD
        }
        for (i in 1..2) {
            val t = prices.size - 1 - i
            // t-3 must be in uptrend
            if (getTrend() != Direction.UP)
                return Signal.Direction.HOLD
            // Chaque open prix antérieur doit être inférieur à celui qui le précédait
            if (prices[t].first >= prices[t - 1].first)
                return Signal.Direction.HOLD
            // Chaque close prix antérieur doit être inférieur à celui qui le précédait
            if (prices[t].last >= prices[t - 1].last)
                return Signal.Direction.HOLD
            // Chaque Close doit etre superieur a son Open
            if (prices[t].first - prices[t].last <= 0)
                return Signal.Direction.HOLD
            // Chaque jour doit ouvrir dans l'intervalle du jour précédant
            if (prices[t].first <= prices[t - 1].last || prices[t].first >= prices[t - 1].first)
                return Signal.Direction.HOLD
        }
        // Si la loop se termine, ça veut dire qu'on est dans le pattern donc tendance baissiere
        return Signal.Direction.SELL
    }
}
