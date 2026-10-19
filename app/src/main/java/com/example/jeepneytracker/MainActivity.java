package com.example.jeepneytracker;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jeepneytracker.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private DBHelper myDB;
    private ActivityMainBinding binding;
    ArrayList<String> data;
    ArrayList<Integer> logIDs;
    int SelectedLogID = -1;
    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //Initialize ViewBinding FIRST
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Apply window insets to binding.main to prevent UI overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //creating an instance for DBHelper
        myDB = new DBHelper(this);

        //set default date to current date
        selectedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(new Date());
        binding.btnDatePicker.setText("Date: " + selectedDate);

        //show existing records on startup
        ShowRecords();

        //adding a new record
        binding.btAdd.setOnClickListener(view -> {
            String plate = binding.txPlate.getText().toString();
            String pax = binding.txPax.getText().toString();

            if (plate.isEmpty() || pax.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            //record timestamp automatically on save
            String timeLogged = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.US).format(new Date());

            boolean checkInsertData = myDB.InsertData(plate, pax, timeLogged);
            if (checkInsertData == true) {
                Toast.makeText(this, "Add Records Successful", Toast.LENGTH_SHORT).show();
                binding.txPlate.setText("");
                binding.txPax.setText("");
                ShowRecords();
            }
        });

        //selecting row in ListView
        binding.lvList.setOnItemClickListener((parent, view, position, id) -> {
            SelectedLogID = logIDs.get(position);
            //retrieve the selected jeepney's information from the database using SelectedLogID
            Cursor record = myDB.getData();
            while (record.moveToNext()) {
                int logID = record.getInt(0);
                if (logID == SelectedLogID) {
                    binding.txPlate.setText(record.getString(1));
                    binding.txPax.setText(record.getString(2));
                    break;
                }
            }
        });

        UpdateRecords();
        DeleteRecords();

        // Date picker button click listener
        binding.btnDatePicker.setOnClickListener(v -> showDatePicker());
    }

    public void ShowRecords() {
        Cursor record = myDB.getData();
        data = new ArrayList<>();
        logIDs = new ArrayList<>();
        while (record.moveToNext()) {
            String timeLogged = record.getString(3);
            // check if the record's date matches the selected date
            if (timeLogged != null && timeLogged.startsWith(selectedDate)) {
                logIDs.add(record.getInt(0));
                data.add("Log ID: " + record.getString(0) + "\n" +
                        "Plate No: " + record.getString(1) + "\n" +
                        "Passengers: " + record.getString(2) + "\n" +
                        "Time: " + record.getString(3) + "\n");
            }
        }
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        binding.lvList.setAdapter(adp);
    }

    public void UpdateRecords() {
        binding.btUpdate.setOnClickListener(view -> {
            boolean checkUpdate = myDB.updateData(SelectedLogID, binding.txPlate.getText().toString(), binding.txPax.getText().toString());
            if (checkUpdate == true) {
                Toast.makeText(this, "Update Records Successful", Toast.LENGTH_SHORT).show();
                binding.txPlate.setText("");
                binding.txPax.setText("");
                ShowRecords();
            }
        });
    }

    public void DeleteRecords() {
        binding.btDelete.setOnClickListener(view -> {
            boolean checkDelete = myDB.deleteData(SelectedLogID);
            if (checkDelete == true) {
                Toast.makeText(this, "Delete Records Successful", Toast.LENGTH_SHORT).show();
                binding.txPlate.setText("");
                binding.txPax.setText("");
                ShowRecords();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar date = Calendar.getInstance();
                    date.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    selectedDate = sdf.format(date.getTime());

                    binding.btnDatePicker.setText("Date: " + selectedDate);

                    // refresh ListView with records for the selected date
                    ShowRecords();
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }
}