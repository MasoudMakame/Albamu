package com.example.rootkali.furnitureshow;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    EditText edtName,edtPrice;
    Button btnAdd,btnList;
    ImageView imageView;
    public static SQLiteHelper sqLiteHelper;
    final int REQUEST_CODE_GALLERY = 999;
    TextView textView;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Initialize();

    }
    //Set for buttons etc
    private void Initialize(){

        //Database creation
        sqLiteHelper = new SQLiteHelper(this, "PDB.sqlite", null, 1);
        //Table Creation
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS PRODUCT(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR, price VARCHAR,image BLOB )");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { ActivityCompat.requestPermissions(
                        MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY
                );
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sqLiteHelper.insertData(
                            edtName.getText().toString().trim(),
                            edtPrice.getText().toString().trim(),
                            imageViewToByte(imageView));

                    Toast.makeText(MainActivity.this, "Umefanikiwa Kuifadhi Bidhaa yako", Toast.LENGTH_LONG).show();
                    edtName.setText("");
                    edtPrice.setText("");
                    imageView.setImageResource(R.mipmap.ic_launcher);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProductList.class);
                startActivity(intent);
            }
        });

    }
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("TAHADHARI !");
        builder.setMessage("Ni kweli unataka kutoka kwenye Application ?");
        builder.setPositiveButton("Ndio", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("hapana", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE_GALLERY){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,REQUEST_CODE_GALLERY);
            }else {
                Toast.makeText(this, "You don't have permission to access File", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_GALLERY && resultCode==RESULT_OK && data!=null){
            Uri uri = data.getData();
            try {
                InputStream inputStream=getContentResolver().openInputStream(uri);
                Bitmap bitmap =BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        edtName = (EditText)findViewById(R.id.edtName);
        edtPrice = (EditText)findViewById(R.id.edtPrice);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnList = (Button) findViewById(R.id.btnList);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView= (TextView)findViewById(R.id.textView);
        scrollView = (ScrollView)findViewById(R.id.scrollview);

        edtName.addTextChangedListener(textWatcher);
        edtPrice.addTextChangedListener(textWatcher);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent =null, chooser= null;
        if(id==R.id.action_feedback){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_person_outline_black_24dp);
            builder.setTitle("Kuhusu Mtengenezaji");
            builder.setMessage("Application hii imesanifiwa na kutengenezwa na Juma Moshi\n(Masoud Jumah)\n\n" +
                    "Email:\njmoshi896@gmail.com or hubgit728@gmail.com\n\n" +
                    "Simu ya Kiganjani:\n+255 683749677 or +255718047962" +
                    "");
            builder.setPositiveButton("Funga", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        if(id==R.id.action_makubaliano){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_help_black_24dp);
            builder.setTitle("Makubaliano ya Faragha");
            builder.setMessage("Application hii imesanifiwa na kutengenezwa kwa lengo la kutunza picha ambazo " +
                    "mfanya biashara au mtu yeyote yule kwa lengo la kuweka kumbukumbu za picha zake za kazi anazo uza au " +
                    "ambazo ameshazifanya hapo nyuma.\nHivyo basi sikusayi wala kuchukua au kuchunguza taarifa zako za siri " +
                    "kama vile namba za simu , meseji zako unazotuma au kutumiwa, kuwa huru kabisa kutumia application.\n" +
                    "Mwenyezi Mungu Akubariki. ");
            builder.setPositiveButton("Nakubali", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Si Kubali", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
       // if(id==R.id.action_settings){
            //Intent intent1 = new Intent(Intent.ACTION_SEND);
            //intent1.setData(Uri.parse("https://drive.google.com/file/d/1OhyLSCmlJlnnm0e_MCHljlZdS2EVPKIA/view?usp=sharing"));
            //String [] to ={"jmoshi896@gail.com"};
            //intent1.putExtra(Intent.EXTRA_EMAIL,to);
            //intent1.putExtra(Intent.EXTRA_SUBJECT,"");
            //intent1.putExtra(Intent.EXTRA_TEXT,"");
            //intent1.setType("text/plain");
            //chooser = intent1.createChooser(intent1,"Send with");
            //startActivity(chooser);
            //return true;
        //}


        if(id==R.id.action_share){
            Intent intent1 = new Intent(Intent.ACTION_SEND);
            intent1.putExtra(Intent.EXTRA_TEXT,"http://www.mediafire.com/file/nj2r0lsmfuiw22b/Albamu.apk/file");
            intent1.setType("text/plain");
            chooser = intent1.createChooser(intent1,"Tuma Kwa...");
            startActivity(chooser);
            return true;
        }
        if (id == R.id.action_aboutApp) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_info_outline_black_24dp);
            builder.setTitle("Kuhusu Album Show 1.0.0");
            builder.setMessage("Application hii nimesanifu na kutengeneza kama album application ambayo " +
                    "itakusaidia kutunza picha na kumuonesha mteja kwa wakati, hivyo app hii itakusaidia " +
                    "kupunguza maswali mengi kutoka kwa mteja, pia itakusaidia kupuguza kupatana bei ya thamani ya bidhaa " +
                    "kwa mteja kwa sababu itamuonesha mteja jina la bidhaa na thamani yake, pia itakuwezesha kubadili picha, " +
                    "thamani ya bidhaa au hata jina la bidhaa kama ukikosea, pia itakuwezesha kufuta picha kama " +
                    "ulipiga vibaya na kuhifadhi humu.natumaini utaifurahia hii application .\nMwenyezi Mungu  akubariki sana.");
            builder.setPositiveButton("Funga", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Enable and disable input if is empty
    private  TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Name = edtName.getText().toString().trim();
            String Price = edtName.getText().toString().trim();
            btnAdd.setEnabled(!Name.isEmpty() && !Price.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
