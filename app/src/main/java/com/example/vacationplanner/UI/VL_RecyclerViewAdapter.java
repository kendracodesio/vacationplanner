package com.example.vacationplanner.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationplanner.R;
import com.example.vacationplanner.entities.Vacation;

import java.util.List;

public class VL_RecyclerViewAdapter extends RecyclerView.Adapter<VL_RecyclerViewAdapter.MyViewHolder>{
    Context context;
    List<Vacation> vacationList;

    VacationItemClickListener clickListener;


    public interface VacationItemClickListener {
        void vacationItemClick(Vacation vacation);
    }

    public VL_RecyclerViewAdapter(Context context, List<Vacation> vacationList, VacationItemClickListener clickListener) {
        this.context = context;
        this.vacationList = vacationList;
        this.clickListener = clickListener;

    }

    public void setVacations(List<Vacation> vacations) {
        this.vacationList = vacations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VL_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This inflates the layout giving a look to the rows
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new VL_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull VL_RecyclerViewAdapter.MyViewHolder holder, int position) {
        //assigning values to the views created in the recycler_view_row_layout file
        //based on position of the recycler view
        Vacation currentVacation = vacationList.get(position);
        holder.vacationListItem.setText(currentVacation.getTitle());

        //enables row in recycler to be clickable for retrieving vacation
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.vacationItemClick(currentVacation);
            }
        });


    }

    @Override
    //gets the number of items to display
    public int getItemCount() {
        return vacationList.size();
    }

    //grabs the view from the recycler_view_row_layout file
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView vacationListItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            vacationListItem = itemView.findViewById(R.id.mVacationListItem);
        }
    }
}
