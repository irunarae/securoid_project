package com.example.info_security;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Encryption extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encryption);
		
		Bitmap bitmap1 = null;
		ImageView img = (ImageView)findViewById(R.id.encryption_img);
		Button ok = (Button)findViewById(R.id.encryption_ok);
		Button cancel =((Button)findViewById(R.id.encryption_cancel));

		String uri = getIntent().getStringExtra("URI");
		final String real_Uri=getRealImagePath(Uri.parse(uri));
		
		try {
			bitmap1 = Images.Media.getBitmap(getContentResolver(),Uri.parse(uri));
			img.setImageBitmap(bitmap1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Main.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

		ok.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//Key, 버퍼 선언부
				int pdwRoundKey[] = new int[32];
				byte pbUserKey[] = {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
				(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
				(byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B,
				(byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F};
				byte pbCipher[] = new byte[16];
				byte[] buf = new byte[16];
				String encrypted = new String(real_Uri);
				
				seedx.SeedRoundKey(pdwRoundKey, pbUserKey);		
				
				FileInputStream in;
				FileInputStream mask_In;
				FileOutputStream out;
				
				//Encryption
				try {
					in = new FileInputStream(real_Uri);
					out = new FileOutputStream(encrypted);
					
					
					//need to CQroid.bmp loading
					Bitmap bmp= BitmapFactory.decodeResource(getResources(),R.drawable.securoid);

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();
										
					if(stream.size()!=0)
						out.write(byteArray);
					else{
						Toast.makeText(getApplicationContext(), "stream error", Toast.LENGTH_SHORT).show();
						finish();
					}
					stream.close();
					
					if((in.read(buf))!=-1){
						Log.e(in.read(buf)+"",buf.length+"/"+buf);
						seedx.SeedEncrypt(buf,pdwRoundKey,pbCipher);
						out.write(pbCipher);
					}

					in.close();
					out.close();
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Toast.makeText(getApplicationContext(), "사진이 암호화되었습니다.", Toast.LENGTH_SHORT).show();
				onBackPressed();
			}
		}); 
	}
	
	protected String getRealImagePath(Uri uriPath) {
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor c = managedQuery(uriPath, proj, null, null, null);
		int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		c.moveToFirst();
		String fullPath = null;
		try {
		fullPath = c.getString(index);  // 파일의 실제 경로 
		} catch(Exception e) {
		e.printStackTrace();
		return null;
		}
		startManagingCursor(c);
		return fullPath;
	}
}
