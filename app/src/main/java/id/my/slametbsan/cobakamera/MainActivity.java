package id.my.slametbsan.cobakamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 101;
    static final int REQUEST_IMAGE_CAPTURE = 11;
    private Button btnFoto;
    private ImageView ivFoto;
    Uri uriFoto;
    String lokasiFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivFoto = findViewById(R.id.ivFoto);
        btnFoto = findViewById(R.id.btnFoto);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permissionCheck())
                    intentAmbilFoto();
            }
        });
    }

    private boolean permissionCheck() {
        boolean b = false;

        //cek ijin akses CAMERA dan WRITE_EXTERNAL_STORAGE
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            //ijin tidak diberikan, maka minta ijin
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{ Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);

            //disable tombol supaya tidak menyebabkan forced-close
            btnFoto.setEnabled(false);
        } else {
            //ijin sudah diberikan, aktifkan tombol
            btnFoto.setEnabled(true);
            b = true;
        }

        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            ivFoto.setImageURI(uriFoto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                //ijin diberikan, aktifkan tombol
                btnFoto.setEnabled(true);
            }
        }
    }

    private void intentAmbilFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            //buat file untuk menyimpan foto
            File fileFoto = null;
            try {
                fileFoto = buatFileFoto();
            } catch (IOException e){
                e.printStackTrace();
            }

            //lanjutkan jika file berhasil dibuat
            if(fileFoto != null){
                uriFoto = FileProvider.getUriForFile(this,
                        "id.my.slametbsan.cobakamera", //samakan dengan manifest
                        fileFoto);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File buatFileFoto() throws IOException {
        //buat nama file foto
        String timeStamp    = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String namaFile     = "SBS_" + timeStamp + "_";
        String extFile      = ".jpg";
        File lokasiDirektori = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File foto = File.createTempFile(namaFile, extFile, lokasiDirektori);
        lokasiFoto = foto.getAbsolutePath();
        return foto;
    }
}
