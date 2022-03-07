package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.database.DBHelper;
import com.example.myapplication.database.Diary;
import com.example.myapplication.login.ImageRequest;
import com.github.channguyen.rsv.RangeSliderView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener{
    private static final String TAG = "MainActivity";

    public static String FOLDER_PHOTO;

    //title
    TextView date;
    String today_date;

    //layout
    View layout_mor,layout_lun,layout_di,layout_sna;

    RadioGroup radioGroup;

    Button save_diary;          //저장하기 버튼

    EditText[] et_diary;
    ImageView[] cameraImage;
    TextView[] et_food;

    //카메라 기능
    File file;
    Uri fileUri;
    Bitmap[] resultPhotoBitmap = new Bitmap[4];
    int image_num;

    //slider
    RangeSliderView[] moodSlider;
    int[] moodIndex;

    //DB
    public static DBHelper mDatabase = null;
    Diary item;
    public static final int MODE_INSERT = 1;
    public static final int MODE_MODIFY = 2;
    int dbmode = MODE_INSERT;
    public List<Diary> diaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Title
        Intent intent = getIntent();
        today_date = intent.getStringExtra("날짜");
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
                if(dbmode == MODE_INSERT){
                    saveNote();
                } else if(dbmode == MODE_MODIFY){
                    modifyNote();
                }

//                String image_lun = mainfragment_di.getView().findViewById(R.id.cameraImage);
//                changeProfileImageToDB(image);

            }
        });

        // 데이터베이스 열기
        openDatabase();

        item = getTableData();

        applyItem();

        setPicturePath();

    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.radioButton_mor) {
                VisibleView(layout_mor);
            }
            else if(i == R.id.radioButton_lun){
                VisibleView(layout_lun);
            }
            else if(i == R.id.radioButton_di){
                VisibleView(layout_di);
            }
            else if(i == R.id.radioButton_sna){
                VisibleView(layout_sna);
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
        et_food = new TextView[4];
        for(int i = 0;i<et_food.length;i++){
            int num = i;
            String et_food_id = "et_food_"+i;
            et_food[i] = findViewById(getResources().getIdentifier(et_food_id,"id",getPackageName()));
            et_food[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity_search.class);
                    intent.putExtra("num_i",num);
                    foodNameResult.launch(intent);
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
            int num = i;
            String cameraImage_id = "cameraImage_"+i;
            cameraImage[i] = findViewById(getResources().getIdentifier(cameraImage_id,"id",getPackageName()));
            cameraImage[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_num = num;
                    TakePicture();
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


    }

    ActivityResultLauncher<Intent> foodNameResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        String foodName = result.getData().getStringExtra("foodName");
                        int num = result.getData().getIntExtra("num",0);
                        if(foodName != null){
                            et_food[num].setText(foodName);
                        }
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

    public void setItem(Diary item){
        this.item = item;
    }

    public void applyItem(){
        if (item != null){
            Toast.makeText(getApplicationContext(),"item not null",Toast.LENGTH_SHORT).show();
            dbmode = MODE_MODIFY;

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
        }else{

            Toast.makeText(getApplicationContext(),"item null",Toast.LENGTH_SHORT).show();
            dbmode = MODE_INSERT;

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
        }
    }




    //endregion

    //region 카메라
    private void TakePicture() {
        try {
            if (file == null) {
                file = createFile();
            }else{
                file.delete();
            }

            file.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(getApplicationContext(), "org.techtown.caps.fileprovider", file);
        }else{
            fileUri = Uri.fromFile(file);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

        startActivityResult.launch(intent);
    }

    private File createFile() {
        String filename = "capture.jpg";

        File storageDir = getApplicationContext().getExternalCacheDir();
        File outFile = null;
        try {
            outFile = File.createTempFile("android_upload", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outFile;
    }

    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(file.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int exifOrientation;
                        int exifDegree;

                        if (exif != null) {
                            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            exifDegree = exifOrientationToDegrees(exifOrientation);
                        } else {
                            exifDegree = 0;
                        }

                        if (bitmap == null) {
                            Toast.makeText(getApplicationContext(), "할당 안됨", Toast.LENGTH_LONG).show();
                        } else {
                            resultPhotoBitmap[image_num] = resizeImage(rotateImage(bitmap,exifDegree));
                            cameraImage[image_num].setImageBitmap(resultPhotoBitmap[image_num]);
                        }
                    }
                }
            }
    );

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    //이미지 회전
    private Bitmap rotateImage(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap resizeImage(Bitmap bitmap){
        int width = cameraImage[image_num].getWidth();
        int height = cameraImage[image_num].getHeight();
        float bmpWidth = bitmap.getWidth();
        float bmpHeight = bitmap.getHeight();

        if (bmpWidth > width) {
            // [원하는 너비보다 클 경우의 설정]
            float mWidth = bmpWidth / 100;
            float scale = width/ mWidth;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        } else if (bmpHeight > height) {
            // [원하는 높이보다 클 경우의 설정]
            float mHeight = bmpHeight / 100;
            float scale = height/ mHeight;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        }

        return  Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);
    }
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

    public void openDatabase() {
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
                "(_id, PICTURE0, FOOD0, MOOD0, COMMENT0, PICTURE1, FOOD1, MOOD1, COMMENT1, PICTURE2, FOOD2, MOOD2, COMMENT2, PICTURE3, FOOD3, MOOD3, COMMENT3) values(" +
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
                "'"+ comment[3] + "')";

        DBHelper database = DBHelper.getInstance(getApplicationContext());
        database.execSQL(sql);

        Toast.makeText(getApplicationContext(),"DB저장 완료",Toast.LENGTH_SHORT).show();
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
                    " where " +
                    "   _id " + "like '" + today_date.replace(" ","") + "'";

            DBHelper database = DBHelper.getInstance(getApplicationContext());
            database.execSQL(sql);

            Toast.makeText(getApplicationContext(),"DB저장 변경",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote() {

        if (item != null) {
            // delete note
            String sql = "delete from " + DBHelper.TABLE_NAME +
                    " where " +
                    "   _id = " + today_date.replace(" ","");

            DBHelper database = DBHelper.getInstance(getApplicationContext());
            database.execSQL(sql);

            Toast.makeText(getApplicationContext(),"DB삭제",Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(getApplicationContext(),"레코드 #" + i + " : " + diary.get_id(),Toast.LENGTH_SHORT).show();
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

        File photoFolder = new File(MainActivity.FOLDER_PHOTO);

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
    //endregion

    //region 외부 DB
    /**비트맵을 바이너리 바이트배열로 바꾸어주는 메서드 */
    public String bitmapToByteArray(Bitmap bitmap) {
        String image = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        image = "&image=" + byteArrayToBinaryString(byteArray);
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

    private void changeProfileImageToDB(String image) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //서버에 로그인 요청을 하면 결과값을 json타입으로 받음
                    JSONObject jsonObject = new JSONObject(response);
                    //서버통신 성공여부
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Toast.makeText(MainActivity.this, "이미지 저장에 성공하였습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "이미지 저장에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //vollyer를 이용해서 서버에 요청
        ImageRequest imageRequest = new ImageRequest(image,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(imageRequest);
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

