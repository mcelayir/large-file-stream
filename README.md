# large-file-stream
A POC application to read large files with stream processing approach

## implementation details

two approaches implemented:

1- <b>Stream:</b> simple buffering with backpressure and aggregating with akka actors
2- Seperating reading and processing with kafka 

## how to run

### tech stack
- scala 2.13
- sbt 1.4.7 
- java 11 
- docker compose

1- Generate a large `csv` file in given format

```
1, "DE"
2, "NL"
1, "US"
3, "US"

```

You can run `largecsvgenerator.csv` by to create a file of 10G.

<b>WARNING</b> A file named `largefile.csv` must be inside projects root folder.

1- to run stream solution

```
> export SOLUTION="stream"
> sbt run
```

2- to run kafka solution

start `kafka` using `docker-compose`

```
> docker-compose up
```

then

```
> export SOLUTION="kafka"
> sbt run
```

Results will be aggregated and displayed on console continously.

Example
```
...
(90,EnrichedProduct(90,Map(NL -> 17852, DE -> 17674, US -> 17624)))
(91,EnrichedProduct(91,Map(US -> 17915, NL -> 17688, DE -> 17755)))
(92,EnrichedProduct(92,Map(NL -> 17568, DE -> 17476, US -> 17770)))
(93,EnrichedProduct(93,Map(DE -> 17690, US -> 17841, NL -> 17584)))
(94,EnrichedProduct(94,Map(DE -> 17675, NL -> 17631, US -> 17560)))
(95,EnrichedProduct(95,Map(NL -> 17717, US -> 17550, DE -> 17790)))
(96,EnrichedProduct(96,Map(US -> 17609, DE -> 17921, NL -> 17732)))
(97,EnrichedProduct(97,Map(US -> 17441, NL -> 17804, DE -> 17370)))
(98,EnrichedProduct(98,Map(DE -> 17775, US -> 17797, NL -> 17658)))
(99,EnrichedProduct(99,Map(NL -> 17711, US -> 17629, DE -> 17813)))
(100,EnrichedProduct(100,Map(US -> 17830, NL -> 17675, DE -> 17805)))
```
