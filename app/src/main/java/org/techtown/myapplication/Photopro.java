package org.techtown.myapplication;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Photopro extends BaseActivity implements View.OnClickListener {

    // LOG

    private String TAGLOG = "MainActivityLoG";
    // 이미지넣는 뷰와 업로드하기위환 버튼
    private ImageView ivUploadImage,ivUploadImage2,ivUploadImage3,ivUploadImage4,ivUploadImage5;
    private Button btnUploadImage,btnUploadImage2,btnUploadImage3,btnUploadImage4,btnUploadImage5;

    // 서버로 업로드할 파일관련 변수
    public String uploadFilePath;
    public String uploadFileName;
    private int REQ_CODE_PICK_PICTURE = 1;

    String userID;
    String file_path;
    private Button btn_rgend;
    // 파일을 업로드 하기 위한 변수 선언
    private int serverResponseCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_at);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");


        // 변수 초기화

        InitVariable();
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        btn_rgend = findViewById(R.id.btn_rgend);
        btn_rgend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Photopro.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }


    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };





    // 초기화

    private void InitVariable() {

        // 이미지를 넣을 뷰

        ivUploadImage = (ImageView) findViewById(R.id.iv_upload_image);

        ivUploadImage.setOnClickListener(this);

        btnUploadImage = (Button) findViewById(R.id.btn_upload_image);

        btnUploadImage.setOnClickListener(this);


        ivUploadImage2 = (ImageView) findViewById(R.id.iv_upload_image2);

        ivUploadImage2.setOnClickListener(this);

        btnUploadImage2 = (Button) findViewById(R.id.btn_upload_image2);

        btnUploadImage2.setOnClickListener(this);


        ivUploadImage3 = (ImageView) findViewById(R.id.iv_upload_image3);

        ivUploadImage3.setOnClickListener(this);

        btnUploadImage3 = (Button) findViewById(R.id.btn_upload_image3);

        btnUploadImage3.setOnClickListener(this);


        ivUploadImage4 = (ImageView) findViewById(R.id.iv_upload_image4);

        ivUploadImage4.setOnClickListener(this);

        btnUploadImage4 = (Button) findViewById(R.id.btn_upload_image4);

        btnUploadImage4.setOnClickListener(this);


        ivUploadImage5 = (ImageView) findViewById(R.id.iv_upload_image5);

        ivUploadImage5.setOnClickListener(this);

        btnUploadImage5 = (Button) findViewById(R.id.btn_upload_image5);

        btnUploadImage5.setOnClickListener(this);

    }



    // ==========================================================================================

    // ==================================== 사진을 불러오는 소스코드 ============================

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String a = saving(requestCode, resultCode, data);
                Bitmap bit = BitmapFactory.decodeFile(a);
                ivUploadImage.setImageBitmap(bit);
            }

        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                String a = saving(requestCode, resultCode, data);
                Bitmap bit = BitmapFactory.decodeFile(a);
                ivUploadImage2.setImageBitmap(bit);
            }

        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                String a = saving(requestCode, resultCode, data);
                Bitmap bit = BitmapFactory.decodeFile(a);
                ivUploadImage3.setImageBitmap(bit);
            }

        } else if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
                String a = saving(requestCode, resultCode, data);
                ;
                Bitmap bit = BitmapFactory.decodeFile(a);
                ivUploadImage4.setImageBitmap(bit);
            }

        } else if (requestCode == 5) {
            if (resultCode == Activity.RESULT_OK) {
                String a = saving(requestCode, resultCode, data);
                Bitmap bit = BitmapFactory.decodeFile(a);
                ivUploadImage5.setImageBitmap(bit);
            }

        }


    }

    private String saving(int requestCode, int resultCode, Intent data)
    {
        Uri uri = data.getData();
        String path = getPath(uri);
        file_path=getUriId(uri);
        String name = getName(uri);
        uploadFilePath = path;
        uploadFileName = name;
        Log.i(TAGLOG, "[onActivityResult] uploadFilePath:" + uploadFilePath + ", uploadFileName:" + uploadFileName);

        return path;
    }

    // 실제 경로 찾기

    private String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }



    // 파일명 찾기

    private String getName(Uri uri) {

        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor

                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }



    // uri 아이디 찾기

    private String getUriId(Uri uri) {

        String[] projection = {MediaStore.Images.ImageColumns._ID};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

    // ==========================================================================================





    // ==========================================================================================

    // ============================== 사진을 서버에 전송하기 위한 스레드 ========================



    private class UploadImageToServer extends AsyncTask<String, String, String> {

        ProgressDialog mProgressDialog;

        String fileName = uploadFilePath;

        HttpURLConnection conn = null;

        DataOutputStream dos = null;

        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1 * 10240 * 10240;

        File sourceFile = new File(uploadFilePath);



        @Override

        protected void onPreExecute() {

            // Create a progressdialog

            mProgressDialog = new ProgressDialog(Photopro.this);

            mProgressDialog.setTitle("Loading...");

            mProgressDialog.setMessage("Image uploading...");

            mProgressDialog.setCanceledOnTouchOutside(false);

            mProgressDialog.setIndeterminate(false);

            mProgressDialog.show();

        }



        @Override

        protected String doInBackground(String... serverUrl) {

            if (!sourceFile.isFile()) {

                runOnUiThread(new Runnable() {

                    public void run() {

                        Log.i(TAGLOG, "[UploadImageToServer] Source File not exist :" + uploadFilePath);

                    }

                });

                return null;

            } else {

                try {

                    // open a URL connection to the Servlet

                    FileInputStream fileInputStream = new FileInputStream(sourceFile);

                    URL url = new URL(serverUrl[0]);



                    // Open a HTTP  connection to  the URL

                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true); // Allow Inputs

                    conn.setDoOutput(true); // Allow Outputs

                    conn.setUseCaches(false); // Don't use a Cached Copy

                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Connection", "Keep-Alive");

                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    conn.setRequestProperty("uploaded_file", fileName);

                    Log.i(TAGLOG, "fileName: " + fileName);



                    dos = new DataOutputStream(conn.getOutputStream());



                    // 사용자 이름으로 폴더를 생성하기 위해 사용자 이름을 서버로 전송한다.

                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"data1\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    dos.writeBytes(userID);

                    dos.writeBytes(lineEnd);



                    // 이미지 전송

                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);



                    // create a buffer of  maximum size

                    bytesAvailable = fileInputStream.available();



                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    buffer = new byte[bufferSize];



                    // read file and write it into form...

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);

                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);

                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }



                    // send multipart form data necesssary after file data...

                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



                    // Responses from the server (code and message)

                    serverResponseCode = conn.getResponseCode();

                    String serverResponseMessage = conn.getResponseMessage();



                    Log.i(TAGLOG, "[UploadImageToServer] HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);



                    if (serverResponseCode == 200) {

                        runOnUiThread(new Runnable() {

                            public void run() {

                                Toast.makeText(Photopro.this, "File Upload Completed", Toast.LENGTH_SHORT).show();

                            }

                        });

                    }

                    //close the streams //

                    fileInputStream.close();

                    dos.flush();

                    dos.close();



                } catch (MalformedURLException ex) {

                    ex.printStackTrace();

                    runOnUiThread(new Runnable() {

                        public void run() {

                            Toast.makeText(Photopro.this, "MalformedURLException", Toast.LENGTH_SHORT).show();

                        }

                    });

                    Log.i(TAGLOG, "[UploadImageToServer] error: " + ex.getMessage(), ex);

                } catch (Exception e) {

                    e.printStackTrace();

                    runOnUiThread(new Runnable() {

                        public void run() {

                            Toast.makeText(Photopro.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();

                        }

                    });

                    Log.i(TAGLOG, "[UploadImageToServer] Upload file to server Exception Exception : " + e.getMessage(), e);

                }

                Log.i(TAGLOG, "[UploadImageToServer] Finish");

                return null;

            } // End else block

        }



        @Override

        protected void onPostExecute(String s) {

            mProgressDialog.dismiss();

        }

    }

    // ==========================================================================================

    @Override

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_upload_image:

                Intent i = new Intent(Intent.ACTION_PICK);

                i.setType(MediaStore.Images.Media.CONTENT_TYPE);

                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.


                // 결과를 리턴하는 Activity 호출

                startActivityForResult(i, 1);

                break;

            case R.id.btn_upload_image:

                if (uploadFilePath != null) {

                    ivUploadImage.setEnabled(false); //아이디값 고
                    btnUploadImage.setBackgroundColor(getResources().getColor(R.color.white));
                    btnUploadImage.setEnabled(false);
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    uploadimagetoserver.execute("http://ydoag2003.dothome.co.kr/ImageUploadToServer.php");
                    Toast.makeText(Photopro.this, ""+uploadFileName, Toast.LENGTH_SHORT).show(); //ㅍ
                    going();
                    startProgress();
                }
                    else {

                    Toast.makeText(Photopro.this, "You didn't insert any image", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.iv_upload_image2:

                Intent i2 = new Intent(Intent.ACTION_PICK);

                i2.setType(MediaStore.Images.Media.CONTENT_TYPE);

                i2.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.

                i2.putExtra("key", 2);


                // 결과를 리턴하는 Activity 호출

                startActivityForResult(i2, 2);

                break;

            case R.id.btn_upload_image2:

                if (uploadFilePath != null) {

                    ivUploadImage2.setEnabled(false); //아이디값 고
                    btnUploadImage2.setBackgroundColor(getResources().getColor(R.color.white));
                    btnUploadImage2.setEnabled(false);
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    uploadimagetoserver.execute("http://ydoag2003.dothome.co.kr/ImageUploadToServer.php");
                    Toast.makeText(Photopro.this, ""+uploadFileName, Toast.LENGTH_SHORT).show(); //ㅍ
                    going();
                    startProgress();
                }
                else {

                    Toast.makeText(Photopro.this, "You didn't insert any image", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.iv_upload_image3:

                Intent i3 = new Intent(Intent.ACTION_PICK);

                i3.setType(MediaStore.Images.Media.CONTENT_TYPE);

                i3.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.


                // 결과를 리턴하는 Activity 호출

                startActivityForResult(i3, 3);

                break;

            case R.id.btn_upload_image3:

                if (uploadFilePath != null) {

                    ivUploadImage3.setEnabled(false); //아이디값 고
                    btnUploadImage3.setBackgroundColor(getResources().getColor(R.color.white));
                    btnUploadImage3.setEnabled(false);
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    uploadimagetoserver.execute("http://ydoag2003.dothome.co.kr/ImageUploadToServer.php");
                    Toast.makeText(Photopro.this, ""+uploadFileName, Toast.LENGTH_SHORT).show(); //ㅍ
                    going();
                    startProgress();
                }
                else {

                    Toast.makeText(Photopro.this, "You didn't insert any image", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.iv_upload_image4:

                Intent i4 = new Intent(Intent.ACTION_PICK);

                i4.setType(MediaStore.Images.Media.CONTENT_TYPE);

                i4.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                i4.putExtra("key", 4);

                // 결과를 리턴하는 Activity 호출

                startActivityForResult(i4, 4);

                break;

            case R.id.btn_upload_image4:

                if (uploadFilePath != null) {
                    ivUploadImage4.setEnabled(false); //아이디값 고
                    btnUploadImage4.setBackgroundColor(getResources().getColor(R.color.white));
                    btnUploadImage4.setEnabled(false);
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    uploadimagetoserver.execute("http://ydoag2003.dothome.co.kr/ImageUploadToServer.php");
                    Toast.makeText(Photopro.this, ""+uploadFileName, Toast.LENGTH_SHORT).show(); //ㅍ
                    going();
                    startProgress();
                }
                else {

                    Toast.makeText(Photopro.this, "You didn't insert any image", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.iv_upload_image5:

                Intent i5 = new Intent(Intent.ACTION_PICK);

                i5.setType(MediaStore.Images.Media.CONTENT_TYPE);

                i5.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                i5.putExtra("key", 5);

                // 결과를 리턴하는 Activity 호출

                startActivityForResult(i5, 5);

                break;

            case R.id.btn_upload_image5:

                if (uploadFilePath != null) {

                    ivUploadImage5.setEnabled(false); //아이디값 고
                    btnUploadImage5.setBackgroundColor(getResources().getColor(R.color.white));
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    btnUploadImage5.setEnabled(false);
                    uploadimagetoserver.execute("http://ydoag2003.dothome.co.kr/ImageUploadToServer.php");
                    Toast.makeText(Photopro.this, ""+uploadFileName, Toast.LENGTH_SHORT).show(); //ㅍ
                    going();
                    startProgress();
                }
                else {

                    Toast.makeText(Photopro.this, "You didn't insert any image", Toast.LENGTH_SHORT).show();

                }

                break;

        }

    }
    private void going()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Profile_Img_Check profile_img_check=new Profile_Img_Check(userID,uploadFileName,responseListener);
        RequestQueue queue = Volley.newRequestQueue(Photopro.this);
        queue.add(profile_img_check);


    }
    private void startProgress() {

        progressON("사진업로드중..");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3000);
    }


}


//https://lcw126.tistory.com/99 <-이거 불러오기 하고싶으면 가면댐 URL 불러오기임 db에 저장됭어있는 url주소

/* 이건 BLOB저장 임
    package org.techtown.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Photopro extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    ImageView imageView;
    String imageFileName;
    String userID;
    String image;
    private MediaScanner mMediaScanner; // 사진 저장 시 갤러리 폴더에 바로 반영사항을 업데이트 시켜주려면 이 것이 필요하다(미디어 스캐닝)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);


        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        // 사진 저장 후 미디어 스캐닝을 돌려줘야 갤러리에 반영됨.
        mMediaScanner = MediaScanner.getInstance(getApplicationContext());
        imageView = findViewById(R.id.iv_photo);

        // 권한 체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();


        findViewById(R.id.btn_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {

                    }

                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                    }
                }
            }
        });
        findViewById(R.id.btn_regi_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Photopro.this, MainActivity.class);
                startActivity(intent);

            }

        });
    }

    private File createImageFile() throws IOException {

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));


                bitmap=resize(bitmap);
                        image=bitmapToByteArray(bitmap);
                        changeProfileImageToDB(image);


                        imageView.setImageBitmap(bitmap);
                        } catch (Exception e) {
                        e.printStackTrace();
                        }

                        }

                        }
public String bitmapToByteArray(Bitmap bitmap)
        {
        String image1 = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray=stream.toByteArray();
        image1 ="&image="+byteArrayToBinaryString(byteArray);
        return image1;
        }

public static String byteArrayToBinaryString(byte[] b)
        {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<b.length; i++)
        {
        sb.append(byteToBinaryString(b[i]));

        }
        return sb.toString();

        }
public static String byteToBinaryString(byte n)
        {
        StringBuilder sb=new StringBuilder("00000000");
        for(int bit=0;bit<8;bit++)
        {
        if(((n>>bit)&1)>0)
        {
        sb.setCharAt(7-bit,'1');
        }
        }

        return sb.toString();
        }

private Bitmap resize(Bitmap bm){

        Configuration config=getResources().getConfiguration();
        if (config.smallestScreenWidthDp>=800)
        bm = Bitmap.createScaledBitmap(bm, 400, 240, true);//이미지 크기로 인해 용량초과
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
private void changeProfileImageToDB(String image)
        {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
@Override
public void onResponse(String response) {
        try {
        JSONObject jsonObject = new JSONObject(response);
        boolean success = jsonObject.getBoolean("success");
        Toast.makeText(getApplicationContext(), "댐",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
        e.printStackTrace();
        } catch (Exception e) {
        e.printStackTrace();
        }
        }
        };
        Profile_Img_Check profile_img_check=new Profile_Img_Check(userID,image,responseListener);
        RequestQueue queue = Volley.newRequestQueue(Photopro.this);
        queue.add(profile_img_check);

        Log.d("비트맵",String.valueOf(photoUri));
        }



        PermissionListener permissionListener = new PermissionListener() {
@Override
public void onPermissionGranted() {
        Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();
        }

@Override
public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
        };








        }

 */



/* 간결하지만 파일저장이안되고 권한요청이안됨됨
* mageView imageView;

    File file;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);

        imageView = findViewById(R.id.iv_photo);

        Button button = findViewById(R.id.btn_photo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

    }

    public void takePicture() {
        try {
            file = createFile();
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, 101);
    }

    private File createFile() {
        String filename = "capture.jpg";
        File outFile = new File(getFilesDir(), filename);
        Log.d("Main", "File path : " + outFile.getAbsolutePath());

        return outFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/
