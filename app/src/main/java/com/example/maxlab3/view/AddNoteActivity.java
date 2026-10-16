package com.example.maxlab3.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.maxlab3.R;
import com.example.maxlab3.controller.NoteController;
import com.example.maxlab3.controller.UserController;
import com.example.maxlab3.model.DataStore;
import com.example.maxlab3.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity implements DataStore.OnDataChangedListener {

    private final NoteController noteController = new NoteController();
    private final UserController userController = new UserController();
    private final List<String> pendingItems = new ArrayList<>();

    private Spinner spinnerOwner;
    private TextView tvNoUser;
    private Button btnGoAddUser, btnSave, btnAddItem;
    private RadioGroup rgType;
    private EditText etTitle, etContent, etItem;
    private View layoutText, layoutChecklist;
    private TextView tvPendingItems;
    private ArrayAdapter<String> ownerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerOwner = findViewById(R.id.spinnerOwner);
        tvNoUser = findViewById(R.id.tvNoUser);
        btnGoAddUser = findViewById(R.id.btnGoAddUser);
        rgType = findViewById(R.id.rgType);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etItem = findViewById(R.id.etItem);
        layoutText = findViewById(R.id.layoutText);
        layoutChecklist = findViewById(R.id.layoutChecklist);
        tvPendingItems = findViewById(R.id.tvPendingItems);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnSave = findViewById(R.id.btnSaveNote);

        ownerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>());
        spinnerOwner.setAdapter(ownerAdapter);

        // Event: เปลี่ยนชนิดโน้ต -> สลับช่องกรอกข้อมูล
        rgType.setOnCheckedChangeListener((group, checkedId) -> updateTypeUI());

        // Event: กดปุ่มเพิ่มรายการ checklist
        btnAddItem.setOnClickListener(v -> addPendingItem());

        // Event: กดปุ่มไปหน้าเพิ่มผู้ใช้
        btnGoAddUser.setOnClickListener(v ->
                startActivity(new Intent(AddNoteActivity.this, AddUserActivity.class)));

        // Event: กดบันทึกโน้ต
        btnSave.setOnClickListener(v -> saveNote());

        updateTypeUI();
        renderPendingItems();
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteController.addListener(this);
        refreshOwners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteController.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        refreshOwners();
    }

    // ---------- UI ----------
    private void refreshOwners() {
        int selected = spinnerOwner.getSelectedItemPosition();
        List<String> names = new ArrayList<>();
        for (User u : userController.getUsers()) {
            names.add(u.getUsername() + " (" + u.getEmail() + ")");
        }
        ownerAdapter.clear();
        ownerAdapter.addAll(names);
        ownerAdapter.notifyDataSetChanged();
        if (selected >= 0 && selected < names.size()) {
            spinnerOwner.setSelection(selected);
        }

        boolean hasUser = !names.isEmpty();
        spinnerOwner.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        tvNoUser.setVisibility(hasUser ? View.GONE : View.VISIBLE);
        btnGoAddUser.setVisibility(hasUser ? View.GONE : View.VISIBLE);
        btnSave.setEnabled(hasUser);
    }

    private boolean isChecklistMode() {
        return rgType.getCheckedRadioButtonId() == R.id.rbChecklist;
    }

    private void updateTypeUI() {
        boolean checklist = isChecklistMode();
        layoutText.setVisibility(checklist ? View.GONE : View.VISIBLE);
        layoutChecklist.setVisibility(checklist ? View.VISIBLE : View.GONE);
    }

    private void addPendingItem() {
        String item = etItem.getText().toString().trim();
        if (item.isEmpty()) {
            etItem.setError(getString(R.string.error_required));
            return;
        }
        pendingItems.add(item);
        etItem.setText("");
        renderPendingItems();
    }

    private void renderPendingItems() {
        if (pendingItems.isEmpty()) {
            tvPendingItems.setText(R.string.no_items_yet);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pendingItems.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append("\u2022 ").append(pendingItems.get(i));
        }
        tvPendingItems.setText(sb.toString());
    }

    // ---------- Save ----------
    private void saveNote() {
        List<User> users = userController.getUsers();
        int pos = spinnerOwner.getSelectedItemPosition();
        User owner = (pos >= 0 && pos < users.size()) ? users.get(pos) : null;
        String title = etTitle.getText().toString();

        // View แค่รวบรวมข้อมูลจากหน้าจอ แล้วส่งให้ Controller ตัดสินใจ/สร้าง Model
        NoteController.Error error;
        if (isChecklistMode()) {
            // ถ้ายังมีข้อความค้างในช่องรายการ ให้นับเป็นรายการสุดท้ายด้วย
            String lastItem = etItem.getText().toString().trim();
            if (!lastItem.isEmpty()) pendingItems.add(lastItem);
            error = noteController.addChecklistNote(owner, title, pendingItems);
        } else {
            error = noteController.addTextNote(owner, title, etContent.getText().toString());
        }

        switch (error) {
            case NO_OWNER:
                Toast.makeText(this, R.string.error_no_user, Toast.LENGTH_SHORT).show();
                break;
            case TITLE_REQUIRED:
                etTitle.setError(getString(R.string.error_required));
                break;
            case NO_ITEMS:
                etItem.setError(getString(R.string.error_need_item));
                break;
            case NONE:
                Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                finish(); // กลับหน้า MainActivity (รีเฟรชเองผ่าน Listener)
                break;
        }
    }
}
