"""Generate csv files to analyse prices,
orders and agents portofolio from ATOM data.
"""
import argparse
import re

def readfile(filename: str):
    f = open(filename, 'r')
    agRegexp = re.compile('^Agent;')
    ordRegexp = re.compile('^Order;')
    priRegexp = re.compile('^Price;')
    agents = ["Agent;Cash;Asset;Quantity;Price;Day"]
    orders = ["Asset;Agent;OrderID;Nature;Type;Price;Quantity;Validity;Day"]
    prices = ["Asset;Price;Quantity;Type;InitOrderID;FfOrderID;BestAsk;BestBid;Day"]
    currentDay = 1
    for l in f.readlines():
        if agRegexp.match(l):
            agents.append("{};{}".format(agRegexp.sub("", l).replace("\n", ""), currentDay))
        elif ordRegexp.match(l):
            orders.append("{};{}".format(ordRegexp.sub("", l).replace("\n", ""), currentDay))
        elif priRegexp.match(l):
            prices.append("{};{}".format(priRegexp.sub("", l).replace("\n", ""), currentDay))
        elif re.match('^Day', l):
            currentDay += 1
    writeto("agents.csv", agents)
    writeto("orders.csv", orders)
    writeto("prices.csv", prices)

def writeto(filename: str, text: list):
    f = open(filename, 'w')
    f.write("\n".join(text))
    f.close()

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-f", "--file", help="datafile from ATOM", type=str, default="out.atom")
    args = parser.parse_args()
    readfile(args.file)

if __name__ == '__main__':
    main()