package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailedExcursion extends AppCompatActivity {
    VacationRepository vacationRepository;
    int vacationId, excursionId;
    Vacation vacation;
    Excursion excursion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_excursion);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int excursionId = intent.getIntExtra("excursion_id", -1);

        TextView vacationTitle = findViewById(R.id.vacationTitle3);
        TextView excursionTitle = findViewById(R.id.excursionTitle);
        TextView excursionDate = findViewById(R.id.excursionDate);
        Button editExcursionBtn = findViewById(R.id.editExcursionBtn);
//        Button homeButton = findViewById(R.id.homeButton);


        if (excursionId != -1) {
            vacationRepository.getExcursionById(excursionId).observe(this, excursion -> {
                if (excursion != null) {
                    this.excursion = excursion;
                    this.excursionId = excursion.getId();
                    this.vacationId = excursion.getVacationId();
                    excursionTitle.setText(excursion.getExcursionTitle());
                    excursionDate.setText(formatExcursionDate(excursion));
                }

                vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                    if (vacation != null) {
                        this.vacation = vacation;
                        vacationTitle.setText(vacation.getTitle());
                    }
                });

            });
        }

        editExcursionBtn.setOnClickListener(v -> {
            Intent editIntent = new Intent(DetailedExcursion.this, EditExcursion.class);
            editIntent.putExtra("excursion_id", excursionId);
            editIntent.putExtra("vacation_id", vacationId);
            startActivity(editIntent);
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String formatExcursionDate(@NotNull Excursion excursion) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String excursionDate = dateFormat.format(excursion.getExcursionDate());
        return excursionDate;
    }
}