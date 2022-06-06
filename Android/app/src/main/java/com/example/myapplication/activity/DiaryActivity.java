package com.example.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.database.DBHelper;
import com.example.myapplication.database.Diary;
import com.example.myapplication.request.SaveImageRequest;
import com.example.myapplication.utils.BitmapUtils;
import com.example.myapplication.utils.UserData;
import com.github.channguyen.rsv.RangeSliderView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity implements AutoPermissionsListener{
    private static final String TAG = "MainActivity";

    public static String FOLDER_PHOTO;

    //title
    TextView date;
    String today_date;

    //layout
    View layout_mor,layout_lun,layout_di,layout_sna;

    RadioGroup radioGroup;

    Button save_diary,btn_back;          //저장하기 버튼

    //데이터
    EditText[] et_diary;
    ImageView[] cameraImage;
    TextView[] et_food;
    Button[] btn_measure;
    int[] kcal = new int[4];
    int[] kcalCheck = new int[4];
    int tot_kcal;
    TextView tot_kcal_tv;
    int[] tanCheck = new int[4];
    int[] danCheck = new int[4];
    int[] giCheck = new int[4];

    //카메라 기능
    Bitmap[] resultPhotoBitmap = new Bitmap[4];
    int image_num,food_num;

    //switch
    Switch btn_share;
    TextView noshare_text,share_text;

    //slider
    RangeSliderView[] moodSlider;
    int[] moodIndex;

    //Dialog
    FloatingActionButton fabTakeButton[];

    //DB
    public static DBHelper mDatabase = null;
    Diary item;
    public static final int MODE_INSERT = 1;
    public static final int MODE_MODIFY = 2;
    int dbMode = MODE_INSERT;

    //외부DB
    String[] resultPhotoBitmap_request;

    //그래프
    BarChart bar_chart_diary;
    TextView tan_diary,dan_diary,gi_diary;
    int tan = 0;
    int dan = 0;
    int gi = 0;
    private int[] colorArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Title
        Intent intent = getIntent();
        today_date = intent.getStringExtra("날짜");
//        today_date = "May 28";
        date = findViewById(R.id.date);
        date.setText(today_date);

        //View
        this.InitializeView();
        initUI();
        VisibleView(layout_mor);

        //자동 권한 설정
        AutoPermissions.Companion.loadAllPermissions(this, 101);

        //라디오그룹
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        //저장하기 버튼
        save_diary = findViewById(R.id.save_diary);
        save_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbMode == MODE_INSERT){
                    saveNote();
                } else if(dbMode == MODE_MODIFY){
                    modifyNote();
                }
                if(btn_share.isChecked()){
                    changeProfileImageToDB();
                }
            }
        });

        // 데이터베이스 열기
        openDatabase();

        item = getTableData();

        applyItem();

        setPicturePath();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DiaryActivity.this,CalendarActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        DiaryActivity_loadc.loadCheck = false;
        startActivity(intent);
        finish();
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.radioButton_mor) {
                VisibleView(layout_mor);
                DiaryActivity_loadc.loadCheck = false;
            }
            else if(i == R.id.radioButton_lun){
                VisibleView(layout_lun);
                DiaryActivity_loadc.loadCheck = false;
            }
            else if(i == R.id.radioButton_di){
                VisibleView(layout_di);
                DiaryActivity_loadc.loadCheck = false;
            }
            else if(i == R.id.radioButton_sna){
                VisibleView(layout_sna);
                DiaryActivity_loadc.loadCheck = false;
            }
        }
    };

    //region layout
    public void InitializeView(){
        layout_mor = (LinearLayout)findViewById(R.id.layout_mor);
        layout_lun = (LinearLayout)findViewById(R.id.layout_lun);
        layout_di = (LinearLayout)findViewById(R.id.layout_di);
        layout_sna = (LinearLayout)findViewById(R.id.layout_sna);
    }

    public void VisibleView(View view){
        layout_mor.setVisibility(View.INVISIBLE);
        layout_lun.setVisibility(View.INVISIBLE);
        layout_di.setVisibility(View.INVISIBLE);
        layout_sna.setVisibility(View.INVISIBLE);

        view.setVisibility(View.VISIBLE);
    }

    private void initUI() {
        noshare_text = findViewById(R.id.noshare_text);
        share_text = findViewById(R.id.share_Text);
        btn_share = findViewById(R.id.btn_share);
        tot_kcal_tv = findViewById(R.id.tot_kcal);

        btn_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    share_text.setVisibility(View.VISIBLE);
                    noshare_text.setVisibility(View.INVISIBLE);
                }else{
                    share_text.setVisibility(View.INVISIBLE);
                    noshare_text.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_measure = new Button[4];
        for(int i = 0;i<btn_measure.length;i++){
            String btn_measure_id = "btn_measure_"+i;
            btn_measure[i] = findViewById(getResources().getIdentifier(btn_measure_id,"id",getPackageName()));
            btn_measure[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] words = new String[]{"로드셀로 측정하기","1인분으로 측정하기"};
                    final int[] count = new int[1];

                    // 대화상자 생성 //
                    AlertDialog.Builder builder = new AlertDialog.Builder(DiaryActivity.this);

                    builder.setTitle("무게 측정하기");            //setTitle -> 제목설정
                    builder.setIcon(R.mipmap.ic_launcher);        //setIcon -> 아이콘 설정

                    //  setPositiveButton -> "OK"버튼  //
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(count[0] == 0) {
                                Intent intent = new Intent(getApplicationContext(), DiaryActivity_loadc.class);
                                startActivity(intent);
                            }else {
                                DiaryActivity_loadc.kcal_g = 0.0;
                            }
                            DiaryActivity_loadc.loadCheck = true;
                        }
                    });

                    //  setSingleChoiceItems -> 라디오버튼 목록 출력  //
                    builder.setSingleChoiceItems(words, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            count[0] = i;
                        }
                    });

                    builder.show();      //대화상자(dialog)화면 출력

                }
            });
        }

        et_food = new TextView[4];
        for(int i = 0;i<et_food.length;i++) {
            int num = i;
            String et_food_id = "et_food_" + i;
            et_food[i] = findViewById(getResources().getIdentifier(et_food_id, "id", getPackageName()));
            et_food[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!DiaryActivity_loadc.loadCheck){
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(DiaryActivity.this);
                        builder.setTitle("식사량 측정");
                        builder.setMessage("식사량을 먼저 측정해주세요.");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        });
                        builder.show();
                    }else{
                        String[] words;
                        if(et_food[num].getText().toString().length() != 0){
                            words = new String[]{"이미지로 검색하기","이름으로 검색하기","음식 이름 삭제하기"};
                        }else{
                            words = new String[]{"이미지로 검색하기","이름으로 검색하기"};
                        }

                        final int[] count = new int[1];

                        // 대화상자 생성 //
                        AlertDialog.Builder builder = new AlertDialog.Builder(DiaryActivity.this);

                        builder.setTitle("음식 검색하기");            //setTitle -> 제목설정
                        builder.setIcon(R.mipmap.ic_launcher);        //setIcon -> 아이콘 설정

                        //  setPositiveButton -> "OK"버튼  //
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                food_num = num;
                                Intent intent = new Intent(getApplicationContext(), DiaryActivity_search.class);
                                intent.putExtra("num_i",food_num);
                                if(count[0] == 0){
                                    if(resultPhotoBitmap[food_num] != null){
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        resultPhotoBitmap[food_num].compress(Bitmap.CompressFormat.JPEG,100,stream);
                                        byte[] byteArray = stream.toByteArray();
                                        intent.putExtra("image",byteArray);
                                    } else{
                                        Toast.makeText(DiaryActivity.this,"이미지를 등록해주세요.",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }else if(count[0] == 2){
                                    et_food[num].setText(null);
                                    tan -= tanCheck[num];
                                    Log.e("탄수화물",tanCheck[num]+" "+num);
                                    tanCheck[num] = 0;
                                    Log.e("탄수화물",danCheck[num]+" "+num);
                                    dan -= danCheck[num];
                                    danCheck[num] = 0;
                                    Log.e("탄수화물",giCheck[num]+" "+num);
                                    gi -= giCheck[num];
                                    giCheck[num] = 0;
                                    tot_kcal -= kcalCheck[num];
                                    kcalCheck[num] = 0;
                                    tot_kcal_tv.setText(tot_kcal + "Kcal");
                                    DiaryActivity_loadc.loadCheck = false;
                                    CreatebarChart(bar_chart_diary);
                                    return;
                                }
                                foodNameResult.launch(intent);
                            }
                        });

                        //  setSingleChoiceItems -> 라디오버튼 목록 출력  //
                        builder.setSingleChoiceItems(words, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                count[0] = i;
                            }
                        });

                        builder.show();      //대화상자(dialog)화면 출력
                    }
                }
            });
        }


        et_diary = new EditText[4];
        for(int i = 0; i<et_diary.length; i++){
            String et_diary_id = "et_diary_"+i;
            et_diary[i] = findViewById(getResources().getIdentifier(et_diary_id,"id",getPackageName()));
        }

        cameraImage = new ImageView[4];
        for(int i = 0; i<cameraImage.length; i++){
            String cameraImage_id = "cameraImage_"+i;
            cameraImage[i] = findViewById(getResources().getIdentifier(cameraImage_id,"id",getPackageName()));
        }

        fabTakeButton =  new FloatingActionButton[4];
        for(int i = 0; i<fabTakeButton.length; i++){
            int num = i;
            String fabTakeButton_id = "fabTakePicture"+i;
            fabTakeButton[i] = findViewById(getResources().getIdentifier(fabTakeButton_id,"id",getPackageName()));
            fabTakeButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_num = num;
                    showImageDialog();
                }
            });
        }

        //slider
        moodSlider = new RangeSliderView[4];
        moodIndex = new int[4];
        final RangeSliderView.OnSlideListener listener0 = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                moodIndex[0] = index;
            }
        };
        final RangeSliderView.OnSlideListener listener1 = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                moodIndex[1] = index;
            }
        };
        final RangeSliderView.OnSlideListener listener2 = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                moodIndex[2] = index;
            }
        };
        final RangeSliderView.OnSlideListener listener3 = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                moodIndex[3] = index;
            }
        };
        for(int i = 0; i<moodSlider.length; i++){
            String moodSlider_id = "sliderView_"+i;
            moodSlider[i] = findViewById(getResources().getIdentifier(moodSlider_id,"id",getPackageName()));
        }
        moodSlider[0].setOnSlideListener(listener0);
        moodSlider[0].setInitialIndex(2);
        moodSlider[1].setOnSlideListener(listener1);
        moodSlider[1].setInitialIndex(2);
        moodSlider[2].setOnSlideListener(listener2);
        moodSlider[2].setInitialIndex(2);
        moodSlider[3].setOnSlideListener(listener3);
        moodSlider[3].setInitialIndex(2);

        bar_chart_diary = findViewById(R.id.bar_chart_diary);
        tan_diary = findViewById(R.id.tan_diary);
        dan_diary  = findViewById(R.id.dan_diary);
        gi_diary = findViewById(R.id.gi_diary);
        CreatebarChart(bar_chart_diary);

    }

    ActivityResultLauncher<Intent> foodNameResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String foodName = result.getData().getStringExtra("foodName");
                        double tan_dou = result.getData().getDoubleExtra("tan", 0);
                        double dan_dou = result.getData().getDoubleExtra("dan", 0);
                        double gi_dou = result.getData().getDoubleExtra("gi", 0);
                        int num = result.getData().getIntExtra("num", 0);
                        kcal[num] = (int) result.getData().getDoubleExtra("kcal", 0);
                        if (DiaryActivity_loadc.kcal_g != 0.0) {
                            Double realkcal = DiaryActivity_loadc.kcal_g / 100;
                            Log.e("리얼 칼", realkcal + "");
                            if (et_food[num].getText().toString().length() == 0) {
                                et_food[num].setText(foodName);
                            } else {
                                et_food[num].setText(et_food[num].getText().toString() + "," + foodName);
                            }
                            kcalCheck[num] = kcalCheck[num] + (int) (kcal[num]*realkcal);
                            tot_kcal = tot_kcal + (int) (kcal[num]*realkcal);
                            tot_kcal_tv.setText(tot_kcal + "Kcal");
                            tanCheck[num] = tanCheck[num] + (int)(tan_dou*realkcal);
                            tan = (int) (tan + (int) tan_dou * realkcal);
                            danCheck[num] = danCheck[num] + (int)(dan_dou*realkcal);
                            dan = (int) (dan + (int) dan_dou * realkcal);
                            giCheck[num] = giCheck[num] + (int)(gi_dou*realkcal);
                            gi = (int) (gi + (int) gi_dou * realkcal);
                        } else {
                            if (et_food[num].getText().toString().length() == 0) {
                                et_food[num].setText(foodName);
                            } else {
                                et_food[num].setText(et_food[num].getText().toString() + "," + foodName);
                            }
                            kcalCheck[num] = kcalCheck[num] + kcal[num];
                            tot_kcal = (tot_kcal + kcal[num]);
                            tot_kcal_tv.setText(tot_kcal + "Kcal");
                            tanCheck[num] = tanCheck[num] + (int)(tan_dou);
                            tan = (tan + (int) tan_dou);
                            danCheck[num] = danCheck[num] + (int)(dan_dou);
                            dan = (dan + (int) dan_dou);
                            giCheck[num] = giCheck[num] + (int)(gi_dou);
                            gi = (gi + (int) gi_dou);
                        }
                        DiaryActivity_loadc.loadCheck = false;
                        CreatebarChart(bar_chart_diary);
                    }
                }
            }
    );

    public void setPicture0(String picturePath, int sampleSize){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap[0] = BitmapFactory.decodeFile(picturePath, options);

        cameraImage[0].setImageBitmap(resultPhotoBitmap[0]);
    }

    public void setFood0(String data){
        et_food[0].setText(data);
    }

    public void setMood0(String mood) {
        try {
            moodIndex[0] = Integer.parseInt(mood);
            moodSlider[0].setInitialIndex(moodIndex[0]);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setComment0(String data){
        et_diary[0].setText(data);
    }

    public void setPicture1(String picturePath, int sampleSize){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap[1] = BitmapFactory.decodeFile(picturePath, options);

        cameraImage[1].setImageBitmap(resultPhotoBitmap[1]);
    }

    public void setFood1(String data){
        et_food[1].setText(data);
    }

    public void setMood1(String mood) {
        try {
            moodIndex[1] = Integer.parseInt(mood);
            moodSlider[1].setInitialIndex(moodIndex[1]);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setComment1(String data){
        et_diary[1].setText(data);
    }

    public void setPicture2(String picturePath, int sampleSize){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap[2] = BitmapFactory.decodeFile(picturePath, options);

        cameraImage[2].setImageBitmap(resultPhotoBitmap[2]);
    }

    public void setFood2(String data){
        et_food[2].setText(data);
    }

    public void setMood2(String mood) {
        try {
            moodIndex[2] = Integer.parseInt(mood);
            moodSlider[2].setInitialIndex(moodIndex[2]);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setComment2(String data){
        et_diary[2].setText(data);
    }

    public void setPicture3(String picturePath, int sampleSize){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap[3] = BitmapFactory.decodeFile(picturePath, options);

        cameraImage[3].setImageBitmap(resultPhotoBitmap[3]);
    }

    public void setFood3(String data){
        et_food[3].setText(data);
    }

    public void setMood3(String mood) {
        try {
            moodIndex[3] = Integer.parseInt(mood);
            moodSlider[3].setInitialIndex(moodIndex[3]);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setComment3(String data){
        et_diary[3].setText(data);
    }

    public void setTan(String data){tan = Integer.parseInt(data);}

    public void setDan(String data){dan = Integer.parseInt(data);}

    public void setGi(String data){gi = Integer.parseInt(data);}

    public void setKcal(String data){tot_kcal = Integer.parseInt(data);}

    public void setItem(Diary item){
        this.item = item;
    }

    public void applyItem(){
        if (item != null){
//            Toast.makeText(getApplicationContext(),"item not null",Toast.LENGTH_SHORT).show();
            dbMode = MODE_MODIFY;

            String[] picturePath = new String[4];
            picturePath[0] = item.getPicture0();
            picturePath[1] = item.getPicture1();
            picturePath[2] = item.getPicture2();
            picturePath[3] = item.getPicture3();

            if (picturePath[0] == null || picturePath[0].equals("")){
                cameraImage[0].setImageResource(R.drawable.icon_camera);
            } else{
                setPicture0(item.getPicture0(),1);
            }

            if (picturePath[1] == null || picturePath[1].equals("")){
                cameraImage[1].setImageResource(R.drawable.icon_camera);
            } else{
                setPicture1(item.getPicture1(),1);
            }

            if (picturePath[2] == null || picturePath[2].equals("")){
                cameraImage[2].setImageResource(R.drawable.icon_camera);
            } else{
                setPicture2(item.getPicture2(),1);
            }

            if (picturePath[3] == null || picturePath[3].equals("")){
                cameraImage[3].setImageResource(R.drawable.icon_camera);
            } else{
                setPicture3(item.getPicture3(),1);
            }

            setFood0(item.getFood0());
            setFood1(item.getFood1());
            setFood2(item.getFood2());
            setFood3(item.getFood3());

            setMood0(item.getMood0());
            setMood1(item.getMood1());
            setMood2(item.getMood2());
            setMood3(item.getMood3());

            setComment0(item.getComment0());
            setComment1(item.getComment1());
            setComment2(item.getComment2());
            setComment3(item.getComment3());

            setTan(item.getTan());
            setDan(item.getDan());
            setGi(item.getGi());
            setKcal(item.getKcal());

            tot_kcal_tv.setText(tot_kcal+ "Kcal");

            CreatebarChart(bar_chart_diary);
        }else{

//            Toast.makeText(getApplicationContext(),"item null",Toast.LENGTH_SHORT).show();
            dbMode = MODE_INSERT;

            cameraImage[0].setImageResource(R.drawable.icon_camera);
            cameraImage[1].setImageResource(R.drawable.icon_camera);
            cameraImage[2].setImageResource(R.drawable.icon_camera);
            cameraImage[3].setImageResource(R.drawable.icon_camera);

            setFood0("");
            setFood1("");
            setFood2("");
            setFood3("");

            setMood0("2");
            setMood1("2");
            setMood2("2");
            setMood3("2");

            setComment0("");
            setComment1("");
            setComment2("");
            setComment3("");

            tan = 0;
            dan = 0;
            gi = 0;
            tot_kcal_tv.setText("0Kcal");

            CreatebarChart(bar_chart_diary);

        }
    }

    //endregion

    private void CreatebarChart(BarChart barChart){

        if(tan < 0){
            tan = 0;
        }
        if(dan < 0){
            dan = 0;
        }
        if(gi < 0){
            gi = 0;
        }
        //내부 데이터
        ArrayList<BarEntry> dataValue = new ArrayList<>();
        dataValue.add(new BarEntry(0,gi));
        dataValue.add(new BarEntry(1,dan));
        dataValue.add(new BarEntry(2,tan));
        tan_diary.setText(tan*4+"kcal");
        dan_diary.setText(dan*4+"kcal");
        gi_diary.setText(gi*9+"kcal");


        //라벨 데이터
//        ArrayList<String> getXAxisValues = new ArrayList<>();
//        getXAxisValues.add("탄수화물");
//        getXAxisValues.add("단백질");
//        getXAxisValues.add("지방");

        colorArray = new int[]{Color.parseColor("#263545")};

        BarDataSet barDataSet = new BarDataSet(dataValue,"");
        barDataSet.setColors(colorArray);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setDrawValues(true);
//        //경계선
//        barDataSet.setBarBorderWidth(2f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String.valueOf((int) value)) + "kcal";
            }
        });

        BarData barData = new BarData(barDataSet);

//        //데이터 값 넓이 설정
        barData.setBarWidth(0.7f);
//        barData.setValueTextSize(12);

//        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        //격자 선 제거
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        //최대,최소,차트 개수
//        barChart.getAxisLeft().setAxisMaximum(1500);
//        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.setMaxVisibleValueCount(3);
        //간격
//        barChart.setExtraOffsets(0f,0f,0f,0f);
        //상호작용 제거
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setTouchEnabled(false);

//        //세로축 이름
//        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues));

        //데이터 내부에 표시
//        barChart.setDrawValueAboveBar(true);

        // Hide graph legend
        barChart.getLegend().setEnabled(false);

        //적용
        barChart.invalidate();

    }

    //region Dialog
    private void showImageDialog() {

        // 업로드 방법 선택 대화상자 보이기
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(galleryIntent, "사진 업로드");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        imageActivityLauncher.launch(chooser);
    }

    ActivityResultLauncher<Intent> imageActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        if (result.getData().getExtras() != null) {
                            // 카메라 결과 획득
                            resultPhotoBitmap[image_num] = (Bitmap) result.getData().getExtras().get("data");
                        } else {
                            // 갤러리(포토) 결과 획득
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                resultPhotoBitmap[image_num] = BitmapUtils.getBitmapFromUri(DiaryActivity.this, uri);
                            }
                        }
                        cameraImage[image_num].setImageBitmap(resultPhotoBitmap[image_num]);
                    }
                }
            }
    );



    //endregion


    //region 내부 DB
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    public  void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = DBHelper.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "database is open.");
        } else {
            Log.d(TAG, "database is not open.");
        }
    }

    private void saveNote() {
        mDatabase.db = mDatabase.Writedb();
        String[] foodName = new String[4];
        String[] comment = new String[4];
        for(int i = 0; i < 4; i++){
            foodName[i] = et_food[i].getText().toString();
            comment[i] = et_diary[i].getText().toString();
        }

        String sql = "insert into " + DBHelper.TABLE_NAME +
                "(_id, PICTURE0, FOOD0, MOOD0, COMMENT0, PICTURE1, FOOD1, MOOD1, COMMENT1, PICTURE2, FOOD2, MOOD2, COMMENT2, PICTURE3, FOOD3, MOOD3, COMMENT3, TAN, DAN, GI, KCAL) values(" +
                "'"+ today_date.replace(" ","")  + "', " +
                "'"+ savePicture(0) + "', " +
                "'"+ foodName[0] + "', " +
                "'"+ moodIndex[0] + "', " +
                "'"+ comment[0] + "', " +
                "'"+ savePicture(1) + "', " +
                "'"+ foodName[1] + "', " +
                "'"+ moodIndex[1] + "', " +
                "'"+ comment[1] + "', " +
                "'"+ savePicture(2) + "', " +
                "'"+ foodName[2] + "', " +
                "'"+ moodIndex[2] + "', " +
                "'"+ comment[2] + "', " +
                "'"+ savePicture(3) + "', " +
                "'"+ foodName[3] + "', " +
                "'"+ moodIndex[3] + "', " +
                "'"+ comment[3] + "', " +
                "'"+ tan + "', "+
                "'"+ dan + "', "+
                "'"+ gi + "', " +
                "'"+ tot_kcal + "')";

        DBHelper database = DBHelper.getInstance(getApplicationContext());
        database.execSQL(sql);
        Intent intent = new Intent(DiaryActivity.this,CalendarActivity.class);
        startActivity(intent);
        finish();

//        Toast.makeText(getApplicationContext(),"DB저장 완료",Toast.LENGTH_SHORT).show();
    }

    private void modifyNote() {
        mDatabase.db = mDatabase.Writedb();
        String[] foodName = new String[4];
        String[] comment = new String[4];


        for(int i = 0; i < 4; i++){
            foodName[i] = et_food[i].getText().toString();
            comment[i] = et_diary[i].getText().toString();
        }

        if (item != null) {
            // update note
            String sql = "update " + DBHelper.TABLE_NAME +
                    " set " +
                    "   PICTURE0 = '" + savePicture(0) + "'" +
                    "   ,FOOD0 = '" + foodName[0] + "'" +
                    "   ,MOOD0 = '" + moodIndex[0] + "'" +
                    "   ,COMMENT0 = '" + comment[0] + "'" +
                    "   ,PICTURE1 = '" + savePicture(1) + "'" +
                    "   ,FOOD1 = '" + foodName[1] + "'" +
                    "   ,MOOD1 = '" + moodIndex[1] + "'" +
                    "   ,COMMENT1 = '" + comment[1] + "'" +
                    "   ,PICTURE2 = '" + savePicture(2) + "'" +
                    "   ,FOOD2 = '" + foodName[2] + "'" +
                    "   ,MOOD2 = '" + moodIndex[2] + "'" +
                    "   ,COMMENT2 = '" + comment[2] + "'" +
                    "   ,PICTURE3 = '" + savePicture(3) + "'" +
                    "   ,FOOD3 = '" + foodName[3] + "'" +
                    "   ,MOOD3 = '" + moodIndex[3] + "'" +
                    "   ,COMMENT3 = '" + comment[3] + "'" +
                    "   ,TAN = '" + tan + "'" +
                    "   ,DAN = '" + dan + "'" +
                    "   ,GI = '" + gi + "'" +
                    "   ,KCAL = '" + tot_kcal + "'" +
                    " where " +
                    "   _id " + "like '" + today_date.replace(" ","") + "'";

            DBHelper database = DBHelper.getInstance(getApplicationContext());
            database.execSQL(sql);
            Intent intent = new Intent(DiaryActivity.this,CalendarActivity.class);
            startActivity(intent);
            finish();
//            Toast.makeText(getApplicationContext(),"DB저장 변경",Toast.LENGTH_SHORT).show();
        }
    }

    private Diary getTableData(){
        try {
            mDatabase.db = mDatabase.Readdb();
            String sql = "SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE _id='" + today_date.replace(" ","") +"'";

            Diary diary = null;

            if(mDatabase.db != null){
                Cursor mCur = mDatabase.db.rawQuery(sql,null);

                int count = mCur.getCount();

                for(int i = 0; i<count; i++){
                    mCur.moveToNext();

                    diary = new Diary();

                    diary.set_id(mCur.getString(0));
                    diary.setPicture0(mCur.getString(1));
                    diary.setFood0(mCur.getString(2));
                    diary.setMood0(mCur.getString(3));
                    diary.setComment0(mCur.getString(4));
                    diary.setPicture1(mCur.getString(5));
                    diary.setFood1(mCur.getString(6));
                    diary.setMood1(mCur.getString(7));
                    diary.setComment1(mCur.getString(8));
                    diary.setPicture2(mCur.getString(9));
                    diary.setFood2(mCur.getString(10));
                    diary.setMood2(mCur.getString(11));
                    diary.setComment2(mCur.getString(12));
                    diary.setPicture3(mCur.getString(13));
                    diary.setFood3(mCur.getString(14));
                    diary.setMood3(mCur.getString(15));
                    diary.setComment3(mCur.getString(16));
                    diary.setTan(mCur.getString(17));
                    diary.setDan(mCur.getString(18));
                    diary.setGi(mCur.getString(19));
                    diary.setKcal(mCur.getString(20));

//                    Toast.makeText(getApplicationContext(),"레코드 #" + i + " : " + diary.get_id(),Toast.LENGTH_SHORT).show();
                }
            }
            return diary;
        }
        catch (SQLException mSQlException)
        {
            throw mSQlException;
        }
    }


    private String savePicture(int i) {
        if (resultPhotoBitmap[i] == null) {
//            Toast.makeText(getApplicationContext(), "No picture to be saved.", Toast.LENGTH_SHORT).show();
            return "";
        }

        File photoFolder = new File(DiaryActivity.FOLDER_PHOTO);

        if(!photoFolder.isDirectory()) {
            photoFolder.mkdirs();
        }

        String photoFilename = createFilename();
        String picturePath = photoFolder + File.separator + photoFilename;

        try {
            FileOutputStream outstream = new FileOutputStream(picturePath);
            resultPhotoBitmap[i].compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }

    public void setPicturePath() {
        String folderPath = getFilesDir().getAbsolutePath();
        FOLDER_PHOTO = folderPath + File.separator + "photo";

        File photoFolder = new File(FOLDER_PHOTO);
        if (!photoFolder.exists()) {
            photoFolder.mkdirs();
        }
    }
    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }
    //endregion

    //region 외부 DB
    /**비트맵을 바이너리 바이트배열로 바꾸어주는 메서드 */
    public String bitmapToByteArray(Bitmap bitmap) {
        String image = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        image = byteArrayToBinaryString(byteArray);
        return image;
    }

    /**바이너리 바이트 배열을 스트링으로 바꾸어주는 메서드 */
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    /**바이너리 바이트를 스트링으로 바꾸어주는 메서드 */
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    /**
     * bitmap을 string으로
     * @param bitmap
     * @return
     */
    public String BitMapToString(Bitmap bitmap){
        String temp = "";

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);	//bitmap compress
        byte [] arr=baos.toByteArray();
        String image= Base64.encodeToString(arr, Base64.DEFAULT);


        try{
            temp=URLEncoder.encode(image,"utf-8");
        }catch (Exception e){
            Log.e("exception",e.toString());
        }

        return temp;
    }

    private Bitmap resize(Bitmap bm){
        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=800)
            bm = Bitmap.createScaledBitmap(bm, 400, 240, true);
        else if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
    }

    private void changeProfileImageToDB() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //서버에 로그인 요청을 하면 결과값을 json타입으로 받음
                    JSONObject jsonObject = new JSONObject(response);
                    //서버통신 성공여부
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Log.e(TAG, "외부 DB success");
                    } else {
                        Log.e(TAG, "외부 DB failed");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        resultPhotoBitmap_request = new String[4];
        for(int i = 0; i<4; i++){
            if(resultPhotoBitmap[i] != null){
                resultPhotoBitmap[i] = resize(resultPhotoBitmap[i]);
//                resultPhotoBitmap_request[i] = bitmapToByteArray(resultPhotoBitmap[i]);
                resultPhotoBitmap_request[i] = BitMapToString(resultPhotoBitmap[i]);

            }else{
                resultPhotoBitmap_request[i] = "";
            }
            if(et_food[i] == null){
                et_food[i].setText("");
            }
        }
        //vollyer를 이용해서 서버에 요청
        SaveImageRequest imageRequest = new SaveImageRequest(UserData.read("user_id",""), resultPhotoBitmap_request[0], et_food[0].getText().toString(),
                resultPhotoBitmap_request[1] ,et_food[1].getText().toString(),
                resultPhotoBitmap_request[2],et_food[2].getText().toString(),
                resultPhotoBitmap_request[3],et_food[3].getText().toString(), tot_kcal_tv.getText().toString(), responseListener);
//        imagetest imagetest1 = new imagetest(resultPhotoBitmap_request[0],responseListener);
        RequestQueue queue = Volley.newRequestQueue(DiaryActivity.this);
        queue.add(imageRequest);
        Log.e(TAG, "외부 DB 연결 성공");
        Log.e(TAG, resultPhotoBitmap_request[0]);
    }

    //endregion

    //region 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this,requestCode,permissions,this);
    }


    @Override
    public void onDenied(int i, String[] permissions) {
//        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGranted(int i, String[] permissions) {
//        Toast.makeText(this,"permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    //endregion

}

