package com.example.healthpet3.ui.notifications;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthpet3.R;
import java.util.Objects;

public class StatusNotificationsActivity extends AppCompatActivity {
    private StatusNotificationsViewModel viewModel;
    private StatusNotificationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_notifications);

        RecyclerView recyclerView = findViewById(R.id.recyclerStatusNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatusNotificationAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(StatusNotificationsViewModel.class);
        viewModel.getNotifications().observe(this, notifications -> {
            adapter.submitList(notifications);
        });
        viewModel.loadNotifications();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}