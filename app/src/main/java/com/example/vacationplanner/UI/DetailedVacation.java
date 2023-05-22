package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailedVacation extends AppCompatActivity {

    VacationRepository vacationRepository;

    int vacationId;
    Vacation vacation;

    List<Excursion> excursionList;

    EL_RecyclerViewAdapter el_recyclerViewAdapter;
    RecyclerView excursionListRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_vacation);


        vacationRepository = new VacationRepository(getApplication());
        Button addExcursionBtn = findViewById(R.id.addExcursionBtn);

        Intent intent = getIntent();
        int vacationId = intent.getIntExtra("vacation_id", -1);

        TextView vacationTitle = findViewById(R.id.vacationTitle);
        TextView vacationDetails = findViewById(R.id.vacationDetails);

        if (vacationId != -1) {

            vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    this.vacation = vacation;
                    this.vacationId = vacation.getId();
                    vacationTitle.setText(vacation.getTitle());
                    vacationDetails.setText(formatVacationDetails(vacation));

                }
            });

        }

        excursionListRecyclerView = findViewById(R.id.excursionList);
        excursionList = new ArrayList<>();
        vacationRepository = new VacationRepository(getApplication());
        //Initialize the RecyclerView Adapter
        //gets the excursionId when excursion item is clicked so details can be shown in the DetailedExcursion activity
        el_recyclerViewAdapter = new EL_RecyclerViewAdapter(DetailedVacation.this, excursionList, excursion -> {
            Intent showExcursionDetails = new Intent(DetailedVacation.this, DetailedExcursion.class);
            Log.d("DetailedVacation", "Excursion Id: to pass to DetailedExcursion: " + excursion.getId());
            showExcursionDetails.putExtra("excursion_id", excursion.getId());
            startActivity(intent);
        });

        //Set the adapter to the RecyclerView
        excursionListRecyclerView.setAdapter(el_recyclerViewAdapter);
        excursionListRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        vacationRepository.getExcursionsByVacation(vacationId).observe(this, excursions -> {
            //update the excursions in the adapter
            el_recyclerViewAdapter.setExcursion(excursions);
        });

        addExcursionBtn.setOnClickListener(v -> {
            Intent addExcursionIntent = new Intent(DetailedVacation.this, AddExcursion.class);
            addExcursionIntent.putExtra("vacation_id", vacation.getId());
            startActivity(addExcursionIntent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vacation_detail_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_edit) {
            Intent editIntent = new Intent(DetailedVacation.this, EditVacation.class );
            editIntent.putExtra("vacation_id", vacationId);
            startActivity(editIntent);
            return true;

        } else {
            return super.onOptionsItemSelected(item);

        }
    }


    private String formatVacationDetails(@NotNull Vacation vacation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String startDate = dateFormat.format(vacation.getStartDate());
        String endDate = dateFormat.format(vacation.getEndDate());
        return "Dates: " + startDate + " - " + endDate + "\n Lodging: " + vacation.getLodgingInfo();
    }
}
