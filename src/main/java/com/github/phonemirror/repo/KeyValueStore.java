package com.github.phonemirror.repo;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This class stores simple key-value pairs on a file in a specified location.
 * @implNote All methods in this class are thread safe.
 */
//TODO: unit test this
@Singleton
public class KeyValueStore {

    @SuppressWarnings("WeakerAccess")
    public static final int MAX_LINE_SIZE = 512;
    private static final int MAX_SAVE_RETRIES = 5;
    private static final String DELIMITER = "=";
    private static final String DEFAULT_PATH = "/.PhoneMirror/";
    private static final String DEFAULT_FILE_NAME = "kvpairs.dat";
    private static final Logger logger = Logger.getLogger(KeyValueStore.class);
    private static final Charset encoding = StandardCharsets.US_ASCII;

    private final Object lock = new Object();
    private final Path path;

    private volatile Map<String, String> keyValuePairs = new HashMap<>();

    @SuppressWarnings("WeakerAccess")
    public KeyValueStore(String rootPathStr) throws IOException {
        Path tmp = Paths.get(rootPathStr + DEFAULT_PATH);
//        path = Paths.get(rootPathStr);
        try {
            Files.createDirectories(tmp);
        } catch (FileAlreadyExistsException e) {
            // this is OK. catch is here to prevent a crash
        }

        path = Paths.get(rootPathStr + DEFAULT_PATH + DEFAULT_FILE_NAME);
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            // this is OK. catch is here to prevent a crash
        }

        loadFile(path);
    }

    public KeyValueStore() throws IOException {
        this(System.getProperty("user.home"));
    }

    private void loadFile(Path path) throws IOException {

        synchronized (lock) {
            try (BufferedReader reader = Files.newBufferedReader(path, encoding)) {
                String line;
                int lineNo = 0;
                while ((line = reader.readLine()) != null) {
                    lineNo++;
                    String[] kv = line.split(DELIMITER);
                    if (kv.length != 2) {
                        logger.error("Could not load line " + lineNo + " of file " + path.toString());
                    } else {
                        keyValuePairs.put(kv[0].trim(), kv[1].trim());
                    }
                }
            }
        }
    }

    /**
     * Load a value from the key value store
     *
     * @param key          the key
     * @param defaultValue a value to return if an entry was not found
     * @return the value that maps to the provided key, or {@code defaultValue} if it could not be found
     */
    @Nullable
    public String get(String key, @Nullable String defaultValue) {
        synchronized (lock) {
            return keyValuePairs.getOrDefault(key, defaultValue);
        }
    }

    /**
     * Store a value.
     * @implNote The length of key and value combined should be less than or equal to 2-{@link #MAX_LINE_SIZE}
     *
     * @param key   the key to store
     * @param value the value to store
     */
    @SuppressWarnings("WeakerAccess")
    public void set(String key, String value) {
        if ((key.length() + value.length() + 2) > MAX_LINE_SIZE) {
            throw new IllegalArgumentException("These parameters are too large. You should consider using an alternative storage method.");
        }
        key = key.trim();
        value = value.trim();
        synchronized (lock) {
            keyValuePairs.put(key, value);
        }

        // TODO: We should try to batch these together in the event that set() was called multiple times quickly
        new Thread(() -> {
            int tryCount = 0;
            boolean success = false;
            while (!success && tryCount <= MAX_SAVE_RETRIES) {
                try {
                    save();
                    success = true;
                } catch (IOException e) {
                    logger.warn("Could not save on attempt " + tryCount, e);
                    tryCount++;
                }
            }

            if (!success) {
                logger.error("Could not save data after " + (tryCount-1) + " attempts. Giving up.");
            } else {
                logger.debug("Successfully saved data to file");
            }
        }).run();
    }

    /**
     * Check if the provided key is in the store.
     *
     * @param key the key to check
     * @return {@code true} if the provided key is in the store, otherwise {@code false}
     */
    public boolean containsKey(String key) {
        synchronized (lock) {
            return keyValuePairs.containsKey(key);
        }
    }

    /**
     * Delete an entry
     *
     * @param key the key to delete
     * @return {@code true} if it was successful, otherwise {@code false}
     */
    public boolean delete(String key) {
        synchronized (lock) {
            return keyValuePairs.remove(key) != null;
        }
    }

    /**
     * Save the contents of the file to the disk. If an error occurs, this method will make an attempt to roll back changes.
     * @throws IOException if a problem occurred while writing.
     */
    private void save() throws IOException {
        synchronized (lock) {
            StringBuffer buffer = new StringBuffer();
            keyValuePairs.forEach((key, value) -> buffer.append(key).append(DELIMITER).append(value).append(System.lineSeparator()));
            logger.debug(buffer.toString());
            Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".tmp");
            if (temp.toFile().exists()) {
                //noinspection ResultOfMethodCallIgnored
                Files.delete(temp);
            }
            Files.copy(path, temp, StandardCopyOption.COPY_ATTRIBUTES);
            Files.newOutputStream(path).close(); //empty file contents


            try (OutputStream os = Files.newOutputStream(path)) {
                os.write(buffer.toString().getBytes(encoding));
                os.flush();
            } catch (IOException ioe) {
                logger.error("Failed to save file. Performing rollback.", ioe);
                Files.copy(temp, path, StandardCopyOption.COPY_ATTRIBUTES);
                Files.delete(temp);
                throw new IOException("Failed to save file. Original file was successfully rolled back.", ioe);
            }

            Files.delete(temp);
        }
    }


}