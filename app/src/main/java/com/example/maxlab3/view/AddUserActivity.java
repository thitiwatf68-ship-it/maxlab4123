package com.example.maxlab3.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.maxlab3.R;
import com.example.maxlab3.controller.UserController;
import com.example.maxlab3.model.DataStore;
import com.example.maxlab3.model.User;

import java.util.ArrayList;
import java.util.List;

/** View: รับข้อมูลผู้ใช้จากหน้าจอแล้วส่งให้ UserController, แสดงรายชื่อผู้ใช้ */
public class AddUserActivity extends AppCompatActivity implements DataStore.OnDataChangedListener {

    private final UserController userController = new UserController();
    private EditText etUsername, etEmail, etPassword;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnSaveUser = findViewById(R.id.btnSaveUser);
        ListView listUsers = findViewById(R.id.listUsers);
        listUsers.setEmptyView(findViewById(R.id.tvNoUsers));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listUsers.setAdapter(adapter);

        btnSaveUser.setOnClickListener(v -> saveUser());
    }

    @Override
    protected void onStart() {
        super.onStart();
        userController.addListener(this);
        refreshUsers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userController.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        refreshUsers();
    }

    private void saveUser() {
        // View ไม่ตัดสินใจเอง: ส่งต่อให้ Controller แล้วแสดงผลตามที่ได้กลับมา
        UserController.Error error = userController.createUser(
                etUsername.getText().toString(),
                etEmail.getText().toString(),
                etPassword.getText().toString());

        switch (error) {
            case USERNAME_REQUIRED:
                etUsername.setError(getString(R.string.error_required));
                break;
            case USERNAME_TAKEN:
                etUsername.setError(getString(R.string.error_username_taken));
                break;
            case EMAIL_INVALID:
                etEmail.setError(getString(R.string.error_email));
                break;
            case PASSWORD_TOO_SHORT:
                etPassword.setError(getString(R.string.error_password));
                break;
            case NONE:
                Toast.makeText(this, R.string.user_saved, Toast.LENGTH_SHORT).show();
                etUsername.setText("");
                etEmail.setText("");
                etPassword.setText("");
                etUsername.requestFocus();
                break;
        }
    }

    private void refreshUsers() {
        List<String> rows = new ArrayList<>();
        for (User u : userController.getUsers()) {
            rows.add(getString(R.string.user_row_format,
                    u.getUserId(), u.getUsername(), u.getEmail(), u.getUserNotes().size()));
        }
        adapter.clear();
        adapter.addAll(rows);
        adapter.notifyDataSetChanged();
    }
}
