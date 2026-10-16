package com.example.maxlab3.controller;

import com.example.maxlab3.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller: ตัวกลางระหว่าง AddUserActivity / AddNoteActivity (View) กับ User (Model)
 * รับผิดชอบตรวจสอบข้อมูลผู้ใช้ แฮชรหัสผ่าน และสร้าง User
 */
public class UserController extends BaseController {

    public enum Error {
        NONE, USERNAME_REQUIRED, USERNAME_TAKEN, EMAIL_INVALID, PASSWORD_TOO_SHORT
    }

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Error createUser(String username, String email, String password) {
        String name = username == null ? "" : username.trim();
        String mail = email == null ? "" : email.trim();

        if (name.isEmpty()) return Error.USERNAME_REQUIRED;
        if (store.isUsernameTaken(name)) return Error.USERNAME_TAKEN;
        if (!EMAIL.matcher(mail).matches()) return Error.EMAIL_INVALID;
        if (password == null || password.length() < 4) return Error.PASSWORD_TOO_SHORT;

        store.createUser(name, mail, sha256(password));
        return Error.NONE;
    }

    public List<User> getUsers() {
        return store.getUsers();
    }

    private static String sha256(String text) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
