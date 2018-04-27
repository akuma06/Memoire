package main.signals

import v13.Day
import v13.OrderBook
import v13.agents.chartist.signal.Signal

class EveningStarSignal() : ChartSignal() {

    override fun update(asset: OrderBook, day: Day): Signal.Direction {
        if (prices.size < 4) { // On a besoin de t, t-1 t-2 et t-3
            return Signal.Direction.HOLD
        }
        val t = prices.size-1
        // t-3 must be in uptrend
        if (getTrend() != Direction.UP)
            return Signal.Direction.HOLD
        // Le t-3 doit être noir C > O
        if (prices[t-3].last <= prices[t-3].first)
            return Signal.Direction.HOLD
        // Le deuxième jour doit avoir un intervalle
        if (Math.abs(prices[t-2].last - prices[t-2].first) <= 0)
            return Signal.Direction.HOLD
        // The second day t-2 must be gaped from t-3
        if (prices[t-2].last <= prices[t-3].last || prices[t-2].first <= prices[t-3].last)
            return Signal.Direction.HOLD
        // t-1 doit etre noir (tendance baissiere)
        if (prices[t-1].first <= prices[t-1].last)
            return Signal.Direction.HOLD
        // et doit fermer plus bas que le milieu de t-3
        if (prices[t-1].last >= prices[t-3].first + (prices[t-3].last - prices[t-3].first)/2)
            return Signal.Direction.HOLD

        // Si on est toujurs là, ça veut dire qu'on est dans le pattern donc retournement à la baisse
        return Signal.Direction.SELL
    }
}
