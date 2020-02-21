package cc.popkorn.example.model

import cc.popkorn.annotations.Exclude

interface DiA

interface DiB

interface DiC

interface Wrapper {
    interface DiD
}

interface DiE

interface DiF

@Exclude
interface DiG


abstract class DaH : DiE

abstract class DaI : DaH(), DiF

abstract class DaJ

