package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditVacation extends AppCompatActivity {

    VacationRepository vacationRepository;
    Date updatedStartDate;
    Date updatedEndDate;
    List<Excursion> excursions = new ArrayList<>();
    List<Date> excursionDates = new ArrayList<>();
    boolean excursionsReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vacation);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int vacationId = intent.getIntExtra("vacation_id", -1);

        TextInputEditText vacationTitleEdit = findViewById(R.id.vacationTitleEdit);
        TextInputEditText lodgingEdit = findViewById(R.id.lodgingEdit);
        Button buttonStartDate = findViewById(R.id.buttonStartDate);
        Button buttonEndDate = findViewById(R.id.buttonEndDate);

        buttonStartDate.setOnClickListener(v -> showDatePickerDialog(buttonStartDate, buttonEndDate));
        buttonEndDate.setOnClickListener(v -> showDatePickerDialog(buttonEndDate, buttonStartDate));

        Button saveBtn = findViewById(R.id.saveBtn);


        if (vacationId != -1) {
            vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    //populate the fields
                    vacationTitleEdit.setText(vacation.getTitle());
                    lodgingEdit.setText(vacation.getLodgingInfo());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    String startDate = dateFormat.format(vacation.getStartDate());
                    String endDate = dateFormat.format(vacation.getEndDate());
                    buttonStartDate.setText(startDate);
                    buttonEndDate.setText(endDate);
                    buttonStartDate.setTag(vacation.getStartDate());
                    buttonEndDate.setTag(vacation.getEndDate());

                    //prepare list of excursion dates for date validation
                    vacationRepository.getExcursionsByVacation(vacationId).observe(this, excursions -> {
                        this.excursions = excursions;
                        for (Excursion excursion : excursions) {
                            excursionDates.add(excursion.getExcursionDate());
                        }
                        excursionsReady = true;
                    });

                    saveBtn.setOnClickListener(v -> {
                        String updatedVacationTitle = vacationTitleEdit.getText().toString();
                        String updatedLodgingInfo = lodgingEdit.getText().toString();
                        updatedStartDate = (Date) buttonStartDate.getTag();
                        updatedEndDate = (Date) buttonEndDate.getTag();

                        if (updatedVacationTitle.isEmpty() || updatedLodgingInfo.isEmpty()
                                || updatedStartDate == null || updatedEndDate == null) {
                            Toast.makeText(EditVacation.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (excursionsReady) {
                            for (Date excursionDate : excursionDates) {
                                if (excursionDate.before(updatedStartDate) || excursionDate.after(updatedEndDate)) {
                                    Toast.makeText(EditVacation.this,
                                            "The updated dates conflict with your scheduled excursions.", Toast.LENGTH_LONG).show();
                                return;
                                }
                            }
                        }

                        vacation.setTitle(updatedVacationTitle);
                        vacation.setLodgingInfo(updatedLodgingInfo);
                        vacation.setStartDate(updatedStartDate);
                        vacation.setEndDate(updatedEndDate);

                        vacationRepository.updateVacation(vacation);
                        Toast.makeText(EditVacation.this, "Vacation updated", Toast.LENGTH_SHORT).show();
                        finish();

                    });

                }
            });
        }

        if (vacationId == -1) {
            Toast.makeText(EditVacation.this, "Error loading vacation", Toast.LENGTH_SHORT).show();
        }
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

    private void showDatePickerDialog(Button button, Button otherButton) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditVacation.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    button.setText(DateFormat.getDateInstance().format(selectedDate));
                    button.setTag(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        if (otherButton.getTag() != null) {
            Date otherDate = (Date) otherButton.getTag();
            if (button.getId() == R.id.buttonStartDate) {
                datePickerDialog.getDatePicker().setMaxDate(otherDate.getTime());
            } else if (button.getId() == R.id.buttonEndDate) {
                datePickerDialog.getDatePicker().setMinDate(otherDate.getTime());
            }
        }
        datePickerDialog.show();
    }
}