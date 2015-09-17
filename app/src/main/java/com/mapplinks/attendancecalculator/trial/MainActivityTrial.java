package com.mapplinks.attendancecalculator.trial;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.mapplinks.attendancecalculator.R;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.util.Random;

public class MainActivityTrial extends ActionBarActivity {

    TextView result, message;
    EditText attendedClasses, conductedClasses, requiredPercent;
    Button B1, reset;

    Double a, b, c;
    String s1, s2, s3, d;
    Integer l1, l2, l3, e, f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_activity_trial);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

       // Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bngtibG4aiXDQUcoaXrw74ZSnwmOGkEdCHbK75tz", "bl7wyZRbK0pVCdY1fI59pPN0XXkGkxlIx20e3iSI");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        final String[] incomplete = getResources().getStringArray(R.array.incomplete);
        final String[] exact = getResources().getStringArray(R.array.exact);
        final String[] drastic = getResources().getStringArray(R.array.drastic);
        final String[] low = getResources().getStringArray(R.array.low);
        final String[] more = getResources().getStringArray(R.array.more);
        final String[] smart = getResources().getStringArray(R.array.smart);
        final String[] dumb = getResources().getStringArray(R.array.dumb);
        final Random rgenerator = new Random();

        attendedClasses = (EditText) findViewById(R.id.editText1);
        conductedClasses = (EditText) findViewById(R.id.editText2);
        requiredPercent = (EditText) findViewById(R.id.editText3);

        String projectToken = "39af48ffc57ba2f6a28157eaec03eecb";
        final MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);

        result = (TextView) findViewById(R.id.textView4);
        message = (TextView) findViewById(R.id.textView5);

        reset = (Button) findViewById(R.id.button2);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendedClasses.setText("");
                conductedClasses.setText("");
                requiredPercent.setText("");
                attendedClasses.requestFocus();
            }
        });


        B1 = (Button) findViewById(R.id.button1);
        B1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mixpanel.track("Calculate");

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                s1 = attendedClasses.getText().toString();
                s2 = conductedClasses.getText().toString();
                s3 = requiredPercent.getText().toString();
                l1 = s1.length();
                l2 = s2.length();
                l3 = s3.length();

                if ((l1 == 0) || (l2 == 0) || (l3 == 0)) {
                    result.setText("");
                    message.setText(incomplete[rgenerator.nextInt(incomplete.length)]);
                } else {
                    a = Double.valueOf(attendedClasses.getText().toString());
                    b = Double.valueOf(conductedClasses.getText().toString());
                    c = Double.valueOf(requiredPercent.getText().toString());
                    if (b == 0) {
                        Toast.makeText(MainActivityTrial.this, "Classes conducted can't be zero!\nEnter valid value!", Toast.LENGTH_SHORT).show();
                        result.setText("");
                        message.setText(incomplete[rgenerator.nextInt(incomplete.length)]);
                    } else if (c == 0) {
                        Toast.makeText(MainActivityTrial.this, "Desired Percentage can't be zero!\nEnter valid value!", Toast.LENGTH_SHORT).show();
                        result.setText("");
                        message.setText(incomplete[rgenerator.nextInt(incomplete.length)]);
                    } else {
                        calculate();
                    }
                }
            }

            public void calculate() {
                d = String.format("%.2f", (a / b * 100));

                if (Float.parseFloat(s3) < 50) {
                    result.setText("I asked for your attendance percentage, not your marks. Enter Properly.");
                    message.setText("");
                } else if (c > 100){
                    result.setText(dumb[rgenerator.nextInt(dumb.length)]);
                    message.setText("");
                } else {

                    if ((Double.parseDouble(d)) > 100)            //SmartyPants
                    {
                        result.setText(smart[rgenerator.nextInt(smart.length)]);
                        message.setText("");
                    } else {
                        if (c < (Double.parseDouble(d)))            //Nerd
                        {
                            e = (int) Math.ceil(((100 * a - c * b) / c));
                            result.setText("Your attendance percentage is " + d + ".\nYou may bunk " + Integer.toString(e) + " Classes!");
                            message.setText(more[rgenerator.nextInt(more.length)]);
                        } else if (c > (Double.parseDouble(d))) {
                            f = (int) Math.ceil((((c * b) - (100 * a)) / (100 - c)));
                            result.setText("Your attendance percentage is " + d + "\nYou need to attend " + Integer.toString(f) + " Classes!");
                            if (f < 8) {
                                message.setText(low[rgenerator.nextInt(low.length)]);
                            } else {
                                message.setText(drastic[rgenerator.nextInt(drastic.length)]);
                            }
                        } else {
                            result.setText("Your attendance percentage is " + d);
                            message.setText(exact[rgenerator.nextInt(exact.length)]);
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_trial, menu);
        return true;
    }

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {
            shareTextUrl();
        } else if (id == R.id.feedback) {
            Intent i = new Intent(MainActivityTrial.this, FeedbackActivity.class);
            MainActivityTrial.this.startActivity(i);
        } else if (id == R.id.rate) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.mapplinks.attendancecalculator"));
            if (!MyStartActivity(intent)) {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?[Id]"));
                if (!MyStartActivity(intent)) {
                    Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, "Attendance Calculator");
        share.putExtra(Intent.EXTRA_TEXT, "I am using Attendance Calculator to check my bunking status via @Mapplinks. Find it here: https://goo.gl/OQt4aJ");

        startActivity(Intent.createChooser(share, "Spread the Word!"));
    }
}
