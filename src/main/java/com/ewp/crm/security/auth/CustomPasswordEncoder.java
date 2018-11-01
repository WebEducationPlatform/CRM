package com.ewp.crm.security.auth;

import com.ewp.crm.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomPasswordEncoder implements PasswordEncoder {
    private static Logger logger = LoggerFactory.getLogger(CustomPasswordEncoder.class);

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[24];
        sr.nextBytes(salt);
        return salt;
    }

    private static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException ex)
        {
            logger.error("No such algorithm exception.",ex);
        }

        return generatedPassword;
    }

    private Map<String, Object> encodePasswordSHA(String password, byte[] salt) {
        Map<String, Object> resHash = new HashMap<>();
        if (salt == null) {
            try {
                salt = getSalt();
            }
            catch (NoSuchAlgorithmException ex) {
                logger.error("Error with generate salt for password. No such algorithm exists", ex);
            }
        }
        String passEncode = null;
        if (salt != null) {
            passEncode = get_SHA_512_SecurePassword(password, salt);
        }
        resHash.put("salt", salt);
        resHash.put("pass", passEncode);

        return resHash;
    }

    public void encodePasswordForUser(User user) {
        Map<String, Object> wrapHash = encodePasswordSHA(user.getPassword(), null);
        user.setPassword((String) wrapHash.get("pass"));
        user.setSalt((byte[]) wrapHash.get("salt"));
    }

    public String encodePassword(String password, byte[] salt) {
        Map<String, Object> wrapHash = encodePasswordSHA(password, salt);

        return (String) wrapHash.get("pass");
    }

    @Override
    public String encode(CharSequence charSequence) {
        return null;
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return (charSequence != null &&
                !((String)charSequence).isEmpty() &&
                charSequence.equals(s));
    }
}