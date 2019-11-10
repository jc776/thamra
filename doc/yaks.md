![quality_level.png](img/1-quality-level.png)

## target directory
figwheel can create/add it for you
add to paths, 'keep' in git, try to remember not to completely delete it?

## npm?
react intends that you use the pre-packaged bundles - production vs development vs "production with tracing" etc
if I use an npm lib that depends on react, that pulls in "npm" react anyway
webpack/rollup CAN build nearly-equivalent bundles OR be configured to use "globals"
more than one layer of rollup "use globals" -> dev.cljs.edn "use globals" -> cljs code "haha, pretend to import it"
shadow-cljs is a different abstraction - not bad, but I sort of prefer a few things figwheel does
I'll deal with this when something I want isn't on cljsjs I guess

## adding libraries
add-lib is neat for CLJ code
figwheel doesn't always detect new CLJS libs, though

----
[Image source](https://derpibooru.org/1363137)