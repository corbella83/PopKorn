package cc.popkorn.exceptions


class InstanceNotFoundException : RuntimeException("Invalid instance. Seems like you didn't call addInjectable")