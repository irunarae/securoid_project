package com.example.info_security;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button choose_Photo = (Button)findViewById(R.id.main_choose);
        choose_Photo.setOnClickListener(dialog_Listener);        
    }
    
    
    OnClickListener dialog_Listener = new OnClickListener(){
		public void onClick(View v) {
			Intent intent1 = new Intent(Intent.ACTION_PICK);
	    	intent1.setDataAndType(
	    			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	    			MediaStore.Images.Media.CONTENT_TYPE);
			startActivityForResult(intent1, 0);
		}
    };
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0: // 사진 폴더에서 선택
				Uri uri = data.getData();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Encryption.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.putExtra("URI", uri.toString());
				
				startActivity(intent);
			}
		}
		else if(resultCode==RESULT_CANCELED){
		}
    }
	
		
}
