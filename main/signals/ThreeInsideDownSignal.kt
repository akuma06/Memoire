package main.signals

import v13.Day
import v13.OrderBook
import v13.agents.chartist.signal.Signal

class ThreeInsideDownSignal() : ChartSignal() {

    override fun update(asset: OrderBook, day: Day): Signal.Direction {
        if (prices.size < 4) { // On a besoin de t, t-1 t-2 et t-3
            return Signal.Direction.HOLD
        }
        val t = prices.size-1
        // t-3 must be in uptrend
        if (getTrend() != Direction.UP)
            return Signal.Direction.HOLD
        // Le t-3 doit être blanc C > O
        if (prices[t-3].first >= prices[t-3].last)
            return Signal.Direction.HOLD
        // le prix open t-2 doit être inférieur au close précédent mais supérieur ou egal au open précédent
        if (prices[t-2].first >= prices[t-3].last || prices[t-2].first < prices[t-3].first)
            return Signal.Direction.HOLD
        // le prix close t-2 doit être inférieur ou egal au close précédent mais supérieur au open précédent
        if (prices[t-2].last > prices[t-3].last || prices[t-2].last <= prices[t-3].first)
            return Signal.Direction.HOLD
        // les prix d'open et closes des deux jours ne peuvent être tous les deux égaux en meme temps
        if (prices[t-2].last == prices[t-3].last && prices[t-2].first == prices[t-3].first)
            return Signal.Direction.HOLD
        // t-1 doit etre noir (tendance baissiere)
        if (prices[t-1].first <= prices[t-1].last)
            return Signal.Direction.HOLD
        // et doit fermer au dessus de l'open de t-3
        if (prices[t-1].last >= prices[t-3].first)
            return Signal.Direction.HOLD

        // Si on est toujurs là, ça veut dire qu'on est dans le pattern donc tendance baissiere
        return Signal.Direction.SELL
    }
}
