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
import org.xml.sax.helpers.*;
import java.util.function.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;



public class MainActivity extends Activity 
{
	List<String> addresses;
	OutputStreamWriter out;
	TextView eigeneIP;
	EditText et;
	EditText et2;
	Button btn;
	InetAddress myAdr;
	Server derServer;
	Socket klient=null;
	int port=25555;
	String ip;
	MainActivity GUI;
	String botschaft="";
	String rbotschaft=""; 
	byte[] sendung;
	paeckchen rPaeckchen;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//	if (hoeren!=null)hoeren.stop();
		//	if (status !=null)status.stop();
		if (derServer != null) derServer.stop();
		derServer = null;

		GUI = this;
		eigeneIP = (TextView)findViewById(R.id.mainTextView1);
		eigeneIP.setText("");
		et = (EditText)findViewById(R.id.mainEditText1);
		et2 = (EditText)findViewById(R.id.mainEditText2);
		btn = (Button) findViewById(R.id.mainButton1);

		btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{

					clickIP();
				}
			});
		// The following code loops through the available network interfaces
        // Keep in mind, there can be multiple interfaces per device, for example
        // one per NIC, one per active wireless and the loopback
        // In this case we only care about IPv4 address ( x.x.x.x format )
        addresses = new ArrayList<String>();
        try
		{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces))
			{
                for (InetAddress address : Collections.list(ni.getInetAddresses()))
                {
                    if (address instanceof Inet4Address)
					{
                        addresses.add(address.getHostAddress());
						eigeneIP.append(address.getHostAddress() + "\r\n");
						//String s =address.getHostName();

                    }
                }
            }
        }
		catch (SocketException e)
		{
			new Alarm(this,  e.getMessage());
        }
		String ip="127.0.0.1";
		if (addresses.get(1) != null) 
		{	
			ip = addresses.get(1).toString();

		}
		et.setText(ip);
    	derServer = new Server(port, this);
		hoeren.start();
		status.start();
	}

	Thread anmelden= new Thread()
	{

		@Override
		public void run()
		{
			// TODO: Implement this method
			try
			{
				klient = new Socket(ip, port);
				runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{  
							new Alarm(GUI, "Klient angemeldet");
						}
					});

			}
			catch (NumberFormatException e)
			{
				//new Alarm(this, "Portnummer muss VZ sein");
				runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{  
							new Alarm(GUI, "Portnummer muss VZ sein");
						}
					});
			}
			catch (IOException e)
			{
				//new Alarm(this,"new Socket fehlgeschlagen");
				runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{  
							new Alarm(GUI, "new Socket fehlgeschlagen");
						}
					});
			}
		}

	};
	public void clickIP()
	{
		ip = et.getText().toString();
		anmelden.start();
	}


	public void ausgeben(String s)
	{
		EditText ausgabe=findViewById(R.id.mainEditText4);
		ausgabe.setText(s);
	}

	public void senden(View v)
	{
		paeckchen dasPaeckchen = new paeckchen(et2.getText().toString(), (( EditText) findViewById(R.id.mainEditText3)).getText().toString());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try
		{
			out = new ObjectOutputStream(bos);   
			out.writeObject(dasPaeckchen);
			sendung = bos.toByteArray();
			out.close();
		}
		catch (IOException ex)
		{

		}

		verschicken.start();

	}
	Thread verschicken=new Thread()
	{

		@Override
		public void run()
		{

			try
			{
				int nr=sendung.length;
				klient.getOutputStream().write(sendung, 0, nr);
			}
			catch (IOException e)
			{
				runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{  
							new Alarm(GUI, "senden fehlgeschlagen " + klient.isConnected());
						}
					});
			}	
		}

	};

	Thread hoeren =new Thread()
	{

		@Override
		public void run()
		{
			String fromServer="";

			while (fromServer != "Bye")
			{  
				if (klient != null)
				{  
					try
					{

						int nr=klient.getInputStream().available();
						if (nr > 0)
						{  
							byte[] array=new byte[nr];
							klient.getInputStream().read(array, 0, nr);

							ByteArrayInputStream bis = new ByteArrayInputStream(array);
							//bytearray als stream
							ObjectInput in = null;
							try
							{
								in = new ObjectInputStream(bis); //deserialize
								try
								{
									rPaeckchen = (paeckchen) in.readObject();
								}
								catch (ClassNotFoundException e)
								{}
								catch (IOException e)
								{} 
							} 

							catch (IOException ex)
							{

							}
							
							rbotschaft = Integer.toString(rPaeckchen.getNr()) + ".) " + rPaeckchen.getNick() + ": " + rPaeckchen.getBotschaft();
							runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{  
										((EditText)findViewById(R.id.mainEditText4)).setText(((EditText)findViewById(R.id.mainEditText4)).getText() + rbotschaft + "\r\n");
									}
								});
						}
					}
					catch (IOException e)
					{

					}


				}
				try
				{
					sleep(20);
				}
				catch (InterruptedException e)
				{}
			}
		}

	};
	Thread status=new Thread()
	{

		@Override
		public void run()
		{
			while (true)
			{
				if (klient == null)
				{
					runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{  
								((TextView)findViewById(R.id.statustext)).setText("Status: Klient null");
								btn.setEnabled(true);
							}
						}); 
				}
				else
				{
					if (klient.isConnected())
					{
						runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{  
									((TextView)findViewById(R.id.statustext)).setText("Status: Klient verbunden");
									btn.setEnabled(false);
								}
							});
					}
					else
					{
						runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{  
									((TextView)findViewById(R.id.statustext)).setText("Status: Klient nicht verbunden");
									btn.setEnabled(true);
								}
							});
					}
				} 
				try
				{
					sleep(100);
				}
				catch (InterruptedException e)
				{}
			}
		}

	};

}
