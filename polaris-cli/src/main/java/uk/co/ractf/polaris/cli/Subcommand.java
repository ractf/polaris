package uk.co.ractf.polaris.cli;

import java.util.concurrent.Callable;

public abstract class Subcommand implements Callable<Integer> {

    public abstract int run() throws Exception;

    @Override
    public Integer call() throws Exception {
        return run();
    }
}
