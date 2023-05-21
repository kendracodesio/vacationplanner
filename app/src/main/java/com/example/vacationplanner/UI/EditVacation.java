package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditVacation extends AppCompatActivity {

    VacationRepository vacationRepository;

    private int vacationId;
    private Vacation vacation;


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
                    vacationTitleEdit.setText(vacation.getTitle());
                    lodgingEdit.setText(vacation.getLodgingInfo());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    String startDate = dateFormat.format(vacation.getStartDate());
                    String endDate = dateFormat.format(vacation.getEndDate());
                    buttonStartDate.setText(startDate);
                    buttonEndDate.setText(endDate);
                    buttonStartDate.setTag(vacation.getStartDate());
                    buttonEndDate.setTag(vacation.getEndDate());


                    saveBtn.setOnClickListener(v -> {
                        String updatedVacationTitle = vacationTitleEdit.getText().toString();
                        String updatedLodgingInfo = lodgingEdit.getText().toString();
                        Date updatedStartDate = (Date) buttonStartDate.getTag();
                        Date updatedEndDate = (Date) buttonEndDate.getTag();

                        if (updatedVacationTitle.isEmpty() || updatedLodgingInfo.isEmpty() || updatedStartDate == null || updatedEndDate == null) {
                            Toast.makeText(EditVacation.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
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
        inflater.inflate(R.menu.edit_vacation_menu, menu);
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