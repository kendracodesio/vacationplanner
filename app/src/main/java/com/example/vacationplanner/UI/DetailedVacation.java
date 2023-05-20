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

import org.w3c.dom.Text;

public class DetailedVacation extends AppCompatActivity {

    VacationRepository vacationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_vacation);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int vacationId = intent.getIntExtra("vacation_id", -1);


        Button deleteBtn = findViewById(R.id.deleteBtn);
        Button editBtn = findViewById(R.id.editVacationBtn);
        TextView vacationTitle = findViewById(R.id.vacationTitle);

        if (vacationId != -1) {

            vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    vacationTitle.setText(vacation.getTitle());

                    deleteBtn.setOnClickListener(v -> {
                        vacationRepository.deleteVacation(vacation, isDeleted -> {
                            if (isDeleted) {
                                finish();
                            } else {
                                Toast.makeText(DetailedVacation.this, "Cannot delete vacation with existing excursions", Toast.LENGTH_LONG).show();
                            }
                        });
                    });

                    editBtn.setOnClickListener(v -> {
                        Intent editIntent = new Intent(DetailedVacation.this, EditVacation.class );
                        editIntent.putExtra("vacation_id", vacationId);
                        startActivity(editIntent);
                    });
                }
            });


        }
    }
}
