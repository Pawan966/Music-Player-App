package com.example.isangeet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    String [] songs;
    ArrayList<File> musics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.list);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                         musics=fetchSongs(Environment.getExternalStorageDirectory());
                         songs=new String [musics.size()];

                         for(int i=0;i<musics.size();i++)
                         {
                             songs[i]=musics.get(i).getName().replace(".mp3","");
                         }

                         arrayAdapter=new ArrayAdapter<>(getApplicationContext(), R.layout.list_item,R.id.textView,songs);
                         listView.setAdapter(arrayAdapter);

                         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                             @Override
                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                 Intent intent=new Intent(getApplicationContext(),PlaySong.class);
                                 String currentSong=listView.getItemAtPosition(position).toString();
                                 intent.putExtra("songList",musics);
                                 intent.putExtra("currentSong",currentSong);
                                 intent.putExtra("position",position);
                                 startActivity(intent);
                             }
                         });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private ArrayList<File> fetchSongs(File file)
    {
        ArrayList<File> arrayList= new ArrayList<>();
        File [] songs = file.listFiles();

            for(File myFile : songs)
            {
                if(!myFile.isHidden() && myFile.isDirectory())
                {
                    arrayList.addAll(fetchSongs(myFile));
                }
                else
                {
                    if(myFile.getName().endsWith(".mp3") || myFile.getName().endsWith(".mp4a") || myFile.getName().endsWith(".wav"))
                    {
                        arrayList.add(myFile);
                    }
                }
            }

        return arrayList;
    }
}