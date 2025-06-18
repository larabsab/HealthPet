package com.example.healthpet3.ui.notifications;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthpet3.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatusNotificationAdapter extends RecyclerView.Adapter<StatusNotificationAdapter.ViewHolder> {
    private List<StatusNotificationsViewModel.StatusNotificationItem> notifications = new ArrayList<>();

    public void submitList(List<StatusNotificationsViewModel.StatusNotificationItem> list) {
        this.notifications = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatusNotificationsViewModel.StatusNotificationItem item = notifications.get(position);
        holder.txtPetName.setText(item.petName);
        holder.txtStatusType.setText(item.status.getType());
        holder.txtVetName.setText(item.status.getVetName() != null ? item.status.getVetName() : "");
        holder.txtStatusDate.setText(formatDate(item.status.getTimestamp() != null ? item.status.getTimestamp().toDate() : null));
        if (item.status.getIcon() != null && !item.status.getIcon().isEmpty()) {
            int resId = holder.itemView.getContext().getResources().getIdentifier(item.status.getIcon(), "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.imgStatusIcon.setImageResource(resId);
            } else {
                holder.imgStatusIcon.setImageResource(R.drawable.statusicon);
            }
        } else {
            holder.imgStatusIcon.setImageResource(R.drawable.statusicon);
        }
        holder.btnMore.setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            Intent intent = new Intent(context, com.example.healthpet3.ui.pet.PetProfileActivity.class);
            intent.putExtra("petId", item.petId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPetName, txtStatusType, txtVetName, txtStatusDate;
        ImageView imgStatusIcon;
        LinearLayout btnMore;
        ViewHolder(View itemView) {
            super(itemView);
            txtPetName = itemView.findViewById(R.id.txtPetName);
            txtStatusType = itemView.findViewById(R.id.txtStatusType);
            txtVetName = itemView.findViewById(R.id.txtVetName);
            txtStatusDate = itemView.findViewById(R.id.txtStatusDate);
            imgStatusIcon = itemView.findViewById(R.id.imgStatusIcon);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}