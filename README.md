# Clams

Clams is a web application framework for Clojure. Clams builds on top of
[Ring](https://github.com/ring-clojure/ring) with the aim of providing a
complete, "batteries included" package for web development. Structurally, it is
a collections of Clojure libraries with glue code to handle routing, controllers,
configuration, and HTTP startup.

**Note: This is very early software and should be considered unstable. Caveat
fabricator.**

## Installation

Add the following dependency to your `project.clj` file:

    [clams "0.1.0"]

## Getting Started

A Clams app is a standalone program. One that has its own `project.clj` and
`-main` entry point.

The most straightforward way to create a Clams app is to use the provided
generator. That will create a new runnable app that can be used as a template
for development. It is also useful to review the minimal app that is generated
to understand the Clams feature set.

To generate and run a Clams app:

    git clone git@github.com:standardtreasury-internal/clams-template.git
    cd clams-template
    lein install
    cd ..
    lein new clams myapp --snapshot
    cd myapp
    lein run

## Documentation

[Refer to the wiki for full documentation.](https://github.com/standardtreasury-internal/clams/wiki)

## Authors

Jim Brusstar ([@jimbru](https://github.com/jimbru)) and Chris Dean ([@ctdean](https://github.com/ctdean)).

Copyright © 2014-present Standard Treasury.
