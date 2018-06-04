"""Generate csv files to analyse prices,
orders and agents portofolio from ATOM data.
Files generated in the following folders:
$OUTPUT/$FILENAME/day_$i/agents_$n.csv
$OUTPUT/$FILENAME/day_$i/prices_$n.csv
$OUTPUT/$FILENAME/day_$i/orders_$n.csv
"""
import argparse
import re
import os
import logging
from tqdm import tqdm
from os.path import getsize, basename

OUTPUT = ""
CURRENT_DAY = 0
CURRENT_TICK = 1
INPUT_FILE = ""
NB_TICK_PER_FILE = 800000
CURRENT_PART = 0

def readfile(filename: str):
    global INPUT_FILE, CURRENT_TICK
    INPUT_FILE = filename
    agRegexp = re.compile('^Agent;')
    ordRegexp = re.compile('^Order;')
    priRegexp = re.compile('^Price;')
    tickRegexp = re.compile('^Tick;') # We only have one tick since one asset
    pb = tqdm(total=getsize(INPUT_FILE), unit="B", unit_scale=True,
              desc=(basename(INPUT_FILE)[:12] + '...') if len(basename(INPUT_FILE)) > 14 else basename(INPUT_FILE), miniters=1,
              ncols=80, ascii=True)
    newday()
    tempAgents = []
    tempOrders = []
    tempPrices = []
    tempTicks = []
    processed_bytes = 0
    with open(INPUT_FILE, 'r') as f:
        for l in f:
            if agRegexp.match(l):
                tempAgents.append("{};{}".format(agRegexp.sub("", l).replace("\n", ""), CURRENT_TICK))
            elif ordRegexp.match(l):
                tempOrders.append("{};{}".format(ordRegexp.sub("", l).replace("\n", ""), CURRENT_TICK))
            elif priRegexp.match(l):
                tempPrices.append("{};{}".format(priRegexp.sub("", l).replace("\n", ""), CURRENT_TICK))
            elif re.match('^Day', l):
                writeAll(tempAgents, tempOrders, tempPrices, tempTicks)
                tempAgents.clear()
                tempOrders.clear()
                tempPrices.clear()
                tempTicks.clear()
                newday()
            elif tickRegexp.match(l):
                tempTicks.append("{}{}".format(tickRegexp.sub("", l).replace("\n", ""), CURRENT_DAY))
                CURRENT_TICK += 1
                if CURRENT_TICK%NB_TICK_PER_FILE == 0:
                    writeAll(tempAgents, tempOrders, tempPrices, tempTicks)
                    tempAgents.clear()
                    tempOrders.clear()
                    tempPrices.clear()
                    tempTicks.clear()
                    newfiles()
            if len(tempAgents) > 1000:
                writeto("agents_{}.csv".format(CURRENT_PART), tempAgents)
                tempAgents.clear()
            elif len(tempOrders) > 1000:
                writeto("orders_{}.csv".format(CURRENT_PART), tempOrders)
                tempOrders.clear()
            elif len(tempPrices) > 1000:
                writeto("prices_{}.csv".format(CURRENT_PART), tempPrices)
                tempPrices.clear()
            processed_bytes += len(l)
            # update progress every MB.
            if processed_bytes >= 1024 * 1024:
                pb.update(processed_bytes)
                processed_bytes = 0
    pb.update(processed_bytes)
    pb.close()
    writeAll(tempAgents, tempOrders, tempPrices, tempTicks)
    tempAgents.clear()
    tempOrders.clear()
    tempPrices.clear()
    tempTicks.clear()

def writeto(filename: str, text: list, mode: str = "a"):
    baseFile = os.path.splitext(INPUT_FILE)[0]
    output_path = os.path.join(OUTPUT, "{}-csv".format(baseFile), "day_{}".format(CURRENT_DAY))
    if not os.path.isdir(output_path):
        os.makedirs(output_path)
    with open(os.path.join(output_path, filename), mode) as f:
        f.write("{}\n".format("\n".join(text)))

def newday():
    global CURRENT_DAY, CURRENT_TICK
    CURRENT_TICK = 1
    CURRENT_DAY += 1
    newfiles()

def newfiles():
    global CURRENT_PART
    CURRENT_PART = int(CURRENT_TICK/NB_TICK_PER_FILE)
    writeto("agents_{}.csv".format(CURRENT_PART), ["Agent;Cash;Asset;Quantity;Price;Tick"], "w")
    writeto("orders_{}.csv".format(CURRENT_PART), ["Asset;Agent;OrderID;Nature;Type;Price;Quantity;Validity;Tick"], "w")
    writeto("prices_{}.csv".format(CURRENT_PART), ["Asset;Price;Quantity;Type;InitOrderID;FfOrderID;BestAsk;BestBid;Tick"], "w")
    writeto("ticks_{}.csv".format(CURRENT_PART), ["Tick;Asset;BestAsk;BestBid;LastPrice;Day"], "w")


def writeAll(agents: list, orders: list, prices: list, ticks: list):
    writeto("agents_{}.csv".format(CURRENT_PART), agents)
    writeto("orders_{}.csv".format(CURRENT_PART), orders)
    writeto("prices_{}.csv".format(CURRENT_PART), prices)
    writeto("ticks_{}.csv".format(CURRENT_PART), ticks)

def main():
    global OUTPUT
    parser = argparse.ArgumentParser()
    parser.add_argument("-f", "--file", help="datafile from ATOM", type=str, default="out.atom")
    parser.add_argument("-o", "--output", help="Output directory path", type=str, default="")
    args = parser.parse_args()
    OUTPUT = str(args.output)
    if not os.path.exists(OUTPUT) or not os.path.isdir(OUTPUT):
        logging.warning("Output directory doesn't exist or is not a folder: {}\nUsing the current working directory as default path.\n".format(OUTPUT))
        OUTPUT = ""
    
    readfile(args.file)
    logging.info("**********************************************")
    logging.info("*        FILES GENERATED SUCCESSFULLY        *")
    logging.info("**********************************************")

if __name__ == '__main__':
    main()
