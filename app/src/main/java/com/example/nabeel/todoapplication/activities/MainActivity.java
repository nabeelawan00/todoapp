package com.example.nabeel.todoapplication.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nabeel.todoapplication.R;
import com.example.nabeel.todoapplication.Utils.ShareMemory;
import com.example.nabeel.todoapplication.adapters.PendingTodoAdapter;
import com.example.nabeel.todoapplication.Utils.TagDB;
import com.example.nabeel.todoapplication.Utils.TodoDB;
import com.example.nabeel.todoapplication.auth.Login;
import com.example.nabeel.todoapplication.models.PendingTodoModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {
    private RecyclerView pendingTodos;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PendingTodoModel> pendingTodoModels;
    private PendingTodoAdapter pendingTodoAdapter;
    private FloatingActionButton addNewTodo;
    private TagDB tagDBHelper;
    private String getTagTitleString;
    private TodoDB todoDBHelper;
    private LinearLayout linearLayout;
    ShareMemory shareMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        shareMemory = ShareMemory.getmInstence();
        setTitle(getString(R.string.app_title));
        showDrawerLayout();
        navigationMenuInit();
        loadPendingTodos();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAddTodo:
                if (tagDBHelper.countTags() == 0) {
                    showDialog();
                } else {
                    showNewTodoDialog();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showDrawerLayout() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, (Toolbar) findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void navigationMenuInit() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pending_task_options, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<PendingTodoModel> newPendingTodoModels = new ArrayList<>();
                if (!newPendingTodoModels.isEmpty()) {
                    for (PendingTodoModel pendingTodoModel : pendingTodoModels) {
                        String getTodoTitle = pendingTodoModel.getTodoTitle().toLowerCase();
                        String getTodoContent = pendingTodoModel.getTodoContent().toLowerCase();
                        String getTodoTag = pendingTodoModel.getTodoTag().toLowerCase();

                        if (getTodoTitle.contains(newText) || getTodoContent.contains(newText) || getTodoTag.contains(newText)) {
                            newPendingTodoModels.add(pendingTodoModel);
                        } else {
                            Toast.makeText(MainActivity.this, "Search not match", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pendingTodoAdapter.filterTodos(newPendingTodoModels);
                    pendingTodoAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.pending_todos) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.completed_todos) {
            startActivity(new Intent(this, CompletedTodos.class));
        } else if (id == R.id.tags) {
            startActivity(new Intent(this, AllTags.class));
        } else if (id == R.id.logout) {
            shareMemory.setUserID("");
            Intent intent = new Intent(MainActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tag_create_dialog_title_text);
        builder.setMessage(R.string.no_tag_in_the_db_text);
        builder.setPositiveButton(R.string.create_new_tag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, AllTags.class));
            }
        }).setNegativeButton(R.string.tag_edit_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }


    private void showNewTodoDialog() {

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR);
        final int minute = calendar.get(Calendar.MINUTE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.add_new_todo_dialog, null);
        builder.setView(view);
        final TextInputEditText todoTitle = (TextInputEditText) view.findViewById(R.id.todo_title);
        final TextInputEditText todoContent = (TextInputEditText) view.findViewById(R.id.todo_content);
        Spinner todoTags = (Spinner) view.findViewById(R.id.todo_tag);

        ArrayAdapter<String> tagsModelArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tagDBHelper.fetchTagStrings());

        tagsModelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        todoTags.setAdapter(tagsModelArrayAdapter);
        todoTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getTagTitleString = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final TextInputEditText todoDate = (TextInputEditText) view.findViewById(R.id.todo_date);
        final TextInputEditText todoTime = (TextInputEditText) view.findViewById(R.id.todo_time);

        todoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                        todoDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        todoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        calendar.set(Calendar.HOUR_OF_DAY, i);
                        calendar.set(Calendar.MINUTE, i1);
                        String timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
                        todoTime.setText(timeFormat);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView addTodo = (TextView) view.findViewById(R.id.add_new_todo);
        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getTodoTitle = todoTitle.getText().toString();
                String getTodoContent = todoContent.getText().toString();
                int todoTagID = tagDBHelper.fetchTagID(getTagTitleString);
                String getTodoDate = todoDate.getText().toString();
                String getTime = todoTime.getText().toString();

                boolean isTitleEmpty = todoTitle.getText().toString().isEmpty();
                boolean isContentEmpty = todoContent.getText().toString().isEmpty();
                boolean isDateEmpty = todoDate.getText().toString().isEmpty();
                boolean isTimeEmpty = todoTime.getText().toString().isEmpty();


                if (isTitleEmpty) {
                    todoTitle.setError("Todo title required !");
                } else if (isContentEmpty) {
                    todoContent.setError("Todo content required !");
                } else if (isDateEmpty) {
                    todoDate.setError("Todo date required !");
                } else if (isTimeEmpty) {
                    todoTime.setError("Todo time required !");
                } else if (todoDBHelper.addNewTodo(
                        new PendingTodoModel(getTodoTitle, getTodoContent, String.valueOf(todoTagID), getTodoDate, getTime)
                )) {
                    Toast.makeText(MainActivity.this, R.string.todo_title_add_success_msg, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
        builder.create().show();
    }

    private void loadPendingTodos() {
        pendingTodos = (RecyclerView) findViewById(R.id.pending_todos_view);
        linearLayout = (LinearLayout) findViewById(R.id.no_pending_todo_section);
        tagDBHelper = new TagDB(this);
        todoDBHelper = new TodoDB(this);

        if (todoDBHelper.countTodos() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
            pendingTodos.setVisibility(View.GONE);
        } else {
            pendingTodoModels = new ArrayList<>();
            pendingTodoModels = todoDBHelper.fetchAllTodos();
            pendingTodoAdapter = new PendingTodoAdapter(pendingTodoModels, this);
        }
        linearLayoutManager = new LinearLayoutManager(this);
        pendingTodos.setAdapter(pendingTodoAdapter);
        pendingTodos.setLayoutManager(linearLayoutManager);
        addNewTodo = (FloatingActionButton) findViewById(R.id.fabAddTodo);
        addNewTodo.setOnClickListener(this);
    }
}
