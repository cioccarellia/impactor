package com.andreacioccarelli.impactor.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings({"unused","WeakerAccess","RedundantStringToString","FieldCanBeLocal"})
public class PreferenceBuilder {

    private final SecurePreferences mWriter;
    private Context Context;
    private final String KEY = "Yn-Oz8&qL!1Nz9)S";
    private final String fileName;
    static public final String DefaultFilename = "$";

    public PreferenceBuilder(Context BaseContext,String FileName) {
        mWriter = new SecurePreferences(BaseContext,FileName,KEY,true);
        Context = BaseContext;
        fileName = FileName;
    }

    public final void initializeContext(Context BaseContext) {
        Context = BaseContext;
    }

    public final void initialize(String Filename) {
        SecurePreferences preferences = new SecurePreferences(Context,Filename,KEY,true);
    }

    public final void putBoolean(String key,boolean Value) {
        try {
            mWriter.putBoolean(key,Value);
        } catch (NullPointerException Exception) {
            Exception.printStackTrace();
        }
    }

    public final void putInt(String key,int Value) {
        try {
            mWriter.putInt(key,Value);
        } catch (NullPointerException Exception) {
            Exception.printStackTrace();
        }
    }

    public final void putString(String key,String Value) {
        try {
            mWriter.putString(key,Value);
        } catch (NullPointerException Exception) {
            Exception.printStackTrace();
        }
    }

    public final boolean getBoolean(@NotNull String _BooleanName,boolean ClassDFUValue) {
        return mWriter.getBoolean(_BooleanName,ClassDFUValue);
    }

    public final int getInt(@NotNull String _IntName,int ClassDFUValue) {
        return mWriter.getInt(_IntName,ClassDFUValue);
    }

    @NonNull
    public final String getString(@NotNull String _StringName,@NotNull String ClassDFUValue) {
        return mWriter.getString(_StringName,ClassDFUValue).toString();
    }

    public final void removeBoolean(@NotNull String key) {
        mWriter.remove(key);
    }

    public final void removeInt(@NotNull String key) {
        mWriter.remove(key);
    }

    public final void removeString(@NotNull String key) {
        mWriter.remove(key);
    }

    public final void erasePreferences(boolean areYouSure) {
        if (areYouSure) {
            mWriter.clear();
        }
    }
}

@SuppressWarnings("unused")
class SecurePreferences {

    @SuppressWarnings({"DeserializableClassInSecureContext","SerializableClassInSecureContext"})
    private static class SecurePreferencesException extends RuntimeException {
        SecurePreferencesException(Throwable e) {
            super(e);
        }
    }

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";

    private final boolean encryptKeys;
    private final Cipher writer;
    private final Cipher reader;
    private final Cipher KeyEncrypter;
    private final SharedPreferences MainStream;

    SecurePreferences(Context context,String preferenceName,String secureKey,boolean encryptKeys) throws SecurePreferencesException {
        try {
            writer = Cipher.getInstance(TRANSFORMATION);
            reader = Cipher.getInstance(TRANSFORMATION);
            KeyEncrypter = Cipher.getInstance(KEY_TRANSFORMATION);

            initCiphers(secureKey);

            MainStream = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE);

            this.encryptKeys = encryptKeys;
        }
        catch (GeneralSecurityException | UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    private void initCiphers(String secureKey) throws UnsupportedEncodingException,NoSuchAlgorithmException,InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = getIv();
        SecretKeySpec secretKey = getSecretKey(secureKey);

        writer.init(Cipher.ENCRYPT_MODE,secretKey,ivSpec);
        reader.init(Cipher.DECRYPT_MODE,secretKey,ivSpec);
        KeyEncrypter.init(Cipher.ENCRYPT_MODE,secretKey);
    }

