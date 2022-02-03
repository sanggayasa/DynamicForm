package io.kecoakburikk.dynamicform;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Muhammad Irfan
 * @since 06/01/2022 11.08
 */
public class DynamicForms {

    @SuppressLint("InflateParams")
    public static void build(AppCompatActivity appCompatActivity, LinearLayout linearLayout, JSONObject jsonObject, DynamicFormCallback dynamicFormCallback) throws JSONException {

        String jsonJudul = jsonObject.getString("title");
        String jsonDeskripsi = jsonObject.getString("description");
        LinearLayout textJudul = (LinearLayout) LayoutInflater.from(appCompatActivity).inflate(R.layout.judul, null);
        TextView textVjudul = textJudul.findViewById(R.id.Judul);
        TextView textVDeskripsi = textJudul.findViewById(R.id.Description);
        textVjudul.setText(jsonJudul);
        textVDeskripsi.setText(jsonDeskripsi);
        ImageView imageView;
        imageView = (ImageView) LayoutInflater.from(appCompatActivity).inflate(R.layout.banner, null);;
        String urlGambar =jsonObject.getString("bannerLink");

        if(!urlGambar.equals("null")) {
            linearLayout.addView(imageView);
            Glide.with(appCompatActivity)
                    // LOAD URL DARI INTERNET
                    .load(urlGambar)
                    .error(R.drawable.salaha)
                    .into(imageView);

        }

        linearLayout.addView(textJudul);

        JSONArray jsonSection = jsonObject.getJSONArray("section");
        for (int w = 0; w< jsonSection.length(); w++) {
            JSONObject sectionObj = jsonSection.getJSONObject(w);
            String judulSection = sectionObj.getString("judulSection");
            TextView judulSectionTemplate = new TextView(appCompatActivity);
            String deskripsiSection = sectionObj.getString("deskripsiSection");
            TextView deskripsiSectionTemplate = new TextView(appCompatActivity);
            judulSectionTemplate.setTextSize(20);
            judulSectionTemplate.setText(judulSection);
            deskripsiSectionTemplate.setText(deskripsiSection);
            LinearLayout templateSection = (LinearLayout) LayoutInflater.from(appCompatActivity).inflate(R.layout.section_template, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            Resources r = appCompatActivity.getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    30,
                    r.getDisplayMetrics()
            );
            params.setMargins(0, px, 0, 0);
            templateSection.setLayoutParams(params);
            templateSection.addView(judulSectionTemplate);
            templateSection.addView(deskripsiSectionTemplate);

            JSONArray jsonArray = sectionObj.getJSONArray("questions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                String type = subJsonObject.getString("type");

                if (type.equals("SHORT_ANSWER")) {
                    TextInputLayout textInputLayout = (TextInputLayout) LayoutInflater.from(appCompatActivity).inflate(R.layout.simple_answer, null);
                    String getType = subJsonObject.getJSONArray("validasi").getJSONObject(0).getString("type");
                    textInputLayout.setHint(subJsonObject.getString("title"));

                    TextInputEditText isi = textInputLayout.findViewById(R.id.text);
                    if (getType.equals("number")) {
                        isi.setInputType(2);
                        isi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    validasiAngka(subJsonObject, textInputLayout, isi);
                                } else {
                                    textInputLayout.setHelperText("");
                                }
                            }
                        });
                    } else {
                        isi.setInputType(1);
                        isi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    validasiHuruf(isi, subJsonObject, textInputLayout);
                                } else {
                                    textInputLayout.setHelperText("");
                                }
                            }
                        });
                    }
                    textInputLayout.setTag(subJsonObject);
                    templateSection.addView(textInputLayout);

                }
                if (type.equals("LONG_ANSWER")) {
                    TextInputLayout textAreaInputLayout = (TextInputLayout) LayoutInflater.from(appCompatActivity).inflate(R.layout.simple_answer, null);
                    TextInputEditText isi = textAreaInputLayout.findViewById(R.id.text);
                    isi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                validasiHuruf(isi, subJsonObject, textAreaInputLayout);
                            } else {
                                textAreaInputLayout.setHelperText("");
                            }
                        }
                    });
                    textAreaInputLayout.setHint(subJsonObject.getString("title"));
                    textAreaInputLayout.setTag(subJsonObject);

                    templateSection.addView(textAreaInputLayout);
                }
                if (type.equals("CHECKBOX")) {
                    CheckBox textAreaLayout = (CheckBox) LayoutInflater.from(appCompatActivity).inflate(R.layout.check_box, null);

                    String setTextCheckBox = subJsonObject.getString("title");
                    textAreaLayout.setTag(subJsonObject);
                    textAreaLayout.setText(setTextCheckBox);
                    templateSection.addView(textAreaLayout);
                }
                if (type.equals("CHECKBOXGROUPRECYCLER")) {
                    RecyclerView templaterecyclerView = (RecyclerView) LayoutInflater.from(appCompatActivity).inflate(R.layout.template_checkbox, null);

                    MoviesRecyclerViewAdapter adapter;
                    ArrayList<CheckboxRecycler> objMovies = new ArrayList<>();

                    JSONArray getOption = subJsonObject.getJSONArray("optionAnswer");

                    for (int p = 0; p < getOption.length(); p++) {
                        objMovies.add(new CheckboxRecycler(getOption.getString(p)));
                    }


                    adapter = new MoviesRecyclerViewAdapter(objMovies);
                    TextView txtJudul = new TextView(appCompatActivity);
                    txtJudul.setText(subJsonObject.getString("title"));
                    templaterecyclerView.getParent();
                    templaterecyclerView.setTag(subJsonObject);
                    templaterecyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity));
                    templaterecyclerView.setAdapter(adapter);
                    templateSection.addView(txtJudul);
                    templateSection.addView(templaterecyclerView);
                }
                if (type.equals("RADIOGROUP")) {
                    RadioGroup radioGroupLayout = (RadioGroup) LayoutInflater.from(appCompatActivity).inflate(R.layout.radio_group_template, null);

                    String title = subJsonObject.getString("title");
                    JSONArray option = subJsonObject.getJSONArray("option");
                    TextView txtJudul = new TextView(appCompatActivity);
                    radioGroupLayout.setTag(subJsonObject);
                    txtJudul.setText(title);
                    radioGroupLayout.addView(txtJudul);
                    for (int c = 0; c < option.length(); c++) {
                        String radioBtnset = option.getString(c);
                        RadioButton radioBtn = new RadioButton(appCompatActivity);
                        radioBtn.setText(radioBtnset);
                        radioBtn.setId(c);
                        radioGroupLayout.addView(radioBtn);
                    }
                    templateSection.addView(radioGroupLayout);
                }
                if (type.equals("DATE")) {
                    TextInputLayout dateTemplate = (TextInputLayout) LayoutInflater.from(appCompatActivity).inflate(R.layout.date, null);
                    TextInputEditText dateTv = dateTemplate.findViewById(R.id.time);
                    String title = subJsonObject.getString("title");
                    String maxDate = subJsonObject.getString("maxDate");
                    String minDate = subJsonObject.getString("minDate");
                    dateTv.setHint("dd/mm/YYYY ");
                    dateTemplate.setTag(subJsonObject);
                    dateTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar newCalendar = Calendar.getInstance();

                            DatePickerDialog datePickerDialog = new DatePickerDialog(appCompatActivity, new DatePickerDialog.OnDateSetListener() {
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                    Calendar newDate = Calendar.getInstance();
                                    newDate.set(year, monthOfYear, dayOfMonth);
                                    dateTv.setText(dateFormatter.format(newDate.getTime()));
                                }

                            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                            if (!maxDate.equals("null")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = null;
                                try {
                                    date = sdf.parse(maxDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Long millis = date.getTime();
                                datePickerDialog.getDatePicker().setMaxDate(millis);
                            }

                            if (!minDate.equals("null")) {
                                SimpleDateFormat sdfmin = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = null;
                                try {
                                    date = sdfmin.parse(minDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Long millis = date.getTime();
                                datePickerDialog.getDatePicker().setMinDate(millis);
                            }
                            datePickerDialog.show();
                        }
                    });
                    TextView titleTv = new TextView(appCompatActivity);
                    titleTv.setText(title);
                    titleTv.setPadding(0, 50, 0, 0);
                    titleTv.setTextColor(Color.parseColor("#0f0f0f"));
                    templateSection.addView(titleTv);
                    templateSection.addView(dateTemplate);
                }
                if (type.equals("TIME")) {
                    TextInputLayout txtTimeTemplateLayout = (TextInputLayout) LayoutInflater.from(appCompatActivity).inflate(R.layout.time, null);
                    TextInputEditText txtTimeLayout = txtTimeTemplateLayout.findViewById(R.id.time);
                    txtTimeLayout.setHint("__:__");
                    txtTimeTemplateLayout.setTag(subJsonObject);
                    String maxTime = subJsonObject.getString("maxTime");
                    String pesanMaxTime = subJsonObject.getString("pesanMaxTime");
                    String minTime = subJsonObject.getString("minTime");
                    String pesanMinTime = subJsonObject.getString("pesanMinTime");

                    txtTimeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar cal = Calendar.getInstance();
                            TimePickerDialog timeset = new TimePickerDialog(appCompatActivity, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    cal.set(Calendar.MINUTE, minute);
                                    String inputTime = "null";
                                    boolean checkInput = false;

                                    if (!maxTime.equals("null")) {
                                        inputTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
                                        SimpleDateFormat convertToMilli = new SimpleDateFormat("HH:mm");
                                        long miliInput = 0, maxValid = 0;

                                        try {
                                            miliInput = convertToMilli.parse(inputTime).getTime();
                                            maxValid = convertToMilli.parse(maxTime).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (miliInput > maxValid) {
                                            if (!pesanMaxTime.equals("null")) {
                                                txtTimeTemplateLayout.setHelperText(pesanMaxTime);
                                            } else {
                                                String text = "harus dibawah " + maxTime.toString();
                                                txtTimeTemplateLayout.setHelperText(text);
                                            }
                                            checkInput = true;
                                        }

                                    }

                                    if (!minTime.equals("null")) {
                                        inputTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
                                        SimpleDateFormat convertToMilli = new SimpleDateFormat("HH:mm");
                                        long miliInput = 0, minValid = 0;

                                        try {
                                            miliInput = convertToMilli.parse(inputTime).getTime();
                                            minValid = convertToMilli.parse(minTime).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                        if (miliInput < minValid) {
                                            if (!pesanMinTime.equals("null")) {
                                                txtTimeTemplateLayout.setHelperText(pesanMinTime);
                                            } else {
                                                String text = "harus diatas " + minTime.toString();
                                                txtTimeTemplateLayout.setHelperText(text);
                                            }
                                            checkInput = true;
                                        }
                                    }

                                    if (!checkInput) {
                                        txtTimeTemplateLayout.setHelperText("");
                                    }

                                    txtTimeLayout.setText(new SimpleDateFormat("HH:mm").format(cal.getTime()));
                                }
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                            timeset.show();
                        }
                    });
                    TextView txtTitle = new TextView(appCompatActivity);
                    txtTitle.setText(subJsonObject.getString("title"));
                    txtTitle.setTextColor(Color.parseColor("#0f0f0f"));
                    txtTitle.setPadding(0, 50, 0, 0);
                    templateSection.addView(txtTitle);
                    templateSection.addView(txtTimeTemplateLayout);
                }
                if (type.equals("DATETIME")) {
                    TextInputLayout timeTemplateLayout = (TextInputLayout) LayoutInflater.from(appCompatActivity).inflate(R.layout.date_time, null);
                    TextInputEditText txtTimeLayout = timeTemplateLayout.findViewById(R.id.time);
                    String title = subJsonObject.getString("title");
                    txtTimeLayout.setHint("__:__  dd/mm/YYYY");
                    timeTemplateLayout.setTag(subJsonObject);
                    String maxTime = subJsonObject.getString("maxTime");
                    String pesanMaxTime = subJsonObject.getString("pesanMaxTime");
                    String minTime = subJsonObject.getString("minTime");
                    String pesanMinTime = subJsonObject.getString("pesanMinTime");
                    String maxDate = subJsonObject.getString("maxDate");
                    String minDate = subJsonObject.getString("minDate");

                    txtTimeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar cal = Calendar.getInstance();
                            TimePickerDialog timeset = new TimePickerDialog(appCompatActivity, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    cal.set(Calendar.MINUTE, minute);
                                    String inputTime = "null";
                                    boolean checkInput = false;
                                    if (!maxTime.equals("null")) {
                                        inputTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
                                        SimpleDateFormat convertToMilli = new SimpleDateFormat("HH:mm");
                                        long miliInput = 0, maxValid = 0;

                                        try {
                                            miliInput = convertToMilli.parse(inputTime).getTime();
                                            maxValid = convertToMilli.parse(maxTime).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (miliInput > maxValid) {
                                            if (!pesanMaxTime.equals("null")) {
                                                timeTemplateLayout.setHelperText(pesanMaxTime);
                                            } else {
                                                String text = "harus dibawah " + maxTime.toString();
                                                timeTemplateLayout.setHelperText(text);
                                            }
                                            checkInput = true;
                                        }

                                    }

                                    if (!minTime.equals("null")) {
                                        inputTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
                                        SimpleDateFormat convertToMilli = new SimpleDateFormat("HH:mm");
                                        long miliInput = 0, minValid = 0;

                                        try {
                                            miliInput = convertToMilli.parse(inputTime).getTime();
                                            minValid = convertToMilli.parse(minTime).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                        if (miliInput < minValid) {
                                            if (!pesanMinTime.equals("null")) {
                                                timeTemplateLayout.setHelperText(pesanMinTime);
                                            } else {
                                                String text = "harus diatas " + minTime.toString();
                                                timeTemplateLayout.setHelperText(text);
                                            }
                                            checkInput = true;
                                        }
                                    }

                                    if (!checkInput) {
                                        timeTemplateLayout.setHelperText("");
                                    }

                                    DatePickerDialog datePickerDialog = new DatePickerDialog(appCompatActivity, new DatePickerDialog.OnDateSetListener() {
                                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                            Calendar newDate = Calendar.getInstance();
                                            newDate.set(year, monthOfYear, dayOfMonth);
                                            txtTimeLayout.setText(dateFormatter.format(newDate.getTime()) + " " + new SimpleDateFormat("HH:mm").format(cal.getTime()));
                                        }

                                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                                    if (!maxDate.equals("null")) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date = null;
                                        try {
                                            date = sdf.parse(maxDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Long millis = date.getTime();
                                        datePickerDialog.getDatePicker().setMaxDate(millis);
                                    }

                                    if (!minDate.equals("null")) {
                                        SimpleDateFormat sdfmin = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date = null;
                                        try {
                                            date = sdfmin.parse(minDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Long millis = date.getTime();
                                        datePickerDialog.getDatePicker().setMinDate(millis);
                                    }
                                    datePickerDialog.show();


                                }
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                            timeset.show();
                        }
                    });
                    TextView label = new TextView(appCompatActivity);
                    label.setText(title);
                    label.setTextColor(Color.parseColor("#0f0f0f"));
                    label.setPadding(0, 50, 0, 0);
                    templateSection.addView(label);
                    templateSection.addView(timeTemplateLayout);
                }

            }
            linearLayout.addView(templateSection);

        }
        MaterialButton materialButton = (MaterialButton) LayoutInflater.from(appCompatActivity).inflate(R.layout.save_button, null);
        String textButton = jsonObject.getString("textButton");
        materialButton.setText(textButton);

        materialButton.setTag("SAVE");
        materialButton.setOnClickListener(v -> {
            List<FormData> formDatas = new ArrayList<>();
            boolean checkValid = repeatAnswer(linearLayout, formDatas, appCompatActivity);
            if (!checkValid) {
                AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
                String dialogText = null,
                        dialogBtnPositif = null,
                        dialogBtnNegatif = null;
                try {
                    dialogText = jsonObject.getString("dialogText");
                    dialogBtnPositif = jsonObject.getString("dialogBtnPositif");
                    dialogBtnNegatif = jsonObject.getString("dialogBtnNegatif");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.setMessage(dialogText)
                        .setPositiveButton(dialogBtnPositif, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dynamicFormCallback.success(formDatas);
                            }
                        }).setNegativeButton(dialogBtnNegatif, null);
                AlertDialog alert = builder.create();
                alert.show();

            }


        });

        linearLayout.addView(materialButton);
    }

    private static void validasiHuruf(TextInputEditText isi,JSONObject subJsonObject, TextInputLayout textInputLayout) {
        String huruf = isi.getText().toString();
        String mustContain = null,
                pesanContain = null,
                mustNotContain = null,
                pesanNotContain = null,
                regex = null,
                pesanRegex = null,
                minCharakter = "0",
                pesanMinChar = null,
                maxCharakter = null,
                pesanMaxChar = null;
        try {
            mustContain = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("contain");
            pesanContain = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("pesanContain");
            mustNotContain = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("notContain");
            pesanNotContain = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("pesanNotContain");
            regex = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("regex");
            pesanRegex = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("pesanRegex");
            minCharakter = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("minCharacter");
            pesanMinChar = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("pesanMinChar");
            maxCharakter = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("maxCharacter");
            pesanMaxChar = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("textValidate").getString("pesanMaxChar");
        }catch (JSONException e){

        }

        if(!huruf.isEmpty()){
            if(!mustContain.equals("null")){
                String contain = ".*"+mustContain+".*";
                boolean result = Pattern.matches(contain,huruf);

                if(!result) {
                    if (pesanContain.equals("null")){
                        textInputLayout.setHelperText("huruf tidak mengandung " + mustContain);
                    }else{
                        textInputLayout.setHelperText(pesanContain);
                    }
                }
            }

            if(!mustContain.equals("null")){
                String scontains = ".*"+mustNotContain+".*";
                boolean sresult = Pattern.matches(scontains,huruf);
                if(sresult){
                    if(pesanNotContain.equals("null")) {
                        textInputLayout.setHelperText("huruf mengandung " + mustNotContain);
                    }else{
                        textInputLayout.setHelperText(pesanNotContain);
                    }
                }
            }

            Integer minCharConvert = Integer.parseInt(convertNull(minCharakter));
            if(minCharConvert != 0 && huruf.length() < minCharConvert){
                if(pesanMinChar.equals("null")) {
                    textInputLayout.setHelperText("huruf kurang dari " + minCharakter);
                }else{
                    textInputLayout.    setHelperText(pesanMinChar);
                }
            }

            Integer maxCharConvert = Integer.parseInt(convertNull(maxCharakter));
            if(maxCharConvert != 0 && huruf.length() > maxCharConvert){
                if(pesanMaxChar.equals("null")) {
                    textInputLayout.setHelperText("huruf lebih dari " + maxCharakter);
                }else{
                    textInputLayout.setHelperText(pesanMaxChar);
                }
            }

            if(!regex.equals("null")){
               boolean result = Pattern.matches(regex,huruf);
               if(!result){
                   if(pesanRegex.equals("null")) {
                       textInputLayout.setHelperText("huruf tidak mengandung " + regex);
                   }else{
                       textInputLayout.setHelperText(pesanRegex);
                   }
               }
            }


        }
    }

    private static String convertNull(String checkChar) {
        if(checkChar.equals("null")){
            return checkChar = "0";
        }
        return checkChar;
    }


    private static boolean repeatAnswer(LinearLayout linearLayout,List<FormData> formDatas, AppCompatActivity appCompatActivity) {
        boolean checkPoint = false;
        for (int r = 2; r < linearLayout.getChildCount() -1 ; r++) {
            LinearLayout section = (LinearLayout) linearLayout.getChildAt(r);

            for (int i = 1; i < section.getChildCount() ; i++) {
                View view = section.getChildAt(i);

                if (view instanceof TextInputLayout) {
                    TextInputLayout textInputLayout = (TextInputLayout) view;
                    TextInputEditText textInputEditText = textInputLayout.findViewById(R.id.text);

                    if (textInputLayout.getHelperText() != null && !textInputLayout.getHelperText().equals("")) {
                        checkPoint = true;
                    }

                    String isi ;
                    if (checklayout(textInputEditText)) {

                        isi = textInputEditText.getText().toString();
                        if (textInputLayout.getTag() != null) {

                            JSONObject obdata = (JSONObject) textInputLayout.getTag();
                            JSONArray dataArr;
                            JSONObject dataJs;
                            String required = "false",title = "null", pesanRequired = "null",type = "null";

                            try {
                                dataArr = obdata.getJSONArray("validasi");
                                title = obdata.getString("title");
                                dataJs = dataArr.getJSONObject(0);
                                required = dataJs.getString("required");
                                pesanRequired = dataJs.getString("pesanRequired");
                                type = dataJs.getString("type");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (required.equals("true")) {
                                if (!isi.isEmpty()) {
                                    checkTypeHuruf(type, checkPoint, isi, textInputLayout);

                                } else {
                                    checkPoint = true;
                                    chekRequired(pesanRequired, textInputLayout);
                                }
                            } else {
                                checkTypeHuruf(type, checkPoint, isi, textInputLayout);
                            }
                            FormData formData = new FormData();

                            formData.setQuestion(title);
                            formData.setAnswer(isi);

                            formDatas.add(formData);
                        }
                    } else if (textInputLayout.findViewById(R.id.time) != null) {
                        textInputEditText = (TextInputEditText) textInputLayout.findViewById(R.id.time);
                        isi = textInputEditText.getText().toString();
                        if (textInputLayout.getTag() != null) {

                            JSONObject obdata = (JSONObject) textInputLayout.getTag();
                            String required = "false";
                            String pesanRequired = "null";
                            String title = "null";

                            try {
                                title = obdata.getString("title");
                                required = obdata.getString("validasi");
                                pesanRequired = obdata.getString("pesanValidasi");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (required.equals("required")) {
                                if (isi.isEmpty()) {
                                    checkPoint = true;
                                    chekRequired(pesanRequired, textInputLayout);
                                } else {
                                    textInputLayout.setHelperText("");
                                }
                            }
                            FormData formData = new FormData();

                            formData.setQuestion(title);
                            formData.setAnswer(isi);

                            formDatas.add(formData);
                        }
                    }
                }

                if (view instanceof CheckBox) {
                    CheckBox checkInputLyt = (CheckBox) view;
                    Boolean getText = checkInputLyt.isChecked();
                    String title = checkInputLyt.getText().toString();


                    if (checkInputLyt.getTag() != null) {
                        JSONObject obdata = (JSONObject) checkInputLyt.getTag();
                        String dataValidate = null,
                                pesanValidate = null;
                        try {
                            dataValidate = obdata.getString("validasi");
                            pesanValidate = obdata.getString("pesanValidasi");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (dataValidate.equals("required")) {
                            if (getText) {

                                checkInputLyt.setTextColor(Color.parseColor("#0f0f0f"));
                            } else {
                                if (pesanValidate.equals("null")) {
                                    String text = "checkbox required";
                                    Toast toast = Toast.makeText(appCompatActivity, text, Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(appCompatActivity, pesanValidate, Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                checkPoint = true;
                                checkInputLyt.setTextColor(Color.parseColor("#F13B15"));
                            }
                        }

                        FormData formData = new FormData();
                        formData.setQuestion(title);
                        formData.setAnswer(getText.toString());
                        formDatas.add(formData);
                    }
                }

                if (view instanceof RadioGroup) {
                    RadioGroup radioGroup = (RadioGroup) view;
                    TextView title = (TextView) radioGroup.getChildAt(0);
                    FormData formData = new FormData();
                    String dataValidate = null;
                    if (radioGroup.getTag() != null) {
                        JSONObject obdata = (JSONObject) radioGroup.getTag();
                        try {
                            dataValidate = obdata.getString("validasi");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    int setcheckempty = 0;

                    for (int y = 1; y < radioGroup.getChildCount(); y++) {
                        RadioButton radiobtn = (RadioButton) radioGroup.getChildAt(y);
                        if (dataValidate == "required") {
                            if (radiobtn.isChecked()) {
                                formData.setAnswer(radiobtn.getText().toString());
                                TextView gettxtpertanyaan = (TextView) radioGroup.getChildAt(0);
                                gettxtpertanyaan.setTextColor(Color.parseColor("#808080"));
                                setcheckempty += 1;
                            }
                            if (setcheckempty == 0) {
                                TextView gettxtpertanyaan = (TextView) radioGroup.getChildAt(0);
                                checkPoint = true;
                                gettxtpertanyaan.setTextColor(Color.parseColor("#F13B15"));
                            }
                        } else {
                            if (radiobtn.isChecked()) {
                                formData.setAnswer(radiobtn.getText().toString());
                            }
                        }
                    }
                    formData.setQuestion(title.getText().toString());
                    formDatas.add(formData);
                }

                if (view instanceof RecyclerView) {
                    RecyclerView checkGroup = (RecyclerView) view;
                    String dataValidate = null,
                            minAnswerNotify = null,
                            maxAnswerNotify = null;
                    int minAnswer = 0, maxAnswer = 0, checkEmpty = 0;
                    if (checkGroup.getTag() != null) {
                        JSONObject obdata = (JSONObject) checkGroup.getTag();
                        try {
                            dataValidate = obdata.getString("validasi");
                            minAnswer = Integer.parseInt(obdata.getString("minAnswer"));
                            maxAnswer = Integer.parseInt(obdata.getString("maxAnswer"));
                            minAnswerNotify = obdata.getString("minAnswerNotify");
                            maxAnswerNotify = obdata.getString("maxAnswerNotify");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int f = 0; f < checkGroup.getChildCount(); f++) {
                            CheckBox checkbtn = (CheckBox) checkGroup.getChildAt(f);

                            if (dataValidate.equals("required")) {
                                if (checkbtn.isChecked()) {
                                    checkbtn.setTextColor(Color.parseColor("#0f0f0f"));
                                    FormData formData = new FormData();
                                    formData.setAnswer(checkbtn.getText().toString());
                                    formData.setQuestion("pilihan");
                                    formDatas.add(formData);
                                    checkEmpty += 1;
                                } else {
                                    checkPoint = true;
                                    checkbtn.setTextColor(Color.parseColor("#F13B15"));
                                }
                            } else {
                                if (checkbtn.isChecked()) {
                                    FormData formData = new FormData();
                                    formData.setAnswer(checkbtn.getText().toString());
                                    formData.setQuestion("pilihan");
                                }
                            }
                        }
                        if (dataValidate.equals("required") && checkEmpty == 0) {
                            String text = "checkbox required";
                            Toast toast = Toast.makeText(appCompatActivity, text, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        if (minAnswer != 0 && checkEmpty < minAnswer) {
                            String text = minAnswerNotify;
                            Toast toast = Toast.makeText(appCompatActivity, text, Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        if (maxAnswer != 0 && checkEmpty > maxAnswer) {
                            String text = maxAnswerNotify;
                            Toast toast = Toast.makeText(appCompatActivity, text, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                }


            }

        };
        return checkPoint ;
    }

    private static void chekRequired(String pesanRequired,TextInputLayout textInputLayout) {
        if (pesanRequired.equals("null")){
            textInputLayout.setHelperText("required");
        }else{
            textInputLayout.setHelperText(pesanRequired);
        }

    }

    private static void checkTypeHuruf(String type,boolean checkPoint, String isi, TextInputLayout textInputLayout) {
        if(type.equals("email") && !Patterns.EMAIL_ADDRESS.matcher(isi).matches()){
            checkPoint = true;
            textInputLayout.setHelperText("masukan email");
        }else if(type.equals("url") && !Patterns.WEB_URL.matcher(isi).matches()){
            checkPoint = true;
            textInputLayout.setHelperText("masukan url");
        }
    }

    private static void validasiAngka(JSONObject subJsonObject, TextInputLayout textInputLayout, TextInputEditText isi) {
        JSONObject getValidateNumber = null;

        try {
            getValidateNumber = subJsonObject.getJSONArray("validasi").getJSONObject(0).getJSONObject("numberValidate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String convertNumber = isi.getText().toString();

        if(!convertNumber.isEmpty()){
            Integer angka = null;
            String min = null,
                    pesanMin = null,
                    max = null,
                    pesanMax = null,
                    minEqual=null,
                    pesanMinEqual = null,
                    maxEqual=null,
                    pesanMaxEqual =null,
                    equal=null,
                    pesanEqual = null,
                    notEqual=null,
                    pesanNotEqual = null,
                    betweenmin=null ,
                    betweenmax = null,
                    pesanBetween = null,
                    notbetweenmin = null,
                    notbetweenmax =  null,
                    pesanNotBetween = null;

            try {
                angka = Integer.parseInt(convertNumber);
                min = getValidateNumber.getString("min");
                pesanMin = getValidateNumber.getString("pesanMin");
                max = getValidateNumber.getString("max");
                pesanMax = getValidateNumber.getString("pesanMax");
                minEqual = getValidateNumber.getString("minEqual");
                pesanMinEqual = getValidateNumber.getString("pesanMinEqual");
                maxEqual = getValidateNumber.getString("maxEqual");
                pesanMaxEqual = getValidateNumber.getString("pesanMaxEqual");
                equal = getValidateNumber.getString("equal");
                pesanEqual = getValidateNumber.getString("pesanEqual");
                notEqual = getValidateNumber.getString("notEqual");
                pesanNotEqual = getValidateNumber.getString("pesanNotEqual");
                betweenmin = getValidateNumber.getString("betweenMin");
                betweenmax = getValidateNumber.getString("betweenMax");
                pesanBetween= getValidateNumber.getString("pesanBetween");
                notbetweenmin = getValidateNumber.getString("notBetweenMin");
                notbetweenmax = getValidateNumber.getString("notBetweenMax");
                pesanNotBetween = getValidateNumber.getString("pesanNotBetween");
            }catch(JSONException e){

                textInputLayout.setHelperText("tampilkan ini jika kosong");
            }


            if(!min.equals("null") && angka < Integer.parseInt(min)){
                if(pesanMin.equals("null")) {
                    textInputLayout.setHelperText("less than " + min);
                }else{
                    textInputLayout.setHelperText(pesanMin);
                }
            }

            if(!max.equals("null") && angka > Integer.parseInt(max)){
                if(pesanMax.equals("null")) {
                    textInputLayout.setHelperText("greater than" + max);
                }else{
                    textInputLayout.setHelperText(pesanMax);
                }
            }

            if(!minEqual.equals("null") && angka <= Integer.parseInt(minEqual)){
                if(pesanMinEqual.equals("null")) {
                    textInputLayout.setHelperText("less than or equal to " + minEqual);
                }else{
                    textInputLayout.setHelperText(pesanMinEqual);
                }
            }

            if(!maxEqual.equals("null") && angka >= Integer.parseInt(maxEqual)){
                if(pesanMaxEqual.equals("null")) {
                    textInputLayout.setHelperText("greater than or equal to " + maxEqual);
                }else{
                    textInputLayout.setHelperText(pesanMaxEqual);
                }
            }

            if(!equal.equals("null") && angka != Integer.parseInt(equal)){
                if(pesanEqual.equals("null")) {
                    textInputLayout.setHelperText("equal to " + equal);
                }else{
                    textInputLayout.setHelperText(pesanEqual);
                }
            }

            if(!notEqual.equals("null") && angka== Integer.parseInt(notEqual) ){
                if(pesanNotEqual.equals("null")){
                    textInputLayout.setHelperText("not equal " + notEqual);
                }else{
                    textInputLayout.setHelperText(pesanNotEqual);
                }
            }

            if(!betweenmin.equals("null") && !betweenmax.equals("null") && angka > Integer.parseInt(betweenmin) && angka < Integer.parseInt(betweenmax)){
                if(pesanBetween.equals("null")) {
                    textInputLayout.setHelperText("between " + betweenmin + " - " + notbetweenmax);
                }else{
                    textInputLayout.setHelperText(pesanBetween);
                }
            }

            if(!notbetweenmin.equals("null") && !notbetweenmax.equals("null")){
                if(angka < Integer.parseInt(notbetweenmin) || angka > Integer.parseInt(notbetweenmax)){
                    if(pesanNotBetween.equals("null")){
                        textInputLayout.setHelperText("not between  " + notbetweenmin +" - "+ notbetweenmax);
                    }else{
                        textInputLayout.setHelperText(pesanNotBetween);
                    }
                }
            }


        }
    }


    private static Boolean checklayout(TextInputEditText textInputEditText) {
        if(textInputEditText != null){
            return true;
        }
        return false;
    }

    private static void getQs(String isi) {
        String title = isi;
        String[] titleList = title.split(":");
        FormData formdate = new FormData();
        formdate.setQuestion(titleList[0]);
    }

    public static class FormData {
        private String question;
        private String answer;

        public String getQuestion() {
            return this.question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return this.answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}