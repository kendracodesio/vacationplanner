package com.example.vacationplanner.database;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vacationplanner.dao.ExcursionDao;
import com.example.vacationplanner.dao.VacationDao;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VacationRepository {
    private final VacationDao mVacationDao;
    private final ExcursionDao mExcursionDao;
    private List<Vacation> mAllVacations;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public VacationRepository(Application application) {
        VacationPlannerDatabase db = VacationPlannerDatabase.getDatabase(application);
        mVacationDao = db.vacationDao();
        mExcursionDao = db.excursionDao();

    }

    public void insertVacation(Vacation vacation) {
        CountDownLatch latch = new CountDownLatch(1);
        databaseExecutor.execute(() -> {
            mVacationDao.insertVacation(vacation);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void updateVacation(Vacation vacation) {
        CountDownLatch latch = new CountDownLatch(1);
        databaseExecutor.execute(() -> {
            mVacationDao.updateVacation(vacation);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<Vacation> getVacationById(int vacationId) {
        return mVacationDao.getVacationById(vacationId);
    }


    public LiveData<List<Vacation>> getAllVacations() {
        return mVacationDao.getAllVacations();
    }

    public LiveData<List<Excursion>> getExcursionsByVacation(int vacationId){
        return mVacationDao.getExcursionsByVacation(vacationId);}

    public interface DeleteCallback {
        void onDelete(boolean isDeleted);
    }

    public void deleteVacation(Vacation vacation, DeleteCallback callback) {
        databaseExecutor.execute(() -> {
            boolean isDeleted;
            if (mVacationDao.getExcursionCountForVacation(vacation.getId()) > 0) {
                isDeleted = false;
            } else {
                mVacationDao.deleteVacation(vacation);
                isDeleted = true;
            }
            callback.onDelete(isDeleted);
        });
    }
    public void insertExcursion(Excursion excursion) {
        CountDownLatch latch = new CountDownLatch(1);
        databaseExecutor.execute(() -> {
            mExcursionDao.insertExcursion(excursion);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateExcursion(Excursion excursion) {
        CountDownLatch latch = new CountDownLatch(1);
        databaseExecutor.execute(() -> {
            mExcursionDao.updateExcursion(excursion);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteExcursion(Excursion excursion) {
        CountDownLatch latch = new CountDownLatch(1);
        databaseExecutor.execute(() -> {
            mExcursionDao.deleteExcursion(excursion);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<Excursion> getExcursionById(int excursionId) {
        return mExcursionDao.getExcursionById(excursionId);
    }
}
