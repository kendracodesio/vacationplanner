package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddVacation extends AppCompatActivity {

    private VacationRepository vacationRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vacation);

        vacationRepository = new VacationRepository(this.getApplication());

        TextInputEditText titleInput = findViewById(R.id.vacationTitleInput);
        TextInputEditText lodgingInput = findViewById(R.id.lodgingInput);
        Button buttonStartDate = findViewById(R.id.buttonStartDate);
        Button buttonEndDate = findViewById(R.id.buttonEndDate);

        buttonStartDate.setOnClickListener(v -> showDatePickerDialog(buttonStartDate, buttonEndDate));
        buttonEndDate.setOnClickListener(v -> showDatePickerDialog(buttonEndDate, buttonStartDate));

        Button createVacationButton = findViewById(R.id.createVacationBtn);


        createVacationButton.setOnClickListener(v ->
        {
            String vacationTitle = titleInput.getText().toString();
            String lodgingInfo = lodgingInput.getText().toString();

            Date startDate = (Date) buttonStartDate.getTag();
            Date endDate = (Date) buttonEndDate.getTag();

            if(vacationTitle.isEmpty() || lodgingInfo.isEmpty() || startDate == null || endDate == null) {
                Toast.makeText(AddVacation.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            Vacation vacation = new Vacation();
            vacation.setTitle(vacationTitle);
            vacation.setLodgingInfo(lodgingInfo);
            vacation.setStartDate(startDate);
            vacation.setEndDate(endDate);

            vacationRepository.insertVacation(vacation);

            Toast.makeText(AddVacation.this, "Vacation created", Toast.LENGTH_SHORT).show();
            finish();

        });


    }

    private void showDatePickerDialog(Button button, Button otherButton) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddVacation.this,
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