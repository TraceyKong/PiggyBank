package com.example.piggybank.ParentActivities;

import android.content.Intent;
import android.os.Bundle;

import com.example.piggybank.Firebase.Get;
import com.example.piggybank.Firebase.Models.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.piggybank.R;

import java.util.ArrayList;

public class TasksParentActivity extends AppCompatActivity {

    private String childId;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_parent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //title toolbar
        String name = getIntent().getExtras().getString("name");
        getSupportActionBar().setTitle(name+"'s Tasks");
        this.childId = getIntent().getExtras().getString("childId");
        //setup recyclerview
        recyclerView = findViewById(R.id.tasksRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        makeRV();
    }

    //opens the ChildOverviewParentActivity
    public void openOverviewActivity(View view) {
        Intent intent = new Intent(TasksParentActivity.this, ChildOverviewParentActivity.class);
        String name = getIntent().getExtras().getString("name");
        intent.putExtra("name", name);
        intent.putExtra("childId", childId);
        startActivity(intent);
    }

    //opens the TransactionsParentActivity
    public void openTransactionsActivity(View view) {
        Intent intent = new Intent(TasksParentActivity.this, ChildOverviewParentActivity.class);//todo transactions
        String name = getIntent().getExtras().getString("name");
        intent.putExtra("name", name);
        intent.putExtra("childId", childId);
        startActivity(intent);
    }

    //opens the CreateTaskActivity
    public void openCreateTaskActivity(View view) {
        Intent intent = new Intent(TasksParentActivity.this, CreateTaskActivity.class);
        intent.putExtra("childId", childId);
        intent.putExtra("name", getIntent().getExtras().getString("name"));
        startActivity(intent);
    }

    //makes the recyclerview for tasks
    private void makeRV() {
        Get get = new Get();
        get.getTasksByChildId(childId, new OnSuccessListener<ArrayList<Task>>() {
            @Override
            public void onSuccess(ArrayList<Task> tasks) {
                adapter = new TasksAdapter(tasks);
                recyclerView.setAdapter(adapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        });
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        makeRV();
    }

    //recyclerview adapter for tasks
    private class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskHolder> {

        private ArrayList<Task> tasks;
        public TasksAdapter(ArrayList<Task> tasks) {
            this.tasks = tasks;
        }
        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_task, parent, false);
            return new TaskHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            //get a task
            final Task task = tasks.get(position);
            //set the name of the task to the textview
            holder.task.setText(task.getName());
            //add status icon to the right of the textview based on the staus of the task
            if(task.getStatus().equals("incomplete"))
                holder.task.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_close,0);
            if(task.getStatus().equals("completed"))
                holder.task.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_remove,0);
            if(task.getStatus().equals("confirmed"))
                holder.task.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_done,0);
            //adds onclick to the textview that will open this task's details in a new activity
            holder.task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TasksParentActivity.this, TaskDetailsParentActivity.class);
                    intent.putExtra("name", getIntent().getExtras().getString("name"));
                    intent.putExtra("payment", task.getPayment());
                    intent.putExtra("status", task.getStatus());
                    intent.putExtra("taskName", task.getName());
                    intent.putExtra("description", task.getDescription());
                    intent.putExtra("id", task.getId());
                    intent.putExtra("childId", childId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        private class TaskHolder extends RecyclerView.ViewHolder {
            private TextView task;

            public TaskHolder(@NonNull View itemView) {
                super(itemView);
                this.task = itemView.findViewById(R.id.taskName);
            }
        }
    }

}
