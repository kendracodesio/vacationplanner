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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditExcursion extends AppCompatActivity {

    VacationRepository vacationRepository;
    Excursion excursion;
    Vacation vacation;
    Date vacationStartDate, vacationEndDate, updatedExcursionDate;
    int vacationId;
    String excursionTitle, updatedExcursionTitle;
    boolean dataReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_excursion);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int excursionId = intent.getIntExtra("excursion_id", -1);


        TextView vacationTitleText = findViewById(R.id.vacationTitle5);
        TextInputEditText excursionTitleInput = findViewById(R.id.excursionTitleInput2);
        Button excursionDateBtn = findViewById(R.id.excursionDateBtn2);
        Button saveButton = findViewById(R.id.saveExcursionBtn);


        if (excursionId != -1) {
            vacationRepository.getExcursionById(excursionId).observe(this, excursion -> {
                if (excursion != null) {
                    //fetching data and populating fields
                    this.excursion = excursion;
                    excursionTitleInput.setText(excursion.getExcursionTitle());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    String excursionDate = dateFormat.format(excursion.getExcursionDate());
                    excursionDateBtn.setText(excursionDate);
                    excursionDateBtn.setTag(excursion.getExcursionDate());
                    this.vacationId = excursion.getVacationId();

                    vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                        this.vacation = vacation;
                        this.vacationStartDate = vacation.getStartDate();
                        this.vacationEndDate = vacation.getEndDate();
                        vacationTitleText.setText(vacation.getTitle());
                        setExcursionDateBtnClickListener(excursionDateBtn);

                        dataReady = true;
                    });

                    saveButton.setOnClickListener(v -> {
                        //setting user input values
                        if (dataReady) {
                            updatedExcursionTitle = excursionTitleInput.getText().toString();
                            updatedExcursionDate = (Date) excursionDateBtn.getTag();
                            Log.d("EditExcursion", updatedExcursionTitle);
                            Log.d("EditExcursion", updatedExcursionDate.toString());
                            if (updatedExcursionTitle.isEmpty() || updatedExcursionDate == null) {
                                Toast.makeText(EditExcursion.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            } else {
                                excursion.setExcursionTitle(updatedExcursionTitle);
                                excursion.setExcursionDate(updatedExcursionDate);
                                vacationRepository.updateExcursion(excursion);
                                Toast.makeText(EditExcursion.this, "Excursion updated", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cancel_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cancel) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);

        }
    }

    private void setExcursionDateBtnClickListener(Button button) {
        if (vacationStartDate != null && vacationEndDate != null) {
            button.setOnClickListener(v -> {
                showDatePickerDialog(button, vacationStartDate, vacationEndDate);
            });
        }
    }

    private void showDatePickerDialog(Button button, Date minDate, Date maxDate) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditExcursion.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    button.setText(DateFormat.getDateInstance().format(selectedDate));
                    button.setTag(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());

        datePickerDialog.show();
    }
}