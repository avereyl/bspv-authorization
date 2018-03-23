package org.bspv.authorization.model.wrapper;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.MultiValueMap;

/**
 * Gather and transform parameters from the http request to be usable for a search.
 *
 */
public class UserSearchWrapper {
    
    private static final List<String> HANDLED_PARAMETERS = Arrays.asList(
            "enabled",
            "disabled",
            "username",
            "ids"
            );

    public UserSearchWrapper(MultiValueMap<String, String> queryMap) {
        queryMap.entrySet()
            .stream()
            .filter(e -> HANDLED_PARAMETERS.contains(e.getKey().toLowerCase()))
            .forEach(e -> this.handleParameter(e.getKey(), e.getValue()));
    }

    private void handleParameter(String key, List<String> value) {
        // TODO Auto-generated method stub
    }

}
