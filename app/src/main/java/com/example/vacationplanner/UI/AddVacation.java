package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.dao.VacationDao;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.textfield.TextInputEditText;

import java.text.BreakIterator;

public class AddVacation extends AppCompatActivity {

//    private Calendar startDate;
//    private Calendar endDate;
    private VacationRepository vacationRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacation);

        vacationRepository = new VacationRepository(this.getApplication());
//        Button pickDateButton = findViewById(R.id.pickDateButton);

        Button createVacationButton = findViewById(R.id.createVacationBtn);
        createVacationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText titleInput = findViewById(R.id.vacationTitleInput);
                String vacationTitle = titleInput.getText().toString();

                Vacation vacation = new Vacation();
                vacation.setTitle(vacationTitle);

                vacationRepository.insertVacation(vacation);

                Toast.makeText(AddVacation.this, "Vacation created", Toast.LENGTH_SHORT).show();
                finish();


            }
        });

    }
}