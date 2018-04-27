package main.helpers

data class Order(val price: Double, val agent: String, val book: String, var quantity: Int) {
    fun toStringArray():Array<Any> {
        return arrayOf(agent.toString(), price, quantity)
    }
}

class Orders(private val sens: Boolean) { // sens: 1 = ASK, 0 = BID
    val orders = ArrayList<Order>()
    fun addOrder(price: Double, agent: String, book: String, quantity: Int) {
        orders.add(Order(price, agent, book, quantity))
        if (!sens) {
            orders.sortWith(compareByDescending({ it.price }))
        } else {
            orders.sortWith(compareBy({ it.price }))
        }
    }
    fun removeOrder(price: Double, quantity: Int) {
        var q = quantity
        while (true) {
            if (!orders.isNotEmpty()) break
            val o = orders[0]
            if ((!sens && o.price < price) || (sens && o.price > price)) break
            if (o.quantity > q) {
                o.quantity -= quantity
                break
            }
            q -= o.quantity
            orders.remove(o)
        }
        if (!sens) {
            orders.sortWith(compareByDescending({ it.price }))
        } else {
            orders.sortWith(compareBy({ it.price }))
        }
    }
    fun getOrders():Array<Array<Any>> {
        return Array(orders.size, { i -> orders[i].toStringArray() })
    }
}