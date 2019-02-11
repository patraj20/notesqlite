package com.example.patankar.notesqlite;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.patankar.notesqlite.database.DatabaseHelper;
import com.example.patankar.notesqlite.database.model.Notes;
import com.example.patankar.notesqlite.utils.MyDividerItemDecoration;
import com.example.patankar.notesqlite.utils.RecyclerTouchListener;
import com.example.patankar.notesqlite.view.NotesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NotesAdapter notesAdapter;
    private CoordinatorLayout coordinatorLayout;
    private List<Notes> notesList;
    private RecyclerView recyclerView;
    private TextView NoNotesView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        NoNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);
        notesList = new ArrayList<>();
        notesList.addAll(db.getAllNotes());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        notesAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(notesAdapter);

        toggleEmptyNotes();


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showsActionDialog(position);
            }
        }));
    }


    private void showNoteDialog(final boolean shouldUpdate,final Notes note,final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.note_dialog,null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogtitle = view.findViewById(R.id.title);
        dialogtitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if(shouldUpdate && note != null) {
            inputNote.setText(note.getNote());

        }
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "Update" : "save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                if (shouldUpdate && note != null) {
                    updateNote(inputNote.getText().toString(), position);
                }
                else {
                    createNote(inputNote.getText().toString());
                }
            }
        });

    }

    private  void showsActionDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit","Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0) {
                    showNoteDialog(true,notesList.get(position),position);
                }
                else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    private void updateNote( String note,int position) {

        Notes n = notesList.get(position);
        n.setNote(note);

        db.updateNOte(n);

        notesList.set(position,n);
        notesAdapter.notifyDataSetChanged();

        toggleEmptyNotes();
    }


    private void createNote( String  note) {
        long id = db.insertNote(note);

        Notes n = db.getNote(id);

        if(n != null ) {
            notesList.add(0,n);
            notesAdapter.notifyDataSetChanged();
            toggleEmptyNotes();
        }
    }

    private void deleteNote(int position) {
        db.deleteNote(notesList.get(position));

        notesList.remove(position);
        notesAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    private void toggleEmptyNotes() {
        if (db.getNotesCount() > 0) {
            NoNotesView.setVisibility(View.GONE);
        } else {
            NoNotesView.setVisibility(View.VISIBLE);
        }
    }

}
