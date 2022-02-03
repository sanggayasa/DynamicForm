package io.kecoakburikk.dynamicform;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Muhammad Irfan
 * @since 06/01/2022 09.35
 */
public class MainActivity extends AppCompatActivity implements DynamicFormCallback {
    private LinearLayout linearLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        this.linearLayout = this.findViewById(R.id.linearLayout);
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Please Wait");
        this.progressDialog.setCancelable(false);

        GetFormTask getFormTask = new GetFormTask();

        getFormTask.execute();
    }

    @Override
    public void success(List<DynamicForms.FormData> formDatas) {
        for (DynamicForms.FormData formData : formDatas) {
            System.err.println("Question: " + formData.getQuestion() + ". Answer: " + formData.getAnswer());
        }
    }

    private class GetFormTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            MainActivity.this.progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                return ApiParser.getForm();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                DynamicForms.build(MainActivity.this, MainActivity.this.linearLayout, jsonObject, MainActivity.this);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            MainActivity.this.progressDialog.dismiss();
        }
    }
}
