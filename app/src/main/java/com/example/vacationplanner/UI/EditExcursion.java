package com.example.vacationplanner.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.VacationRepository;

public class EditExcursion extends AppCompatActivity {

    VacationRepository vacationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_excursion);

        vacationRepository = new VacationRepository(getApplication());

        Intent intent = getIntent();
        int excursionId = intent.getIntExtra("excursion_id", -1);
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
}