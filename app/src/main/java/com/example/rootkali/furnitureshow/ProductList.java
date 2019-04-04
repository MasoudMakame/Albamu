package com.example.rootkali.furnitureshow;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ProductList extends AppCompatActivity{
    ListView listView;
    ArrayList<Product> list;
    ProductListAdapter adapter = null;

    CharSequence [] fonts  ={"8sp","12","16","18","20","22","24","26","28","30"};
    TextView txtName,txtPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_activity);
        listView = (ListView) findViewById(R.id.grindView);
        list = new ArrayList<>();
        adapter = new ProductListAdapter(this, R.layout.product_items, list);
        listView.setAdapter(adapter);

        //Query data from database
        final Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM PRODUCT");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            list.add(new Product(name, price, image, id));
        }
        adapter.notifyDataSetChanged();
        if(list.size()==0){
            Toast.makeText(this, "Hakuna taarifa za bidhaa", Toast.LENGTH_LONG).show();
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                CharSequence[] items = {"Badili Bidhaa yako", "Futa Bidhaa yako"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductList.this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Chagua Kitendo");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM PRODUCT");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            //dialog update
                            showDialogUpdate(ProductList.this, arrID.get(i));
                        } else {
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM PRODUCT");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(i));
                        }

                    }
                });
                builder.show();
                return true;
            }
        });
    }
    ImageView imageViewProduct;
    private void showDialogUpdate(Activity activity, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_product_activity);
        dialog.setTitle("Update");


        imageViewProduct = (ImageView) dialog.findViewById(R.id.imageViewUpdate);
        final EditText edtName = (EditText) dialog.findViewById(R.id.edtName);
        final EditText edtPrice = (EditText) dialog.findViewById(R.id.edtPrice);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        //get data of row clicked from database
        final Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM PRODUCT WHERE id="+position);
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            edtName.setText(name);// set name clicked
            String price = cursor.getString(2);
            edtPrice.setText(price);// set price clicked
            byte[] image = cursor.getBlob(3);
            // set image
            imageViewProduct.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));

            //add to list
            list.add(new Product(name, price, image, id));
        }

        //width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        //height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        ProductList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 999
                );
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    MainActivity.sqLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            edtPrice.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewProduct),position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Umefanikiwa Kubadili Bidhaa yako", Toast.LENGTH_LONG).show();
                }catch (Exception error){
                    Log.d("Hajafanikiwa Kubadili",error.getMessage());
                }
                updateProductList();
            }
        });

    }
    //Delete Product
    private void showDialogDelete(final int idProduct){
        AlertDialog.Builder dialaogDelete = new AlertDialog.Builder(ProductList.this);
        dialaogDelete.setTitle("TAHADHARI !");
        dialaogDelete.setIcon(R.mipmap.ic_launcher);
        dialaogDelete.setMessage("Ni kweli unataka kufuta Bidhaa yako ?");
        dialaogDelete.setPositiveButton("Ndio", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                try{
                    MainActivity.sqLiteHelper.deleteData(idProduct);
                    Toast.makeText(getApplicationContext(), "Umefanikiwa Kufuta Bidhaa yako", Toast.LENGTH_LONG).show();
                }catch (Exception error){
                    Log.e("Makosa",error.getMessage());
                }
                updateProductList();
            }
        });
        dialaogDelete.setNegativeButton("Hapana", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialaogDelete.show();
    }
    private void updateProductList(){

        //Query data from database
        final Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM PRODUCT");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            list.add(new Product(name, price, image, id));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 999) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 999);
            } else {
                Toast.makeText(this, "Hauna Mamlaka ya kuangalia faili hili", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewProduct.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //Search Product
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_main, menu);
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
            intent1.putExtra(Intent.EXTRA_TEXT,"https://www.mediafire.com/file/ar7fijp9ghis962/Furniture_Show.apk/file");
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
}