package com.github.phonemirror.repo;

import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.UUID;

/**
 * This class manages a persistent serial ID. This id allows a specific installation to be
 * identified so the correct cryptographic key can be obtained when communicating with other
 * devices.
 */
public class SerialRepository {

    private final Logger logger = Logger.getLogger(SerialRepository.class);
    private static final String REPO_KEY = "SerialId";

    private String serial;
    private KeyValueStore storage;

    @Inject
    public SerialRepository(KeyValueStore store) {
        storage = store;
        loadRepo();
    }

    private void loadRepo() {
        serial = storage.get(REPO_KEY, null);
        if (serial == null) {
            logger.debug("Serial ID does not exist. Generating a new one.");
            serial = generateSerialId();
        } else {
            logger.debug("Serial ID successfully loaded from data store.");
        }
    }

    /**
     * Generate a new ID and save it to {@code SharedPreferences}
     * @return the generated ID
     */
    private String generateSerialId() {
        String id = UUID.randomUUID().toString();
        storage.set(REPO_KEY, id);
        return id;
    }


    /**
     * Obtain the application installation's unique identifier.
     * @return the ID.
     */
    public String getSerialId() {
        return serial;
    }


}
