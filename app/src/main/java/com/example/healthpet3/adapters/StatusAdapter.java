package com.example.healthpet3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.app.AlertDialog;
import android.content.Context;

import com.example.healthpet3.R;
import com.example.healthpet3.models.Status;
import com.example.healthpet3.utils.TimeUtils;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private Context context;
    private List<Status> statusList;

    public StatusAdapter(Context context, List<Status> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_status, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        Status status = statusList.get(position);

        holder.titleTextView.setText(status.getType());
        holder.vetNameTextView.setText(status.getVetName());
        holder.timeTextView.setText(TimeUtils.getRelativeTime(status.getTimestamp()));

//        holder.timeTextView.setText(status.getFormattedTime()); // certifique-se que existe esse método
        // Imagem circular com fundo A9D8D6 já no XML (use ImageView de ícone aqui se tiver)

        holder.moreButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_status_details, null);

            TextView title = dialogView.findViewById(R.id.detailTitle);
            TextView vet = dialogView.findViewById(R.id.detailVet);
            TextView time = dialogView.findViewById(R.id.detailTime);
            TextView statuspet = dialogView.findViewById(R.id.detailStatus);
            TextView fullDetails = dialogView.findViewById(R.id.detailFullText);

            title.setText("🐾 Tipo: " + status.getType());
            statuspet.setText("🩺 Status: " + status.getStatus());
            vet.setText("👩‍⚕️ Veterinário: " + status.getVetName());
            time.setText("📅 Data: " + TimeUtils.getRelativeTime(status.getTimestamp()));
            fullDetails.setText("💬 Descrição: " + status.getDescription());

            builder.setView(dialogView);
            builder.setPositiveButton("Fechar", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, vetNameTextView, timeTextView;
        LinearLayout moreButton;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.statusTitle);
            vetNameTextView = itemView.findViewById(R.id.statusVet);
            timeTextView = itemView.findViewById(R.id.statusTime);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }



    public void setStatusList(List<Status> list) {
        this.statusList = list;
        notifyDataSetChanged();
    }
}