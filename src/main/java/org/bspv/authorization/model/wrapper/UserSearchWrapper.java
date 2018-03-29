package org.bspv.authorization.model.wrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.util.MultiValueMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Gather and transform parameters from the http request to be usable for a search.
 *
 */
@ToString(of = { "enabled", "username", "ids" })
@EqualsAndHashCode(of= { "enabled", "username", "ids" })
public class UserSearchWrapper {

    private enum Parameters {

        ENABLED {

            @Override
            void handle(UserSearchWrapper wrapper, List<String> value) {
                if (value.size() > 1) {
                    throw new IllegalArgumentException("The parameter 'enabled' allow at most 1 value.");
                }
                final boolean enabled = value.isEmpty() || Boolean.parseBoolean(value.get(0));
                wrapper.enabled.ifPresent(e -> {
                    if (!e.equals(enabled)) {
                        throw new IllegalArgumentException(
                                "Parameter 'enabled' already set with a different value (or mutually exclusive with 'disabled' param.");
                    }
                });
                wrapper.enabled = Optional.of(enabled);
            }

        },
        
        DISABLED {

            @Override
            void handle(UserSearchWrapper wrapper, List<String> value) {
                if (value.size() > 1) {
                    throw new IllegalArgumentException("The parameter 'disabled' allow at most 1 value.");
                }
                final boolean disabled = value.isEmpty() && !Boolean.parseBoolean(value.get(0));
                wrapper.enabled.ifPresent(e -> {
                    if (e.equals(disabled)) {
                        throw new IllegalArgumentException(
                                "Parameter 'disabled' already set with a different value (or mutually exclusive with 'ensabled' param.");
                    }
                });
                wrapper.enabled = Optional.of(!disabled);
            }

        },
        
        USERNAME {

            @Override
            void handle(UserSearchWrapper wrapper, List<String> value) {
                if (value.size() != 1) {
                    throw new IllegalArgumentException("The parameter 'username' allow exactly 1 value.");
                }
                wrapper.username = Optional.of(value.get(0));
            }

        },

        IDS {

            @Override
            void handle(UserSearchWrapper wrapper, List<String> value) {
                wrapper.ids = Optional.of(Collections.unmodifiableList(value));
            }

        };

        abstract void handle(UserSearchWrapper wrapper, List<String> value);
    }

    @Getter
    private Optional<Boolean> enabled = Optional.empty();
    @Getter
    private Optional<String> username = Optional.empty();
    @Getter
    private Optional<List<String>> ids = Optional.empty();

    /**
     * Constructor.
     * @param queryMap
     */
    public UserSearchWrapper(MultiValueMap<String, String> queryMap) {
        queryMap.entrySet()
            .stream()
            .filter(e -> Arrays.asList(Parameters.values())
                    .stream()
                    .anyMatch(p -> p.name().equalsIgnoreCase(e.getKey())))
            .forEach(e -> Parameters.valueOf(e.getKey().toUpperCase()).handle(this, e.getValue()));
    }

}
