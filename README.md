# clams

An opinionated framework for Clojure web applications.

# Usage

Clams is a framework to create Clojure REST api apps that consume and
produce JSON.  Structurally, it is a collections of Clojure libraries
with glue code to handle the url routes, route controllers,
configuration, and HTTP startup.

We are still in very early stages and this should be considered Alpha
level software that can change at any moment.

## Setup

A Clams app is a standalone program.  One that has its own
`project.clj` and `main` entry point.

The most straightforward way to create a Clams app is to use the
provided generator.  That will create a new runnable app that can be
used as a template to develop the app.  It is also useful to review
the minimal app that is generated to understand the Clams feature set.

To generate and run a Clams app:

    git clone git@github.com:standardtreasury-internal/clams-template.git
    cd clams-template
    lein install
    cd ..
    lein new clams myapp --snapshot
    cd myapp
    lein run

## Startup

The Clams startup code is very simple.  The function
`clams.app/start-server` takes the name of the application's namespace
and bootstraps from that.  Since in our working example, the app is
named `myapp`, the startup code is just:

    (clams.app/start-server 'myapp)

This will load all the configuration, all the routes, and then start
the `http-kit` server on the configured port.  The server will
included a standard set of middleware appropriate for our REST api,
but you also can include middleware as an optional argument to
`start-server`.

## Routes

Routes are defined in `myapp.routes/routes`.  This is an array of
routes, where each individual route is the HTTP method to use, the
path to match, and the name of the controller function used to handle
the route.

- The method is just one of the normal HTTP methods that ae defined in
  `clams.route`.
- The path matching is a `clout` pathspec given as a string or vector.
  See the generated app for examples.
- The controller is a keyword that encodes the fully qualified
  function that will handle this route.

  The keyword is converted to a function by splitting it by the `-`
  character and taking the last element as the function name and first
  elements as the package name within the `myapp.controllers.`
  namespace.  That is, `:users-pages-create` keyword corresponds to
  the `myapp.controllers.users.pages/create` function.

## Controllers

The Controller is just a function that is named as above.  The
function is executed and expected to return a valid Ring response
object or to throw an exception.  There are helper functions in
`clams.response` to accomplish both of these tasks.

If you just use `defn` to create the controller function, the function
should take only a single Ring request object.  It is up to the
function to parse out all the params needed to handle the request.

Alternatively, you can use the `defcontroller` macro.  This will
define a function that takes a a list of arguments that are augmented
by a schema type that will be pulled from the parameters in the
request.  That is, each arg in the function definition will be the
name of a corresponding parameter and will be coerced into the type
given in the function definition.

For example, to make a controller `create` that names a user-id,
title, rating, and body then one would use:

    (defcontroller create
      "Create a new page"
      [id p/Str title p/Str rating p/Int body p/Str]
      ...)

Where the schema types are referenced here as `p/` and defined in
`clams.params`.

## Configuration

The configuration is loaded at startup time from files, the
environment, and Java command line properties.

The last configuration parameter defined overrides any previous
definitions.

In order, the configuration sources are:

- conf/base.edn
- conf/default.edn
- conf/$CLAMS_ENV.edn
- Environment variables
- Java properties

Where `$CLAMS_ENV` is the value of the `CLAMS_ENV` environment
variable (if any).

# Authors

- @jimbru
