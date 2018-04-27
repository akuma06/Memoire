package main.helpers

data class Point(val x: Double, val y: Double)

class Points {
    private val points = ArrayList<Point>()
    fun addPoint(X: Double, Y: Double) {
        points.add(Point(X,Y))
    }
    fun getXPoints():DoubleArray {
        val x = DoubleArray(points.size)
        for ((i, pt) in points.withIndex()) {
            x[i] = pt.x
        }
        return x
    }
    fun getYPoints():DoubleArray {
        val y = DoubleArray(points.size)
        for ((i, pt) in points.withIndex()) {
            y[i] = pt.y
        }
        return y
    }
}