---

## Important note

Ta4j is not maintained by Marc de Verdelhan anymore. Future PRs and issues will not be treated here. See [#192](https://github.com/mdeverdelhan/ta4j/issues/192) for more information. This repository is kept for archival purposes and may be used as documentation for ta4j 0.9 (which is a stable version).

The community decided to take over the project and continue to maintain it. The new repository is available at https://github.com/ta4j/ta4j.

---

# ta4j [![Build Status](https://img.shields.io/travis/mdeverdelhan/ta4j.svg)](https://travis-ci.org/mdeverdelhan/ta4j) [![Chat on Riot.im](https://img.shields.io/badge/chat-riot.im-green.svg)](https://riot.im/app/#/room/#ta4j:matrix.org)

***Technical Analysis For Java***

![Ta4 main chart](https://raw.githubusercontent.com/wiki/mdeverdelhan/ta4j/img/ta4j_main_chart.png)

Ta4j is an open source Java library for [technical analysis](http://en.wikipedia.org/wiki/Technical_analysis). It provides the basic components for creation, evaluation and execution of trading strategies.

### Features

 * [x] 100% Pure Java - works on any Java Platform version 8 or later
 * [x] More than 100 technical indicators (Aroon, ATR, moving averages, parabolic SAR, RSI, etc.)
 * [x] A powerful engine for building custom trading strategies
 * [x] Utilities to run and compare strategies
 * [x] Minimal 3rd party dependencies
 * [x] Simple integration
 * [x] One more thing: it's MIT licensed

### Maven configuration

Ta4j is available on [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22ta4j%22). You just have to add the following dependency in your `pom.xml` file.

```xml
<dependency>
    <groupId>eu.verdelhan</groupId>
    <artifactId>ta4j</artifactId>
    <version>0.9</version>
</dependency>
```

For ***snapshots***, add the following repository to your `pom.xml` file.
```xml
<repository>
    <id>sonatype snapshots</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```
The current snapshot version is `0.10-SNAPSHOT`.


### Getting Help

The [wiki](https://github.com/mdeverdelhan/ta4j/wiki) is the best place to start learning about ta4j.

Of course you can ask anything [via 𝕏](http://x.com/MarcdeVerdelhan). For more detailed questions, please use the [issues tracker](http://github.com/mdeverdelhan/ta4j/issues).

### Contributing to ta4j

Here are some ways for you to contribute to ta4j:

  * [Create tickets for bugs and new features](http://github.com/mdeverdelhan/ta4j/issues) and comment on the ones that you are interested in.
  * [Fork this repository](http://help.github.com/forking/) and submit pull requests.
  * Consider donating for new feature development.

See also: the [contribution policy](.github/CONTRIBUTING.md).
