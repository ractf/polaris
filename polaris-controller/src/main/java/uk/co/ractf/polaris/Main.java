package uk.co.ractf.polaris;

import uk.co.ractf.polaris.controller.ControllerMain;
import uk.co.ractf.polaris.host.NodeMain;

import java.util.Arrays;

public class Main {

    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -jar polaris.jar (controller/node)");
            System.exit(1);
        }

        if (args[0].equals("controller")) {
            ControllerMain.main(Arrays.copyOfRange(args, 1, args.length));
        } else if (args[1].equals("node")) {
            NodeMain.main(Arrays.copyOfRange(args, 1, args.length));
        }
    }

}
