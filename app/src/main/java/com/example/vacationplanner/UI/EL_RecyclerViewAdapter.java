package com.example.vacationplanner.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationplanner.R;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;

import java.util.List;

public class EL_RecyclerViewAdapter extends RecyclerView.Adapter<EL_RecyclerViewAdapter.MyViewHolder>{
    Context context;
    List<Excursion> excursionList;
    EL_RecyclerViewAdapter.ExcursionItemClickListener clickListener;


    public interface ExcursionItemClickListener {
        void excursionItemClick(Excursion excursion);
    }

    public EL_RecyclerViewAdapter(Context context, List<Excursion> excursionList, EL_RecyclerViewAdapter.ExcursionItemClickListener clickListener) {
        this.context = context;
        this.excursionList = excursionList;
        this.clickListener = clickListener;

    }

    public void setExcursion(List<Excursion> excursions) {
        this.excursionList = excursions;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public EL_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This inflates the layout giving a look to the rows
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.excursion_view_row, parent, false);
        return new EL_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EL_RecyclerViewAdapter.MyViewHolder holder, int position) {
        //assigning values to the views created in the recycler_view_row_layout file
        //based on position of the recycler view
        Excursion currentExcursion = excursionList.get(position);
        holder.excursionListItem.setText(currentExcursion.getExcursionTitle());

        //enables row in recycler to be clickable for retrieving excursion
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.excursionItemClick(currentExcursion);
            }
        });


    }



    @Override
    public int getItemCount() {
        return excursionList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView excursionListItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            excursionListItem = itemView.findViewById(R.id.excursionListItem);
        }
    }
}
