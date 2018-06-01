package vlimv.taxi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvalidRegActivity extends AppCompatActivity implements ServerRequest.NextActivity{
    private static final int PICK_FROM_GALLERY = 1;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    String mCurrentPhotoPath;
    ImageView image;
    DilatingDotsProgressBar progressBar;

    // Server request related
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;

    //Buttons
    Button next_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid_reg);

        progressBar = findViewById(R.id.progress);

        next_btn = findViewById(R.id.button);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multipartBody.length < 0) {
                    Toast.makeText(view.getContext(), "Пожалуйста, загрузите фотографию", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    next_btn.setVisibility(View.GONE);
                    progressBar.showNow();
                    ServerRequest.getInstance(view.getContext()).uploadCert(view.getContext(), multipartBody, SharedPref.loadToken(view.getContext()), mimeType);
                }
            }
        });

        ImageButton back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton btn_load = findViewById(R.id.button_load);
        ImageButton btn_camera = findViewById(R.id.button_camera);
        image = findViewById(R.id.image);
        btn_load.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                try {
                    if (ActivityCompat.checkSelfPermission(InvalidRegActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(InvalidRegActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(ActivityCompat.checkSelfPermission(
                            InvalidRegActivity.this, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED){
                        dispatchTakePictureIntent();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            Toast.makeText(getBaseContext(),
                                    "Разрешите приложению доступ к камере, чтобы сделать изображение.", Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri targetUri = data.getData();
                    Bitmap bitmap;
                    next_btn.setEnabled(true);
                    next_btn.setBackground(getDrawable(R.drawable.ripple_effect_square));

                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                        image.setImageBitmap(bitmap);
                        byte[] imageFile = getByteArrayImage(bitmap);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(bos);
                        try {
                            // generate filename
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "JPEG_" + timeStamp + ".jpg";
                            Log.d("Preparing image", imageFileName);
                            // the first file
                            buildPart(dos, imageFile, imageFileName);
                            // the second file
                            // buildPart(dos, fileData2, "ic_action_book.png");
                            // send multipart form data necesssary after file data
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                            // pass to multipart body
                            multipartBody = bos.toByteArray();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bitmap;
                    next_btn.setEnabled(true);
                    next_btn.setBackground(getDrawable(R.drawable.ripple_effect_square));
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        image.setImageBitmap(bitmap);
                        byte[] imageFile = getByteArrayImage(bitmap);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(bos);
                        try {
                            // generate filename
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "JPEG_" + timeStamp + ".jpg";
                            // the first file
                            buildPart(dos, imageFile, imageFileName);
                            // the second file
                            // buildPart(dos, fileData2, "ic_action_book.png");
                            // send multipart form data necesssary after file data
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                            // pass to multipart body
                            multipartBody = bos.toByteArray();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + "\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"certificate\"; filename=\""
                + fileName + "\"" + "\r\n");
        dataOutputStream.writeBytes("Content-Type: image/jpeg\r\n");
        dataOutputStream.writeBytes("\r\n");

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes("\r\n");
    }
    public byte[] getByteArrayImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }
    // Convert bitmap to string to be sent to sever
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getBaseContext(), "Произошла ошибка. Попробуйте еще раз.", Toast.LENGTH_LONG)
                        .show();
            }
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.vlimv.taxi.fileprovider",
//                        photoFile);
//                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    Toast.makeText(getBaseContext(),
                            "Разрешите приложению доступ к памяти, чтобы загрузить изображение.",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(getBaseContext(),
                            "Разрешите приложению доступ к камере, чтобы сделать изображение.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void goNext() {
        Intent intent = new Intent(getApplicationContext(), PassengerMainActivity.class);
        startActivity(intent);
    }

    @Override
    public void tryAgain() {
        progressBar.hideNow();
        next_btn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Произошла ошибка, попробуйте еще раз", Toast.LENGTH_SHORT).show();
    }
}
