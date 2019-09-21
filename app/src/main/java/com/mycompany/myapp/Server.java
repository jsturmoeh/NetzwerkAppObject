package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import java.net.*;
import java.net.SocketException;

import java.net.ServerSocket;            
import java.net.Socket;         
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
import java.io.OutputStream;
import java.io.DataOutputStream;
import android.util.*;

public class Server
{


	int port;
	ArrayList<Socket> clients=new ArrayList<Socket>();
	Socket client=null;
	MainActivity GUI;
	ServerSocket sersock=null;

	public Server(int portnr, MainActivity pGUI)
	{
		GUI = pGUI;
		port = portnr;
		verbinde.start();
		kommuniziere.start();


	}
	public void stop()
	{
		kommuniziere.stop();
		try
		{
			sersock.close();
		}
		catch (IOException e)
		{}
		sersock=null;

	}
	Thread kommuniziere=new Thread()
	{

		@Override
		public void run()
		{
			// TODO: Implement this method
			int Fehler=0;
			while (Fehler == 0)

			{  
				for (Socket s: clients)
				{
					try
					{
						
						int nr=0;
						if (s!=null) nr=s.getInputStream().available();
						if (nr > 0)
						{  
							byte[] array=new byte[nr];
							try
							{

								//dis.read(array);
								s.getInputStream().read(array, 0, nr);
								for (Socket s2: clients)
								{ 
											if (s2!=null)s2.getOutputStream().write(array, 0, nr);
								}
							}
							catch (IOException e)
							{ 
								GUI.runOnUiThread(new Runnable()
									{
										@Override
										public void run()
										{  
											new Alarm(GUI, "Server Fehler bei read oder write");
										}
									});
							}
						}
					}
					catch (IOException e)
					{
						//new Alarm(GUI,"kein Outputstream");
						
						 GUI.runOnUiThread(new Runnable()
						 {
						 @Override
						 public void run()
						 {  
						 new Alarm(GUI, "Server Fehler bei available");
						 }
						 });
						 Fehler=1;

					}
				}
			}
		}

	};
	Thread verbinde= new Thread()
	{

		@Override
		public void run()
		{
			// TODO: Implement this method
			int x=0;
			
			int Fehler=0; 

			try
			{ 
				sersock = new ServerSocket(port);

				//new Alarm(GUI, "Server gestartet");
				GUI.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{  
							new Alarm(GUI, "Server gestartet Port:" + Integer.toString(port));
						}
					});
				Log.i("Serverdebug", "Server gestartet");

			}
			catch (IOException e)
			{ 
				//new Alarm(GUI, "Fehler bei der Erzeugung des Serversockets");
				GUI.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{  
							new Alarm(GUI, "Fehler bei der Erzeugung des Serversockets");
						}
					});
			}
			while (Fehler == 0)
			{   
				x = x + 1;

				try
				{   Socket s=sersock.accept();
					clients.add(s);
					//new Alarm(GUI, "Client angemeldet");
					GUI.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{  
								new Alarm(GUI, "Neuer Klient angemeldet");
							}
						});
				}
				catch (IOException e)
				{
					//new Alarm(GUI, "Acce=ptfehler");
					GUI.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{  
								new Alarm(GUI, "Fehler bei accept");
							}
						});
					Fehler = 1;
				}
				catch (Exception e)
				{

				}


				//super.run();
			}
		}

	};

}
