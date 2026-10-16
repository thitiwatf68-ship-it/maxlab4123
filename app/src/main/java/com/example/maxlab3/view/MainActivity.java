package com.example.maxlab3.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.maxlab3.R;
import com.example.maxlab3.controller.NoteController;
import com.example.maxlab3.model.DataStore;
import com.example.maxlab3.model.Note;
import com.example.maxlab3.model.User;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DataStore.OnDataChangedListener {

    private final NoteController noteController = new NoteController();
    private NoteAdapter adapter;
    private Button btnBrowseNote;
    private View layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnAddNote = findViewById(R.id.btnAddNote);
        Button btnAddUser = findViewById(R.id.btnAddUser);
        btnBrowseNote = findViewById(R.id.btnBrowseNote);
        layoutLoading = findViewById(R.id.layoutLoading);
        ListView listNotes = findViewById(R.id.listNotes);

        adapter = new NoteAdapter(this);
        listNotes.setAdapter(adapter);
        listNotes.setEmptyView(findViewById(R.id.tvEmpty));

        // Event: กดปุ่ม -> เปลี่ยนไปหน้า AddNoteActivity
        btnAddNote.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));

        // Event: กดปุ่ม -> เปลี่ยนไปหน้า AddUserActivity
        btnAddUser.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddUserActivity.class)));

        // Event: กดปุ่ม -> โหลดข้อมูลใน Thread แยก (2 วินาที) แล้วไปหน้า BrowseNoteActivity
        btnBrowseNote.setOnClickListener(v -> loadThenOpenBrowse());

        // Event: แตะที่โน้ต -> แสดงรายละเอียดพร้อมเจ้าของ
        listNotes.setOnItemClickListener((parent, view, position, id) ->
                showNoteDetail(adapter.getItem(position)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteController.addListener(this);   // Model เปลี่ยน -> View รีเฟรช
        refreshNotes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteController.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        refreshNotes();
    }

    private void loadThenOpenBrowse() {
        setLoading(true);   // แสดง ProgressBar + กันกดซ้ำ

        // งานหน่วง 2 วินาทีทำงานใน Worker Thread (ไม่บล็อก UI Thread)
        noteController.loadBrowseData(success -> runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;   // ผู้ใช้ออกจากหน้าไปแล้ว
            setLoading(false);
            startActivity(new Intent(MainActivity.this, BrowseNoteActivity.class));
        }));
    }

    private void setLoading(boolean loading) {
        layoutLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnBrowseNote.setEnabled(!loading);
    }

    private void refreshNotes() {
        adapter.clear();
        adapter.addAll(noteController.getAllNotes());
        adapter.notifyDataSetChanged();
    }

    private void showNoteDetail(Note note) {
        if (note == null) return;
        User owner = note.getOwner();
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(note.getCreatedDate());
        String header = getString(R.string.detail_owner, owner != null ? owner.getUsername() : "-")
                + "\n" + getString(R.string.detail_email, owner != null ? owner.getEmail() : "-")
                + "\n" + getString(R.string.detail_date, date);

        new AlertDialog.Builder(this)
                .setTitle(note.getTitle())
                .setMessage(header + "\n\n" + note.getDetail())
                .setPositiveButton(R.string.close, null)
                .show();
    }
}
