package cc.popkorn.example.model

import cc.popkorn.annotations.Exclude

interface DiA

interface DiB

interface DiC

interface Wrapper {
    interface DiD
}


@Exclude
interface DiG
