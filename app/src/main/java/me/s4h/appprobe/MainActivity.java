package me.s4h.appprobe;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.processChooser)
    Spinner mProcessChooser;
    @Bind(R.id.tv)
    TextView tv;

    @Bind(R.id.intervalChooser)
    Spinner mIntervalChooser;

    ProcessChooserAdapter pcAdapter;
    ActivityManager mActivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pcAdapter = new ProcessChooserAdapter(this);
        mProcessChooser.setAdapter(pcAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivityManager =
                (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        this.pcAdapter.clear();
        this.pcAdapter.addAll(mActivityManager.getRunningAppProcesses());

    }

    @OnClick(R.id.myBtn)
    public void onBtnClicked() {
        Toast.makeText(this, "helloworld", Toast.LENGTH_SHORT).show();
        RunningAppProcessInfo info = (RunningAppProcessInfo) mProcessChooser.getSelectedItem();
        new MyTask().execute(info.processName);
    }


    private class MyTask extends AsyncTask<String, String, Void> {
        java.lang.Process p = null;
        BufferedReader br = null;

        @Override
        protected void onProgressUpdate(String... values) {
            MainActivity.this.tv.setText(values[0]);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                p = Runtime.getRuntime().exec("top");
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if(line.contains(params[0])){
                        Log.i("fdsff", "f43f------->" + line);
                        publishProgress(line);
                    }
                }
            } catch (IOException e) {
                Log.e("executeTop", "error in getting first line of top");
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    p.destroy();
                } catch (IOException e) {
                    Log.e("executeTop",
                            "error in closing and destroying top process");
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class ProcessChooserAdapter extends ArrayAdapter<RunningAppProcessInfo> {
        private LayoutInflater inflater;

        public ProcessChooserAdapter(Context context) {
            super(context, 0);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView text;
            if (convertView == null) {
                view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            } else {
                view = convertView;
            }
            text = (TextView) view.findViewById(android.R.id.text1);
            RunningAppProcessInfo item = getItem(position);
            text.setText(item.pid + ":" + item.processName);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return this.getView(position, convertView, parent);
        }
    }


}
