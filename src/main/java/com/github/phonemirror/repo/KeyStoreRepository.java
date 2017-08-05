package com.github.phonemirror.repo;

import com.github.phonemirror.util.Configuration;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * This class manages public and private keys.
 *
 * TODO: move to something more secure (password protected) to save keys.
 */
@Singleton
public class KeyStoreRepository {

    private final Logger logger = Logger.getLogger(KeyStoreRepository.class);

    private static final String ALGORITHM = "RSA";
    private static final String SIG_ALG = "SHA256WithRSA";
    private static final String FLAG_NAME = "key_exists";
    private static final int KEY_SIZE = 2048;
    private Configuration config;
    private KeyValueStore kvs;

    private KeyPair pair;

    @Inject
    public KeyStoreRepository(Configuration config, KeyValueStore keyValueStore) {
        this.config = config;
        kvs = keyValueStore;
        if (!keyValueStore.containsKey(FLAG_NAME)) {
            logger.trace("No key found. Generating new key pair");
            generateKeyPair();
        } else {
            logger.trace("Found key pair. Loading keys...");
            try {
                loadKeys();
                logger.trace("... done loading keys.");
            } catch (IOException e) {
                throw new RuntimeException("Could not load keys");
            }
        }
    }

    private void loadKeys() throws IOException {
        byte[] publicKey = Files.readAllBytes(Paths.get(config.getPublicKeyPath()));
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey);

        byte[] privateKey = Files.readAllBytes(Paths.get(config.getPrivateKeyPath()));
        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey);

        try {
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            pair = new KeyPair(factory.generatePublic(publicSpec), factory.generatePrivate(privateSpec));
        } catch (NoSuchAlgorithmException e) {
            // should never happen.
            throw new RuntimeException("RSA not supported.", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Could not decode keys", e);
        }
    }


    private void generateKeyPair() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(ALGORITHM);
            kpg.initialize(KEY_SIZE);
            pair = kpg.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            // should not happen
            logger.error(ALGORITHM + " algorithm not found.");
            throw new RuntimeException(ALGORITHM + " algorithm not found.");
        }

        try {
            saveKey(pair.getPublic(), config.getPublicKeyPath());
            saveKey(pair.getPrivate(), config.getPrivateKeyPath());

            kvs.set(FLAG_NAME, "true");
        } catch (IOException e) {
            throw new RuntimeException("Could not save keys.");
        }
    }

    /**
     * Writes {@code key} to file with path {@code path} in
     * standard encoding ({@code X.509} for RSA public key, {@code PKCS#8} for RSA private key).
     *
     * @param key the key to write.
     * @param path the path to save the file at
     *
     * @throws IOException if something goes wrong.
     */
    private void saveKey(Key key, String path) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(key.getEncoded());
        } catch (IOException e) {
            // rethrow, but make sure that the FileOutputStream is closed.
            throw new IOException(e);
        }
    }

    public PrivateKey getPrivateKey() {
        return pair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return pair.getPublic();
    }


}
