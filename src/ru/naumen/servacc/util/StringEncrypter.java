/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 *
 */
package ru.naumen.servacc.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

public class StringEncrypter
{
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    public static final String DES_ENCRYPTION_SCHEME = "DES";

    private static final String UTF8 = "UTF-8";

    private KeySpec keySpec;
    private SecretKeyFactory keyFactory;
    private Cipher cipher;

    public StringEncrypter(String encryptionScheme, String encryptionKey) throws EncryptionException
    {
        if (encryptionKey == null)
        {
            throw new IllegalArgumentException("encryption key was null");
        }

        try
        {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(encryptionKey.getBytes());
            BigInteger bi = new BigInteger(1, digest);
            encryptionKey = String.format("%0" + (digest.length << 1) + "X", bi);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new EncryptionException(e);
        }

        if (encryptionKey.trim().length() < 24)
        {
            throw new IllegalArgumentException("encryption key was less than 24 characters");
        }

        try
        {
            byte[] keyAsBytes = encryptionKey.getBytes(UTF8);

            if (encryptionScheme.equals(DESEDE_ENCRYPTION_SCHEME))
            {
                keySpec = new DESedeKeySpec(keyAsBytes);
            }
            else if (encryptionScheme.equals(DES_ENCRYPTION_SCHEME))
            {
                keySpec = new DESKeySpec(keyAsBytes);
            }
            else
            {
                throw new IllegalArgumentException("Encryption scheme not supported: " + encryptionScheme);
            }

            keyFactory = SecretKeyFactory.getInstance(encryptionScheme);
            cipher = Cipher.getInstance(encryptionScheme);
        }
        catch (InvalidKeyException e)
        {
            throw new EncryptionException(e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new EncryptionException(e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new EncryptionException(e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new EncryptionException(e);
        }
    }

    public String encrypt(String unencryptedString) throws EncryptionException
    {
        if (Util.isEmptyOrNull(unencryptedString))
        {
            throw new IllegalArgumentException("unencrypted string is empty");
        }

        try
        {
            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cleartext = unencryptedString.getBytes(UTF8);
            byte[] ciphertext = cipher.doFinal(cleartext);
            return Util.base64encode(ciphertext);
        }
        catch (Exception e)
        {
            throw new EncryptionException(e);
        }
    }

    public String decrypt(String encryptedString) throws EncryptionException
    {
        if (Util.isEmptyOrNull(encryptedString))
        {
            throw new IllegalArgumentException("encrypted string is empty");
        }
        try
        {
            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] cleartext = Util.base64decode(encryptedString);
            byte[] ciphertext = cipher.doFinal(cleartext);
            return new String(ciphertext, UTF8);
        }
        catch (Exception e)
        {
            throw new EncryptionException(e);
        }
    }

    public static class EncryptionException extends Exception
    {
        private static final long serialVersionUID = -126262601593604355L;

        public EncryptionException(Throwable t)
        {
            super(t);
        }

        public EncryptionException(String message)
        {
            super(message);
        }
    }
}
