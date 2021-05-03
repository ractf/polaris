package uk.co.ractf.polaris.api.pod;

import java.util.Map;

public interface PodWithAffinity {

    Map<String, String> getAffinity();

    Map<String, String> getAntiaffinity();

}
