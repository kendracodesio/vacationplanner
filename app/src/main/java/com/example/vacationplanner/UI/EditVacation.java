package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;

public class EditVacation extends AppCompatActivity {

    VacationRepository vacationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vacation);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int vacationId = intent.getIntExtra("vacation_id", -1);

        EditText vacationTitleEdit = findViewById(R.id.vacationTitleEdit);
        Button saveBtn = findViewById(R.id.saveBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);

        if (vacationId != -1) {
            vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    vacationTitleEdit.setText(vacation.getTitle());

                    saveBtn.setOnClickListener(v -> {
                        String updatedVacationTitle = vacationTitleEdit.getText().toString();
                            vacation.setTitle(updatedVacationTitle);
                            vacationRepository.updateVacation(vacation);
                            Toast.makeText(EditVacation.this, "Vacation updated", Toast.LENGTH_SHORT).show();
                            finish();

                    });

                    cancelBtn.setOnClickListener(v -> finish());
                }

            } );
        }


    }
}