    private IvParameterSpec getIv() {
        byte[] iv = new byte[writer.getBlockSize()];
        java.lang.System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(),0,iv,0,writer.getBlockSize());
        return new IvParameterSpec(iv);
    }

    private SecretKeySpec getSecretKey(String key) throws UnsupportedEncodingException,NoSuchAlgorithmException {
        byte[] keyBytes = createKeyBytes(key);
        return new SecretKeySpec(keyBytes,TRANSFORMATION);
    }

    private byte[] createKeyBytes(String key) throws UnsupportedEncodingException,NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        md.reset();
        return md.digest(key.getBytes(CHARSET));
    }

    public final void putString(String key,String Value) {
        MainStream.edit().putString(encrypt(key,KeyEncrypter),encrypt(Value,writer)).apply();
    }

    @NotNull
    public final String getString(String key,String Default) {
        if (MainStream.contains(encrypt(key,KeyEncrypter))) {
            try {
                return String.valueOf(decrypt(MainStream.getString(encrypt(key,KeyEncrypter),encrypt(String.valueOf(Default),writer))));
            } catch (SecurePreferencesException | IllegalArgumentException e) {
                e.printStackTrace();
                putString(key,Default);
                return Default;
            }
        } else {
            putString(key,Default);
            return Default;
        }
    }

    public final void putInt(String key,int Value) {
        MainStream.edit().putString(encrypt(key,KeyEncrypter),encrypt(String.valueOf(Value),writer)).apply();
    }

    public final int getInt(String key,int Default) {
        if (MainStream.contains(encrypt(key,KeyEncrypter))) {
            try {
                return Integer.parseInt(decrypt(MainStream.getString(encrypt(key,KeyEncrypter),encrypt(String.valueOf(Default),writer))));
            } catch (SecurePreferencesException | IllegalArgumentException e) {
                e.printStackTrace();
                putInt(key,Default);
                return Default;
            }
        } else {
            putInt(key,Default);
            return Default;
        }
    }

    public final void putBoolean(String key,boolean Value) {
        MainStream.edit().putString(encrypt(key,KeyEncrypter),encrypt(String.valueOf(Value),writer)).apply();
    }

    public final boolean getBoolean(String key,boolean Default) {
        if (MainStream.contains(encrypt(key,KeyEncrypter))) {
            try {
                return Boolean.parseBoolean(decrypt(MainStream.getString(encrypt(key,KeyEncrypter),encrypt(String.valueOf(Default),writer))));
            } catch (SecurePreferencesException | IllegalArgumentException e) {
                putBoolean(key,Default);
                return Default;
            }
        } else {
            putBoolean(key,Default);
            return Default;
        }
    }

    final void remove(String key) {
        MainStream.edit().remove(key).apply();
    }


    public final void put(String key,String value) {
        if (value == null) {
            MainStream.edit().remove(toKey(key)).apply();
        } else {
            putValue(toKey(key),value);
        }
    }

    public final boolean containsKey(String key) {
        return MainStream.contains(toKey(key));
    }

    public final void removeValue(String key) {
        MainStream.edit().remove(toKey(key)).apply();
    }

    @Nullable
    public final String getString(String key) throws SecurePreferencesException {
        if (MainStream.contains(toKey(key))) {
            String securedEncodedValue = MainStream.getString(toKey(key),"");
            return decrypt(securedEncodedValue);
        }
        return null;
    }

    final void clear() {
        MainStream.edit().clear().apply();
    }

    private String toKey(String key) {
        return encryptKeys ? encrypt(key,KeyEncrypter) : key;
    }

    private void putValue(String key,String value) throws SecurePreferencesException {
        String secureValueEncoded = encrypt(value,writer);

        MainStream.edit().putString(key,secureValueEncoded).apply();
    }

    private String encrypt(String value,Cipher writer) throws SecurePreferencesException {
        byte[] secureValue;
        try {
            secureValue = convert(writer,value.getBytes(CHARSET));
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
        return Base64.encodeToString(secureValue,Base64.NO_WRAP);
    }

    private String decrypt(String securedEncodedValue) {
        byte[] securedValue = Base64.decode(securedEncodedValue,Base64.NO_WRAP);
        byte[] value = convert(reader,securedValue);
        try {
            return new String(value,CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    private static byte[] convert(Cipher cipher,byte[] bs) throws SecurePreferencesException {
        try {
            return cipher.doFinal(bs);
        }
        catch (Exception e) {
            throw new SecurePreferencesException(e);
        }
    }
}
