package com.dopage.dolnad.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dopage.dolnad.Interfaces.ApiService;
import com.dopage.dolnad.Models.Cryptography;
import com.dopage.dolnad.Models.GenericResponse;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.DialogHelper;
import com.dopage.dolnad.Utils.RetrofitClient;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateCode extends AppCompatActivity {

    private ImageView QRCodeImage;
    private Button downloadButton;

    private AlertDialog progressDialog;
    public final static int QRCodeWidth = 500;

    private Bitmap bitmap;
    private TextView encryptedTextView;
    private String encryptedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        QRCodeImage = findViewById(R.id.QRCodeImageView);
        downloadButton = findViewById(R.id.downloadButton);
        encryptedTextView = findViewById(R.id.textEncrypted);

        buildProgressDialog();

        if(getIntent().hasExtra("userString")){
            new QRCodeTask().execute(getIntent().getStringExtra("userString"));

            String userString = getIntent().getStringExtra("userString");
            Cryptography cryptography = new Cryptography("MacDonald");


            try {
                encryptedString = cryptography.encrypt(userString, "MacDonald");
                encryptedTextView.setText(encryptedString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        downloadButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "dolnad", null);
//                        Toast.makeText(GenerateCode.this, "Saved to gallery", Toast.LENGTH_SHORT).show();

                        if(!progressDialog.isShowing())
                            progressDialog.show();
//
//                        ByteArrayOutputStream blob = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100/* Ignored for PNGs */, blob);
//
//                        RequestBody propertyImage = RequestBody.create(blob.toByteArray(), MediaType.parse("image/*"));
//
//                        final MultipartBody.Part qrImage = MultipartBody.Part.createFormData("qr",
//                                "qr",
//                                propertyImage);
//
                        User user = new Gson().fromJson(getIntent().getStringExtra("userString"), User.class);
                        ApiService apiService = RetrofitClient.createService(ApiService.class);
                        Call<GenericResponse> call = apiService.updateQR(user.getId(), encryptedString);
//
                        call.enqueue(new Callback<GenericResponse>() {
                            @Override
                            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();

                                if(response.isSuccessful()){
                                    onSuccessfullyUploadingQR(response.body());
                                }else{
                                    try {
                                        String errorMessage = response.errorBody().string();
                                        Toast.makeText(GenerateCode.this,
                                                errorMessage, Toast.LENGTH_SHORT).show();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<GenericResponse> call, Throwable t) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();

                                Toast.makeText(GenerateCode.this,
                                        t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
        );
    }

    private void onSuccessfullyUploadingQR(GenericResponse genericResponse){
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }


    private void buildProgressDialog(){
        progressDialog = DialogHelper.buildProgressDialog(
                GenerateCode.this,
                getString(R.string.please_wait)
        );
    }

    public class QRCodeTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                bitmap = textToImageEncode(strings[0]);
                return bitmap;
            } catch (WriterException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            if(bitmap != null){
                QRCodeImage.setImageBitmap(bitmap);
                downloadButton.setVisibility(View.VISIBLE);
            }

        }
    }

    private Bitmap textToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;

        try{
            bitMatrix = new MultiFormatWriter().encode(value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QRCodeWidth, QRCodeWidth, null);
        }catch (IllegalArgumentException e){
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for(int y = 0; y < bitMatrixHeight; y++){
            int offset = y * bitMatrixWidth;
            for(int x = 0; x < bitMatrixWidth; x++){
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);

        return bitmap;

    }
}
