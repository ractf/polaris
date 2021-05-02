package uk.co.ractf.polaris.cli.subcommands;

import picocli.CommandLine;
import uk.co.ractf.polaris.cli.subcommands.challenge.ChallengeDelete;
import uk.co.ractf.polaris.cli.subcommands.challenge.ChallengeInspect;
import uk.co.ractf.polaris.cli.subcommands.challenge.ChallengeList;
import uk.co.ractf.polaris.cli.subcommands.challenge.ChallengeSubmit;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "challenge", aliases = {"chal", "ch", "c"}, subcommands = {
        ChallengeList.class,
        ChallengeDelete.class,
        ChallengeSubmit.class,
        ChallengeInspect.class
})
public class Challenge implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return null;
    }
}