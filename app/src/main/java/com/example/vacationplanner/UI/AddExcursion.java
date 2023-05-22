package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.textfield.TextInputEditText;

public class AddExcursion extends AppCompatActivity {

    VacationRepository vacationRepository;
    private int vacationId;
    private Vacation vacation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_excursion);

        vacationRepository = new VacationRepository(this.getApplication());

        Intent intent = getIntent();
        vacationId = intent.getIntExtra("vacation_id", -1);

        TextView vacationTitle = findViewById(R.id.vacationTitle2);
        TextInputEditText excursionTitleInput = findViewById(R.id.excursionTitleInput);
        Button createExcursionBtn = findViewById(R.id.createExcursionBtn);


        if (vacationId != -1) {

            vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    this.vacation = vacation;
                    this.vacationId = vacation.getId();
                    vacationTitle.setText(vacation.getTitle());

                }
            });


        }

        createExcursionBtn.setOnClickListener(v -> {
            String excursionTitle = excursionTitleInput.getText().toString();

            Excursion excursion = new Excursion();
            excursion.setVacationId(vacationId);
            excursion.setExcursionTitle(excursionTitle);
            Log.d("AddExcursion", excursion.getExcursionTitle());
            vacationRepository.insertExcursion(excursion);

            Toast.makeText(AddExcursion.this, "Excursion created", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}