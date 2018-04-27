library(readr)
library(plotly)
library(plyr)
library(quantmod)

setwd("C:/Users/akuma06/Documents/Dev/atom")
prices = read_csv2("prices.csv")
agents = read_csv2("agents.csv")
orders = read_csv2("orders.csv")

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
