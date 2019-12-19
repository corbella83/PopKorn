package cc.popkorn.example;

import cc.popkorn.annotations.InjectableProvider;
import cc.popkorn.core.Scope;

@InjectableProvider(scope = Scope.BY_USE)
public class MyInteger {

    public int create() {
        return 54;
    }

}
