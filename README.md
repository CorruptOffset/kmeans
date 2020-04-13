# K-Means Scala

kmeans is a Scala library for Clustering Data.

- light build
- build with Scala Futures
- build your own distance calculation if needed
- fast ( on my machine ;) )
- Cross Build for 2.12 and 2.13
... 

## Installation
Build your own for now

## Usage

```scala
val settings = ClusterSettings(...)
val method = ... # your Distance Calculation Method e.g. Manhattan
val seeds: Seq[Seed] = Seq(..) #your data here
val kmeans = new Kmeans(seeds,method)
val kmeansFuture = kmeans.process()
```

## Contributing
Pull requests are VERY MUCH  welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate. (Some Tests available)

TODO:
- optimize me (more)
- place to publish this artifact
- more calc methods out of the box
- make code beautiful
- ...

## License
[MIT](https://choosealicense.com/licenses/mit/)