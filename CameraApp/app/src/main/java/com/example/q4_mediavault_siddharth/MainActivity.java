package com.example.q4_mediavault_siddharth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_CODE_PICK_FOLDER = 102;
    private static final int REQUEST_CODE_PICK_FOLDER_FOR_SAVE = 103;

    private Uri currentFolderUri;
    private String currentPhotoPath;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private final List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewGallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(this, imageUris);
        recyclerView.setAdapter(adapter);

        // Part A: Take Photo -> Choose Folder -> Camera
        findViewById(R.id.fabCamera).setOnClickListener(v -> {
            openFolderPickerForSaving();
        });

        // Part B: View Folder
        findViewById(R.id.titleText).setOnClickListener(v -> openFolderPicker());
    }

    private void openFolderPickerForSaving() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_FOLDER_FOR_SAVE);
    }

    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_FOLDER);
    }

    private void loadImagesFromFolder(Uri folderUri) {
        imageUris.clear();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, folderUri);
        if (pickedDir != null) {
            for (DocumentFile file : pickedDir.listFiles()) {
                if (file.getType() != null && file.getType().startsWith("image/")) {
                    imageUris.add(file.getUri());
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void checkPermissionAndOpenFile() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File vaultDir = new File(storageDir, "UserChoice");
        if (!vaultDir.exists()) vaultDir.mkdirs();

        File image = File.createTempFile("IMG_" + timeStamp + "_", ".jpg", vaultDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void saveImageToChosenFolder(File sourceFile, Uri destFolderUri) {
        try {
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, destFolderUri);
            DocumentFile newFile = pickedDir.createFile("image/jpeg", sourceFile.getName());

            if (newFile != null) {
                InputStream in = new FileInputStream(sourceFile);
                OutputStream out = getContentResolver().openOutputStream(newFile.getUri());

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                sourceFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_FOLDER_FOR_SAVE && data != null) {
                currentFolderUri = data.getData();
                // Grant permanent access to this folder
                getContentResolver().takePersistableUriPermission(currentFolderUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                checkPermissionAndOpenFile();

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // PHOTO TAKEN! Now copy it to the chosen folder URI
                if (currentFolderUri != null && currentPhotoPath != null) {
                    saveImageToChosenFolder(new File(currentPhotoPath), currentFolderUri);
                    Toast.makeText(this, "Photo Saved to Chosen Folder!", Toast.LENGTH_SHORT).show();
                    loadImagesFromFolder(currentFolderUri); // Refresh grid
                }

            } else if (requestCode == REQUEST_CODE_PICK_FOLDER && data != null) {
                currentFolderUri = data.getData();
                loadImagesFromFolder(currentFolderUri);
            }
        }
    }
}