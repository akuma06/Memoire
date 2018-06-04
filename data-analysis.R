library(readr)
library(plotly)
library(plyr)
library(quantmod)

setwd("I:/Documents/simulation_83fd0275-9a01-4690-aeb9-4949c4914497-csv/")
DaysIDC310 = c()
DaysIDC33 = c()
for (day in 1:2) {
  a2a = list(Name="paul-1", Returns10=c(), Returns3=c(), Signals10=c(), Signals3=c())
  for (i in 1:4) {
    if (file_test("-f", sprintf("day_%d/prices_%d.csv", day, i))) {
      prices = read_csv2(sprintf("day_%d/prices_%d.csv", day, i))
      agents = read_csv2(sprintf("day_%d/agents_%d.csv", day, i))
      orders = read_csv2(sprintf("day_%d/orders_%d.csv", day, i))
      ticks = read_csv2(sprintf("day_%d/ticks_%d.csv", day, i))
      
      agentOrders = subset(orders, orders$Agent == a2a$Name)
      nbAgents = length(agentOrders$Agent)
      if (nbAgents == 0)
        next()
      for(a in 1:nbAgents) {
        currentTicks = ticks[ticks$Tick %in% (agentOrders[a,]$Tick-3):(agentOrders[a,]$Tick+2), ]
        if (i > 1) {
          a2a$Returns3 = c(a2a$Returns3, agentOrders[a,]$Cash + (agentOrders[a,]$Price*agentOrders[a,]$Quantity))
        }
        if (length(currentTicks$Tick) < 6)
          next()
        MA1 = sum(currentTicks$LastPrice[1:3])/3
        MA2 = sum(currentTicks$LastPrice[4:6])/3
        if (agentOrders[a,]$Type == "B" && MA1 < MA2) {
          a2a$Signals3 = c(a2a$Signals3, 1)
        } else if (agentOrders[a,]$Type == "A" && MA1 > MA2) {
          a2a$Signals3 = c(a2a$Signals3, 1)
        } else {
          a2a$Signals3 = c(a2a$Signals3, 0)
        }
        currentTicks = ticks[ticks$Tick %in% (agentOrders[a,]$Tick-10):(agentOrders[a,]$Tick+9), ]
        if (length(currentTicks$Tick) < 20)
          next()
        MA1 = sum(currentTicks$LastPrice[1:10])/10
        MA2 = sum(currentTicks$LastPrice[11:20])/10
        if (agentOrders[a,]$Type == "B" && MA1 < MA2) {
          a2a$Signals10 = c(a2a$Signals10, 1)
        } else if (agentOrders[a,]$Type == "A" && MA1 > MA2) {
          a2a$Signals10 = c(a2a$Signals10, 1)
        } else {
          a2a$Signals10 = c(a2a$Signals10, 0)
        }
      }
    }
  }
  DaysIDC310 = c(DaysIDC310, sum(a2a$Signals10)/length(a2a$Signals10))
  DaysIDC33 = c(DaysIDC33, sum(a2a$Signals3)/length(a2a$Signals3))
}




mtCandle = matrix(currentTicks$LastPrice, nrow = 3, byrow = TRUE)
mprec = sum(mtCandle[1,])/6
hausses = 0
baisses = 0
for (i in 2:6) {
  mnow = sum(mtCandle[i,])/6
  if (mprec > mnow)
    hausses++
  if (mprec < mnow)
    baisses++
  mprec = mnow
}
if (hausses > 4)
  a2a$Signals10
m1 = sum(mtCandle[1,])/6
m2 = sum(mtCandle[2,])/6
m3 = sum(mtCandle[3,])/6
m4 = sum(mtCandle[4,])/6
m5 = sum(mtCandle[5,])/6
m6 = sum(mtCandle[6,])/6

agentOrders[1,]$Type

Day = {}
Day$High = tapply(prices$Price/100, prices$Day, max)
Day$Low = tapply(prices$Price/100, prices$Day, min)
Day$Close = tapply(prices$Price/100, prices$Day, function(p) {
  return(p[length(p)])
});
Day$Open = tapply(prices$Price/100, prices$Day, function(p) {
  return(p[1])
});
Day = as.data.frame(Day)

plot_ly(data = Day, x = 1:30, type = "candlestick", open = ~ Open, close = ~ Close, low = ~ Low, high = ~ High)

plot(subset(prices$Price, prices$Day == 1), type = "l")
#plot(subset(prices$Price, prices$Day == 2), type = "l")
#plot(subset(prices$Price, prices$Day == 3), type = "l")
