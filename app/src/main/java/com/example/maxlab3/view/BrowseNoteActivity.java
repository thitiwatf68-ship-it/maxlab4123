package com.example.maxlab3.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.maxlab3.R;
import com.example.maxlab3.controller.NoteController;
import com.example.maxlab3.model.Note;

import java.util.List;

/**
 * View: หน้าเรียกดู/ค้นหาโน้ตของ User
 * เริ่มต้นเป็นหน้าว่าง เมื่อกดค้นหา -> แสดง ProgressBar ระหว่างที่ Controller ค้นหาใน Thread แยก
 * (หน่วง 2 วินาที) แล้วแสดง "ไม่พบข้อมูล" หรือรายการโน้ตที่พบ
 */
public class BrowseNoteActivity extends AppCompatActivity {

    private final NoteController noteController = new NoteController();

    private EditText etSearchUser;
    private Button btnSearch;
    private View layoutSearching;
    private TextView tvSearchResult;
    private ListView listResults;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etSearchUser = findViewById(R.id.etSearchUser);
        btnSearch = findViewById(R.id.btnSearch);
        layoutSearching = findViewById(R.id.layoutSearching);
        tvSearchResult = findViewById(R.id.tvSearchResult);
        listResults = findViewById(R.id.listResults);

        adapter = new NoteAdapter(this);
        listResults.setAdapter(adapter);

        // Event: กดปุ่มค้นหา
        btnSearch.setOnClickListener(v -> search());
    }

    private void search() {
        String keyword = etSearchUser.getText().toString();
        if (!noteController.isValidKeyword(keyword)) {
            etSearchUser.setError(getString(R.string.error_required));
            return;
        }

        // เริ่มค้นหา: แสดง ProgressBar, ซ่อนผลเก่า, กันกดซ้ำ
        setSearching(true);
        tvSearchResult.setVisibility(View.GONE);
        adapter.clear();

        noteController.searchNotesByUser(keyword, result -> runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            setSearching(false);
            showResult(result);
        }));
    }

    private void showResult(List<Note> notes) {
        if (notes == null || notes.isEmpty()) {
            tvSearchResult.setText(R.string.not_found);   // "ไม่พบข้อมูล"
            tvSearchResult.setVisibility(View.VISIBLE);
        } else {
            adapter.addAll(notes);
            adapter.notifyDataSetChanged();
        }
    }

    private void setSearching(boolean searching) {
        layoutSearching.setVisibility(searching ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!searching);
    }
}
