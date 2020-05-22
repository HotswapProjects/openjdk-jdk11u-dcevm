# Changelog for DCEVVM11 (HotswapVM)

## Dcevm-11.0.7+1 (2019-05-22)

1. Skip GC runs for redefinitions without instance size change
1. Better support for lambda class redefinition
1. Fix access to com.sun.beans.util (HotswapAgent patch)
1. Fix fallback mode to standard redefinition

## Dcevm-11.0.6+1 (2019-04-12)

1. Fix direct method handles
1. Fixed repeated redefinition of inner(hosted) anonymous class
1. Optimization for huge redefinition (iterate classes+clear cp cache only once)

## Version 11.0.5+7 (2018-12-04)

1. Open ClassInfo.CACHE for HotswapAgent. Fixes issues with bean resolving

## Version 11.0.5+3 (2018-11-21)

1. Fix static final initializer - allow new static final fields also in Java11

## Dcevm-11.0.1+6 (2018-12-23)

1. Cuncurrent Mark Sweep GC support. Use -XX:+UseConcMarkSweepGC VM option to turn it on.

## Dcevm-11.0.1+5 (2018-12-16)

1. Fix method handles + Field handles (static + instance)
1. "HotswapAgent not found on path:.." is not printed out on start if hotswap-agent.jar is missing in lib/hotswap/. So jvm image can be used as alternative jdk image (-XXaltjvm=dcevm) without hotswap-agent without unpleasant messages.
1. Newest HotswapAgent 1.3.1-SNAPHOT (including new WicketPlugin)
1. fix HotswapProjects/openjdk-jdk11u#1
