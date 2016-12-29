package com.zakaria.ingeniusclientrest;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zakaria.ingeniusclientrest.pojo.Projet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kevin on 12/3/14.
 */
public class CalendarFragment extends android.app.Fragment {
    CalendarView calendar;
    ScrollView sv;
    Spinner mySpinner;
    RadioGroup radioGroup;
    View fragmentRootView;
    EditText mEdit;
    Button   mButton;
    String date;
    String radio;
    String message;
    String id_projet;
    int id;
    RequestParams params = new RequestParams();
    private ArrayList<Projet> listeProjet = new ArrayList<Projet>();
    public CalendarFragment() {
        // Required empty public constructor
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        fragmentRootView = inflater.inflate(R.layout.main_calendar, container, false);
        mySpinner  = (Spinner)fragmentRootView.findViewById(R.id.myspinner);
        radioGroup = (RadioGroup) fragmentRootView.findViewById(R.id.radioGroup);
        mEdit   = (EditText)fragmentRootView.findViewById(R.id.editText);
        calendar = (CalendarView) fragmentRootView.findViewById(R.id.calendarView);
        mButton = (Button)fragmentRootView.findViewById(R.id.btnt);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                String a =Integer.toString(dayOfMonth);
                String b =Integer.toString(month);
                String c =Integer.toString(year);
                date=a+b+c;

                params.put("date", date);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                radio = rb.getText().toString();
                params.put("quantite", radio);
            }
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Projet serv = (Projet) parent.getSelectedItem();
                id_projet = Integer.toString(serv.getIdProjet());
                params.put("id_projet", id_projet);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences prefs = this.getActivity().getSharedPreferences("acti", Context.MODE_PRIVATE);
        id = prefs.getInt("id",2);
        String o =Integer.toString(id);
        params.put("id_util", o);
        ws_Projet(id);


        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        message= mEdit.getText().toString();
                        params.put("comment", message);
                        Toast.makeText(getActivity(), params.toString(), Toast.LENGTH_LONG).show();

                        //date=="+date+"&comment="+message+"&quantite="+radio+"&id_util="+id+"&id_projet="+id_projet
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.get(getResources().getString(R.string.addressIP_Port)+"/resfulingeniusserver/act/commenter",params
                                ,new AsyncHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(String response) {

                                        try {
                                            JSONObject obj = new JSONObject(response);

                                            if(obj.getBoolean("status")){

                                                Toast.makeText(getActivity(), obj.getString("liste"), Toast.LENGTH_LONG).show();

                                            }

                                            else{
                                                Toast.makeText(getActivity(), obj.getString("liste"), Toast.LENGTH_LONG).show();

                                            }
                                        } catch (JSONException e) {
                                            System.out.print("11111");


                                            Toast.makeText(getActivity(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();

                                        }
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Throwable error,String content) {



                                        if(statusCode == 404){
                                            Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                                        }
                                        // When Http response code is '500'
                                        else if(statusCode == 500){
                                            Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                        }
                                        // When Http response code other than 404, 500
                                        else{
                                            Toast.makeText(getActivity(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });




                    }
                });
        return fragmentRootView;
    }


    void ws_Projet(int id)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getResources().getString(R.string.addressIP_Port)+"/resfulingeniusserver/projet/listprojets?idutilisateur="+id
                ,new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if(obj.getBoolean("status")){
                        String str = obj.getString("liste");
                        String[] listeligne = str.split(";");

                        for(int i = 0 ; i < listeligne.length; i++ ) {

                            String ligne = listeligne[i];

                            String[] listeChamps = ligne.split(",");

                            int id = Integer.parseInt(listeChamps[0]);

                            String service = listeChamps[1].toString();

                            Projet s = new Projet(id,service);

                            listeProjet.add(s);
                        }
                    ArrayAdapter<Projet> adapter = new ArrayAdapter<Projet>(getActivity(), R.layout.spinner_item, listeProjet);
                        mySpinner.setAdapter(adapter);
                    }

                    else{
                        Toast.makeText(getActivity(), obj.getString("liste"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    System.out.print("11111");


                    Toast.makeText(getActivity(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
            @Override
            public void onFailure(int statusCode, Throwable error,String content) {



                if(statusCode == 404){
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getActivity(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}