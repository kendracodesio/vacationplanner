package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;
import com.example.vacationplanner.entities.Vacation;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailedVacation extends AppCompatActivity {

    VacationRepository vacationRepository;

    private int vacationId;
    private Vacation vacation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_vacation);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int vacationId = intent.getIntExtra("vacation_id", -1);

        TextView vacationTitle = findViewById(R.id.vacationTitle);
        TextView vacationDetails = findViewById(R.id.vacationDetails);

        if (vacationId != -1) {

            vacationRepository.getVacationById(vacationId).observe(this, vacation -> {
                if (vacation != null) {
                    this.vacation = vacation;
                    this.vacationId = vacation.getId();
                    vacationTitle.setText(vacation.getTitle());
                    vacationDetails.setText(formatVacationDetails(vacation));

                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vacation_detail_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_delete) {
            vacationRepository.deleteVacation(vacation, isDeleted -> {
                if (isDeleted) {
                    finish();
                } else {
                    Toast.makeText(DetailedVacation.this, "Cannot delete vacation with existing excursions", Toast.LENGTH_LONG).show();
                }
            });
            return true;
        } else if (item.getItemId() == R.id.action_edit) {
            Intent editIntent = new Intent(DetailedVacation.this, EditVacation.class );
            editIntent.putExtra("vacation_id", vacationId);
            startActivity(editIntent);
            return true;

        } else {
            return super.onOptionsItemSelected(item);

        }
    }


    private String formatVacationDetails(@NotNull Vacation vacation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String startDate = dateFormat.format(vacation.getStartDate());
        String endDate = dateFormat.format(vacation.getEndDate());
        return "Dates: " + startDate + " - " + endDate + "\n Lodging: " + vacation.getLodgingInfo();
    }
}
