package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

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
        Button selectExcursionDate = findViewById(R.id.excursionDateBtn);
        Button createExcursionBtn = findViewById(R.id.createExcursionBtn);

        selectExcursionDate.setOnClickListener(v -> {showDatePickerDialog(selectExcursionDate);});


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
            Date excursionDate = (Date) selectExcursionDate.getTag();

            if (excursionTitle.isEmpty() || excursionDate == null) {
                Toast.makeText(AddExcursion.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Excursion excursion = new Excursion();
            excursion.setVacationId(vacationId);
            excursion.setExcursionTitle(excursionTitle);
            excursion.setExcursionDate(excursionDate);
            Log.d("AddExcursion", excursion.getExcursionTitle());
            vacationRepository.insertExcursion(excursion);

            Toast.makeText(AddExcursion.this, "Excursion created", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cancel_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_cancel) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);

        }
    }
    private void showDatePickerDialog(Button button) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddExcursion.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    button.setText(DateFormat.getDateInstance().format(selectedDate));
                    button.setTag(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}