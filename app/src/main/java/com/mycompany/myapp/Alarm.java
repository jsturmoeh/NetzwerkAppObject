package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import java.net.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import java.io.*;
import android.content.DialogInterface;
import android.content.Intent;

public class Alarm
{
	public Alarm(MainActivity GUI,String m)
	{
		AlertDialog ad = new AlertDialog.Builder(GUI).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage(m);
		ad.setButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();                    
				}
			});
		ad.show();
	}
}
