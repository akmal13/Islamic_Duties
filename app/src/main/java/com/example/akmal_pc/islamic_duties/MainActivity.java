package com.example.akmal_pc.islamic_duties;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;


public class MainActivity extends AppCompatActivity {

    private static final ActivityInfo ActivityInfo = new ActivityInfo();
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private DatePickerDialog.OnDateSetListener mdatelistener;
    private TextView tampildeadlinepeng;
    private TextView zakatpengtahded;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView jamsubuh = findViewById(R.id.Jamsubuh);
        TextView jamzuhur = findViewById(R.id.JamZuhur);
        TextView jamashar = findViewById(R.id.JamAshar);
        TextView jammaghrib = findViewById(R.id.JamMaghrib);
        TextView jamIsya = findViewById(R.id.JamIsya);
        final EditText inputzakat = findViewById(R.id.inputgaji);
        final TextView hasilzakat = findViewById(R.id.hasilZakat);
        final EditText inputfitrah = findViewById(R.id.inputbankeluarga);
        final TextView hasilliter = findViewById(R.id.outfitrahliter);
        final TextView hasilkilo = findViewById(R.id.outfitrahkilo);
        final TextView hasilzakpengtah = findViewById(R.id.hasilZakattah);
        SplashScreen Update_jam = new SplashScreen();
        jamsubuh.setText(Update_jam.JamSubuh());
        jamzuhur.setText(Update_jam.JamZuhur());
        jamashar.setText(Update_jam.JamAshar());
        jammaghrib.setText(Update_jam.JamMaghrib());
        jamIsya.setText(Update_jam.JamIsya());


        if(isServicesOK()){
            init();
        }

        inputzakat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String hasil = inputzakat.getText().toString();

                try{
                    if(hasil!=""){
                        DecimalFormat df = new DecimalFormat();
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setGroupingSeparator('.');
                        df.setDecimalFormatSymbols(dfs);
                        double hasilza = Float.parseFloat(hasil)*0.025 ;
                        String tengah = String.valueOf(hasilza);
                        int bhasil = Math.round(Float.parseFloat(tengah));
                        String hasilnya = df.format(bhasil);

                        int tahhasil = bhasil*12;
                        String hasiltah = df.format(tahhasil);

                        hasilzakpengtah.setText("Rp. "+hasiltah);
                        hasilzakat.setText("Rp. " + hasilnya);
                    }
                }catch(NumberFormatException e){Log.e("Main ac","exception");
                hasilzakat.setText("Rp. 0"); hasilzakpengtah.setText("Rp. 0");}

            }
        });

        inputfitrah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String zakfit = inputfitrah.getText().toString();

                try{
                    float mak = Float.parseFloat(zakfit);
                    int cekgas = Math.round(mak);
                    DecimalFormat dff = new DecimalFormat("###.#");
                    int akhirf = Integer.parseInt(dff.format(cekgas));
                    String nilailiter = dff.format(akhirf*3.5);
                    String akhirkilo = dff.format(akhirf*2.7);


                    hasilliter.setText(nilailiter + " Liter");
                    hasilkilo.setText(akhirkilo+ " Kilogram");

                }catch (NumberFormatException e){Log.e("Main ac", "Exception fitrah");
                hasilliter.setText("0 Liter"); hasilkilo.setText("0 Kilogram");}

            }
        });

        setDeadline();

    }

    private void init(){
        TextView btnMap = findViewById(R.id.MapBtn);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });


    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){

            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void setDeadline(){
        Button setTanggalPenghasilan = findViewById(R.id.inputjadwalgaji);
        tampildeadlinepeng = findViewById(R.id.deadlinezakatpeng);
        zakatpengtahded = findViewById(R.id.deadlinezakatpengtahnilai);


        setTanggalPenghasilan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DATE);

                DatePickerDialog datepick = new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,mdatelistener,year,month,day);
                 datepick.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datepick.show();
                }
            });

            mdatelistener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int i3, int i2, int i1) {
                    String bulan = String.valueOf(i2+2);

                    String akded = String.valueOf(i1) + "/" + bulan+"/"+String.valueOf(i3);
                    String akdeeed = String.valueOf(i1) + "/" + String.valueOf(i2+1)+"/"+String.valueOf(i3+1);

                    if ((i2+2) == 13){
                        akded = String.valueOf(i1) + "/" + "1"+"/"+String.valueOf(i3+1);
                    }

                    tampildeadlinepeng.setText(akded);

                    zakatpengtahded.setText(akdeeed);

                }
            };
    }


}